package dev.qilletni.lib.spotify.music.auth;

import se.michaelthelin.spotify.SpotifyApi;

public class SpotifyApiSingleton {
    
    private static SpotifyAuthorizer spotifyAuthorizer;

    public static SpotifyApi getSpotifyApi() {
        return spotifyAuthorizer.getSpotifyApi();
    }

    public static SpotifyAuthorizer getSpotifyAuthorizer() {
        return spotifyAuthorizer;
    }
    
    public static void setSpotifyAuthorizer(SpotifyAuthorizer spotifyApi) {
        SpotifyApiSingleton.spotifyAuthorizer = spotifyApi;
    }
}
