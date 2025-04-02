package dev.qilletni.lib.spotify;

import dev.qilletni.api.lang.types.DoubleType;
import dev.qilletni.api.lang.types.EntityType;
import dev.qilletni.api.lang.types.IntType;
import dev.qilletni.api.lang.types.ListType;
import dev.qilletni.api.lang.types.QilletniType;
import dev.qilletni.api.lang.types.SongType;
import dev.qilletni.api.lang.types.StringType;
import dev.qilletni.api.lang.types.list.ListInitializer;
import dev.qilletni.api.lang.types.typeclass.QilletniTypeClass;
import dev.qilletni.api.lib.annotations.NativeOn;
import dev.qilletni.api.music.Track;
import dev.qilletni.api.music.factories.SongTypeFactory;
import dev.qilletni.lib.spotify.exceptions.NoSeedSetException;
import dev.qilletni.lib.spotify.music.SpotifyMusicFetcher;
import dev.qilletni.lib.spotify.music.auth.SpotifyApiSingleton;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RecommendationsFunctions {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecommendationsFunctions.class);

    private final SpotifyApi spotifyApi;
    private final ListInitializer listInitializer;
    private final SongTypeFactory songTypeFactory;

    public RecommendationsFunctions(ListInitializer listInitializer, SongTypeFactory songTypeFactory) {
        this.listInitializer = listInitializer;
        this.songTypeFactory = songTypeFactory;
        spotifyApi = SpotifyApiSingleton.getSpotifyApi();
    }

    private <T> void computerIfPresent(EntityType entity, String propertyName, Consumer<T> propertyConsumer) {
        if (!entity.getEntityScope().isDefined(propertyName)) {
            return;
        }

        var lookedUpValue = entity.getEntityScope().lookup(propertyName).getValue();
        if (lookedUpValue instanceof IntType intType && intType.getValue() != -999) {
            propertyConsumer.accept((T) Integer.valueOf(Long.valueOf(intType.getValue()).intValue()));
            return;
        }

        if (lookedUpValue instanceof DoubleType doubleType && doubleType.getValue() != -999) {
            propertyConsumer.accept((T) Float.valueOf(Double.valueOf(doubleType.getValue()).floatValue()));
            return;
        }

        if (lookedUpValue instanceof ListType listType && !listType.getItems().isEmpty()) {
            propertyConsumer.accept((T) listType);
        }
    }

    private boolean entityHasSet(EntityType entity, String propertyName) {
        return entity.getEntityScope().isDefined(propertyName);
    }

    @NativeOn("Recommender")
    public ListType recommend(EntityType entity, int trackCount) throws IOException, ParseException, SpotifyWebApiException {
        var recommendations = spotifyApi.getRecommendations();

        var anySeedSet = new AtomicBoolean(false);

        computerIfPresent(entity, "seedArtists", (ListType seedArtists) -> {
            var seedArtistIds = seedArtists.getItems().stream().map(EntityType.class::cast).map(artist ->
                            artist.getEntityScope().lookup("id").getValue().toString())
                    .collect(Collectors.joining(","));
            
            LOGGER.debug("Seed artists: {}", seedArtistIds);

            anySeedSet.set(true);
            recommendations.seed_artists(seedArtistIds);
        });

        computerIfPresent(entity, "seedGenres", (ListType seedGenres) -> {
            var seedGenreIds = seedGenres.getItems().stream().map(StringType.class::cast).map(StringType::getValue)
                    .collect(Collectors.joining(","));
            
            LOGGER.debug("Seed genres: {}", seedGenreIds);

            anySeedSet.set(true);
            recommendations.seed_genres(seedGenreIds);
        });

        computerIfPresent(entity, "seedTracks", (ListType seedTracks) -> {
            var seedTrackIds = seedTracks.getItems().stream().map(SongType.class::cast).map(SongType::getTrack).map(Track::getId)
                    .collect(Collectors.joining(","));

            LOGGER.debug("Seed tracks: {}", seedTrackIds);

            anySeedSet.set(true);
            recommendations.seed_tracks(seedTrackIds);
        });

        if (!anySeedSet.get()) {
            throw new NoSeedSetException("No seed set for recommendations");
        }

        recommendations.limit(trackCount);

        computerIfPresent(entity, "maxAcousticness", recommendations::min_acousticness);
        computerIfPresent(entity, "minAcousticness", recommendations::max_acousticness);
        computerIfPresent(entity, "targetAcousticness", recommendations::target_acousticness);

        computerIfPresent(entity, "maxDanceability", recommendations::min_danceability);
        computerIfPresent(entity, "minDanceability", recommendations::max_danceability);
        computerIfPresent(entity, "targetDanceability", recommendations::target_danceability);

        computerIfPresent(entity, "maxDurationMs", recommendations::min_duration_ms);
        computerIfPresent(entity, "minDurationMs", (Integer max_duration_ms) -> {
            System.out.println("max_duration_ms = " + max_duration_ms);
            recommendations.max_duration_ms(max_duration_ms);
        });
        computerIfPresent(entity, "targetDurationMs", recommendations::target_duration_ms);

        computerIfPresent(entity, "maxEnergy", recommendations::min_energy);
        computerIfPresent(entity, "minEnergy", recommendations::max_energy);
        computerIfPresent(entity, "targetEnergy", recommendations::target_energy);

        computerIfPresent(entity, "maxInstrumentalness", recommendations::min_instrumentalness);
        computerIfPresent(entity, "minInstrumentalness", recommendations::max_instrumentalness);
        computerIfPresent(entity, "targetInstrumentalness", recommendations::target_instrumentalness);

        computerIfPresent(entity, "maxKey", recommendations::min_key);
        computerIfPresent(entity, "minKey", recommendations::max_key);
        computerIfPresent(entity, "targetKey", recommendations::target_key);

        computerIfPresent(entity, "maxLiveness", recommendations::min_liveness);
        computerIfPresent(entity, "minLiveness", recommendations::max_liveness);
        computerIfPresent(entity, "targetLiveness", recommendations::target_liveness);

        computerIfPresent(entity, "maxMode", recommendations::min_mode);
        computerIfPresent(entity, "minMode", recommendations::max_mode);
        computerIfPresent(entity, "targetMode", recommendations::target_mode);

        computerIfPresent(entity, "maxPopularity", recommendations::min_popularity);
        computerIfPresent(entity, "minPopularity", recommendations::max_popularity);
        computerIfPresent(entity, "targetPopularity", recommendations::target_popularity);

        computerIfPresent(entity, "maxSpeechiness", recommendations::min_speechiness);
        computerIfPresent(entity, "minSpeechiness", recommendations::max_speechiness);
        computerIfPresent(entity, "targetSpeechiness", recommendations::target_speechiness);

        computerIfPresent(entity, "maxTempo", recommendations::min_tempo);
        computerIfPresent(entity, "minTempo", recommendations::max_tempo);
        computerIfPresent(entity, "targetTempo", recommendations::target_tempo);

        computerIfPresent(entity, "maxTimeSignature", recommendations::min_time_signature);
        computerIfPresent(entity, "minTimeSignature", recommendations::max_time_signature);
        computerIfPresent(entity, "targetTimeSignature", recommendations::target_time_signature);

        computerIfPresent(entity, "maxValence", recommendations::min_valence);
        computerIfPresent(entity, "minValence", recommendations::max_valence);
        computerIfPresent(entity, "targetValence", recommendations::target_valence);

        var recommendedSongs = Arrays.stream(recommendations.build().execute().getTracks())
                .map(SpotifyMusicFetcher::createTrackEntity)
                .map(songTypeFactory::createSongFromTrack)
                .map(QilletniType.class::cast)
                .toList();

        return listInitializer.createList(recommendedSongs, QilletniTypeClass.SONG);
    }

}
