package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.api.lang.types.CollectionType;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimit;
import is.yarr.qilletni.api.lang.types.collection.CollectionLimitUnit;
import is.yarr.qilletni.api.lang.types.collection.CollectionOrder;
import is.yarr.qilletni.api.lang.types.weights.WeightEntry;
import is.yarr.qilletni.api.music.MusicCache;
import is.yarr.qilletni.api.music.PlayActor;
import is.yarr.qilletni.api.music.Track;
import is.yarr.qilletni.api.music.TrackOrchestrator;
import is.yarr.qilletni.api.music.orchestrator.weights.WeightUnit;
import is.yarr.qilletni.music.spotify.exceptions.InvalidWeightException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SpotifyTrackOrchestrator implements TrackOrchestrator {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyTrackOrchestrator.class);

    private final PlayActor playActor;
    private final MusicCache musicCache;

    public SpotifyTrackOrchestrator(PlayActor playActor, MusicCache musicCache) {
        this.playActor = playActor;
        this.musicCache = musicCache;
    }

    @Override
    public void playTrack(Track track) {
        playActor.playTrack(track);
    }
    
    /*
     * Notes regarding weights:
     * If a collection is sequential, weights are not used
     * Total weight % must not go over 100%
     * Weight % has higher priority than multiplier, obviously
     * Weight % is total % played, NOT in addition. So, tracks with % weights are removed from shuffled queues
     * If playing while looping, it will wait until the track queue is empty, so % weights may make it go on for longer than intended
     */

    @Override
    public void playCollection(CollectionType collectionType, boolean loop) {
        LOGGER.debug("Play collection: {}", collectionType.getPlaylist().getTitle());

        conditionallyPlayCollection(collectionType, loop, track -> {}, () -> true);
    }

    @Override
    public void playCollection(CollectionType collectionType, CollectionLimit collectionLimit) {
        LOGGER.debug("Play {} tracks from collection: {}", collectionType.getPlaylist().getTitle(), collectionLimit);
        
        if (collectionLimit.limitUnit() == CollectionLimitUnit.COUNT) {
            playCollectionCountLimited(collectionType, collectionLimit.limitCount(), true);
        } else {
            playCollectionTimeLimited(collectionType, calculateLimitMilliseconds(collectionLimit), true);
        }
    }
    
    private void playCollectionTimeLimited(CollectionType collectionType, long limitMilliseconds, boolean loop) {
        var totalTimePassed = new AtomicInteger();
        
        conditionallyPlayCollection(collectionType, loop, track -> totalTimePassed.addAndGet(track.getDuration()),
                () -> totalTimePassed.get() < limitMilliseconds);
    }
    
    private void playCollectionCountLimited(CollectionType collectionType, int count, boolean loop) {
        var totalTracksPlayed = new AtomicInteger();
        
        conditionallyPlayCollection(collectionType, loop, $ -> totalTracksPlayed.incrementAndGet(),
                () -> totalTracksPlayed.get() < count);
    }
    
    private void conditionallyPlayCollection(CollectionType collectionType, boolean loop, Consumer<Track> playCallback, Supplier<Boolean> shouldPlayTrack) {
        var shuffle = collectionType.getOrder() == CollectionOrder.SHUFFLE;

        var tracks = musicCache.getPlaylistTracks(collectionType.getPlaylist());

        if (!shuffle) {
            tracks.forEach(playActor::playTrack);
            return;
        }

        int totalWeightPercentage = validateWeights(collectionType);
        var weightDispersion = calculateWeightDispersion(collectionType, totalWeightPercentage);

        tracks = prunePercentageWeightedTracks(tracks, collectionType);
        
        Queue<Track> trackQueue = applyWeights(tracks, collectionType);

        while (!trackQueue.isEmpty() && shouldPlayTrack.get()) {
            var weightedTrackOptional = chooseWeightedTrack(weightDispersion);
            
            if (weightedTrackOptional.isPresent()) {
                weightedTrackOptional.ifPresent(weightedTrack -> {
                    playCallback.accept(weightedTrack);
                    playActor.playTrack(weightedTrack);
                });
            } else {
                var playingTrack = trackQueue.poll();
                playCallback.accept(playingTrack);
                playActor.playTrack(playingTrack);

                if (trackQueue.isEmpty() && loop) {
                    trackQueue = applyWeights(tracks, collectionType);
                    trackQueue = ensureNoBeginningDuplicate(trackQueue, playingTrack);
                    
                    LOGGER.debug("Reapplying weights to: {}", trackQueue);
                }
            }
        }
    }
    
    private Queue<Track> ensureNoBeginningDuplicate(Queue<Track> tracks, Track previouslyPlayed) {
        if (tracks.size() > 1 && tracks.peek() != null && tracks.peek().equals(previouslyPlayed)) {
            LOGGER.debug("Shuffled queue starts with previously played song, {}", previouslyPlayed.getName());
            var indexToPlace = ThreadLocalRandom.current().nextInt(0, tracks.size() - 2) + 1;
            
            LOGGER.debug("Will place at {}", indexToPlace);

            tracks.poll();
            
            var newTracks = new LinkedList<Track>();
            for (int i = 0; i < indexToPlace; i++) {
                newTracks.add(tracks.poll());
            }
            
            newTracks.add(previouslyPlayed);

            for (int i = 0; i < tracks.size(); i++) {
                newTracks.add(tracks.poll());
            }
            
            LOGGER.debug("New shuffled queue = {}", newTracks.stream().map(Track::getName).collect(Collectors.joining(", ")));
            
            return newTracks;
        }
        
        return tracks;
    }

    /**
     * Calculates the number of milliseconds the {@link CollectionLimit} represents. If the unit is of type
     * {@link CollectionLimitUnit#COUNT}, -1 is returned.
     * 
     * @param collectionLimit The collection limit to calculate
     * @return The number of milliseconds
     */
    private long calculateLimitMilliseconds(CollectionLimit collectionLimit) {
        return collectionLimit.limitUnit().getTimeUnit().toMillis(collectionLimit.limitCount());
    }

    /**
     * Calculates with weights what weighted track to choose, returning an empty optional if a track should be selected
     * from the queue.
     * 
     * @return The track to play, if from weighted
     */
    private List<Optional<Track>> calculateWeightDispersion(CollectionType collectionType, int totalWeightPercentage) {
        var weights = collectionType.getWeights();

        var dispersion = new ArrayList<Optional<Track>>();
        if (weights != null) {
            dispersion.addAll(weights.getWeightEntries().stream()
                    .filter(entry -> entry.getWeightUnit() == WeightUnit.PERCENT)
                    .flatMap(weightEntry -> IntStream.range(0, weightEntry.getWeightAmount())
                            .mapToObj($ -> Optional.of(weightEntry.getSong().getTrack())))
                    .toList());
        }
        
        dispersion.addAll(IntStream.range(0, 100 - totalWeightPercentage)
                .mapToObj($ -> Optional.<Track>empty()).toList());
        
        return Collections.unmodifiableList(dispersion);
    }

    /**
     * Choose a random track from the dispersion.
     * 
     * @param weightDispersion The list of possible weighted tracks
     * @return The chosen track, if any
     */
    private Optional<Track> chooseWeightedTrack(List<Optional<Track>> weightDispersion) {
        var num = ThreadLocalRandom.current().nextInt(0, weightDispersion.size() - 1);
        return weightDispersion.get(num);
    }
    
    /**
     * Checks if the weights are valid, and returns the total percentage used up by weights.
     * 
     * @param collectionType The collection holding the weights
     * @return The total weighted percentage
     */
    private int validateWeights(CollectionType collectionType) {
        var weights = collectionType.getWeights();
        
        if (weights == null) {
            return 0;
        }

        int totalPercent = weights.getWeightEntries().stream()
                .filter(entry -> entry.getWeightUnit() == WeightUnit.PERCENT)
                .map(WeightEntry::getWeightAmount)
                .reduce(Integer::sum)
                .orElse(0);
        
        if (totalPercent > 100) {
            throw new InvalidWeightException("Total weight percentage cannot go over 100%! Current total is " + totalPercent + "%");
        }
        
        return totalPercent;
    }
    
    private List<Track> prunePercentageWeightedTracks(List<Track> tracks, CollectionType collectionType) {
        var weights = collectionType.getWeights();

        if (weights == null) {
            return tracks;
        }

        var removeTracks = weights.getWeightEntries().stream()
                .filter(entry -> entry.getWeightUnit() == WeightUnit.PERCENT)
                .map(entry -> entry.getSong().getTrack())
                .toList();
        
        var trackCopy = new ArrayList<>(tracks);
        trackCopy.removeIf(removeTracks::contains);
        
        LOGGER.debug("Pruned from {}", tracks.stream().map(Track::getName).collect(Collectors.joining(", ")));
        LOGGER.debug("to {}", trackCopy.stream().map(Track::getName).collect(Collectors.joining(", ")));
        
        return trackCopy;
    }
    
    private Queue<Track> applyWeights(List<Track> tracks, CollectionType collectionType) {
        var weights = collectionType.getWeights();
        
        LinkedList<Track> trackQueue;
        if (weights != null) {
            var trackWeightMap = weights.getWeightEntries().stream()
                    .filter(entry -> entry.getWeightUnit() == WeightUnit.MULTIPLIER)
                    .collect(Collectors.toMap(entry -> entry.getSong().getTrack(), entry -> entry.getWeightAmount() - 1));
            
            LOGGER.debug("trackWeightMap = {}", trackWeightMap);
            
            trackQueue = tracks.stream()
                    .flatMap(track -> IntStream.range(0, trackWeightMap.getOrDefault(track, 0) + 1).mapToObj($ -> track))
                    .collect(Collectors.toCollection(LinkedList::new));
            
            LOGGER.debug("trackQueue = {}", trackQueue.stream().map(Track::getName).collect(Collectors.joining(", ")));
        } else {
            trackQueue = new LinkedList<>(tracks);
        }
        
        Collections.shuffle(trackQueue);
        
        return trackQueue;
    }
}
