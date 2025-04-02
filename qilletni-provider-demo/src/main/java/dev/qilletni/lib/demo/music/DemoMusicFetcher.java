package dev.qilletni.lib.demo.music;

import dev.qilletni.api.music.Album;
import dev.qilletni.api.music.Artist;
import dev.qilletni.api.music.MusicFetcher;
import dev.qilletni.api.music.Playlist;
import dev.qilletni.api.music.Track;
import dev.qilletni.lib.demo.music.entities.DemoAlbum;
import dev.qilletni.lib.demo.music.entities.DemoArtist;
import dev.qilletni.lib.demo.music.entities.DemoPlaylist;
import dev.qilletni.lib.demo.music.entities.DemoTrack;
import dev.qilletni.lib.demo.music.entities.DemoUser;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class DemoMusicFetcher implements MusicFetcher {

    private final static String RAND_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHUJKLMNOPQRSTUVWXYZ";
    
    @Override
    public Optional<Track> fetchTrack(String name, String artist) {
        return Optional.of(createRandomTrack(name, artist));
    }

    @Override
    public Optional<Track> fetchTrackById(String id) {
        return Optional.of(createRandomTrack(id));
    }

    @Override
    public List<Track> fetchTracks(List<TrackNameArtist> tracks) {
        return tracks.stream().map(trackNameArtist -> createRandomTrack(trackNameArtist.name(), trackNameArtist.artist())).toList();
    }

    @Override
    public List<Track> fetchTracksById(List<String> trackIds) {
        return trackIds.stream().map(this::createRandomTrack).toList();
    }

    @Override
    public Optional<Playlist> fetchPlaylist(String name, String author) {
        return Optional.of(new DemoPlaylist(generateRandomString(), name, 10, new DemoUser(generateRandomString(), author)));
    }

    @Override
    public Optional<Playlist> fetchPlaylistById(String id) {
        return Optional.of(new DemoPlaylist(id, generateRandomString(), 10, new DemoUser(generateRandomString(), generateRandomString())));
    }

    @Override
    public Optional<Album> fetchAlbum(String name, String artist) {
        return Optional.of(new DemoAlbum(generateRandomString(), name, List.of(new DemoArtist(generateRandomString(), artist))));
    }

    @Override
    public Optional<Album> fetchAlbumById(String id) {
        return Optional.of(new DemoAlbum(id, generateRandomString(), List.of(new DemoArtist(generateRandomString(), generateRandomString()))));
    }

    @Override
    public List<Track> fetchAlbumTracks(Album album) {
        return IntStream.range(0, 5).mapToObj($ -> createRandomTrack(generateRandomString())).toList();
    }

    @Override
    public List<Track> fetchPlaylistTracks(Playlist playlist) {
        return IntStream.range(0, 5).mapToObj($ -> createRandomTrack(generateRandomString())).toList();
    }

    @Override
    public Optional<Artist> fetchArtistByName(String name) {
        return Optional.of(new DemoArtist(generateRandomString(), name));
    }

    @Override
    public Optional<Artist> fetchArtistById(String id) {
        return Optional.of(new DemoArtist(id, generateRandomString()));
    }

    private Track createRandomTrack(String id) {
        return new DemoTrack(id, generateRandomString(10), List.of(fetchArtistById(generateRandomString(6)).get()), fetchAlbumById(generateRandomString(6)).get(), 100);
    }

    private Track createRandomTrack(String name, String artist) {
        return new DemoTrack(generateRandomString(6), name, List.of(fetchArtistByName(artist).get()), fetchAlbumById(generateRandomString(6)).get(), 100);
    }

    private String generateRandomString() {
        return generateRandomString(6);
    }
    
    private String generateRandomString(int length) {
        var stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(RAND_CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(RAND_CHARACTERS.length() - 1)));
        }
        
        return stringBuilder.toString();
    }
}
