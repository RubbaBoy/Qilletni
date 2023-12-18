package is.yarr.qilletni.music.spotify;

import is.yarr.qilletni.music.async.ThrowableVoid;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.net.URIBuilder;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class SpotifyAuthorizer {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyAuthorizer.class);

    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri(System.getenv("REDIRECT_URI"));

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .build();

    private final String codeChallenge;
    private final String codeVerifier;

    public SpotifyAuthorizer(String codeChallenge, String codeVerifier) {
        this.codeChallenge = codeChallenge;
        this.codeVerifier = codeVerifier;
    }

    /**
     * Creates a {@link SpotifyAuthorizer} with generated code verifier and challenges.
     * 
     * @return The created authorizer
     */
    public static SpotifyAuthorizer createWithCodes() {
        var codeVerifier = SpotifyAuthUtility.generateCodeVerifier(43, 128);
        var codeChallenge = SpotifyAuthUtility.generateCodeChallenge(codeVerifier);

        return new SpotifyAuthorizer(codeChallenge, codeVerifier);
    }

    /**
     * Populates {@link #spotifyApi} with correct access and refresh tokens, starting a loop to automatically refresh.
     * 
     * @return The populated {@link SpotifyApi}
     */
    public CompletableFuture<SpotifyApi> authorizeSpotify() {
        var completableFuture = new CompletableFuture<SpotifyApi>();
        
        try {
            getCodeFromUser().thenCompose(this::setupSpotifyApi)
                    .thenAccept(this::beginRefreshLoop)
                    .thenRun(() -> completableFuture.complete(spotifyApi));
        } catch (Exception e) {
            completableFuture.completeExceptionally(e);
        }
        
        return completableFuture;
    }

    /**
     * Opens the Spotify authentication URL and gets the authentication code from the callback of it. 
     * 
     * @return The authentication code future
     * @throws Exception
     */
    private CompletableFuture<String> getCodeFromUser() throws Exception {
        var authorizationCodeUriRequest = spotifyApi.authorizationCodePKCEUri(codeChallenge)
//          .state("x4xkmn9pu3j6ukrs8n")
//          .scope("user-read-birthdate,user-read-email")
//          .show_dialog(true)
                .build();

        authorizationCodeUriRequest.executeAsync().thenAccept(uri -> {
            try {
                Desktop.getDesktop().browse(uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        var codeFuture = new CompletableFuture<String>();

        var server = setupCallbackServer(codeFuture);
        server.join();

        return codeFuture;
    }

    /**
     * Sets up the server on port 8088 and completes the future with the {@code code} query parameter. 
     * 
     * @param codeFuture The future to complete with the code
     * @return The created server
     * @throws Exception
     */
    private Server setupCallbackServer(CompletableFuture<String> codeFuture) throws Exception {
        var server = new Server(8088); // Set your desired port
        server.setHandler(new Handler.Abstract() {
            @Override
            public boolean handle(Request request, Response response, Callback callback) throws Exception {
                if (!request.getHttpURI().getPath().equals("/spotify")) {
                    return false;
                }

                var code = new URIBuilder(request.getHttpURI().toURI()).getFirstQueryParam("code").getValue();

                response.setStatus(200);
                response.write(true, ByteBuffer.wrap("Thank you! You may close this tab.\n".getBytes(StandardCharsets.UTF_8)), callback);

                server.stop();

                codeFuture.complete(code);
                return true;
            }
        });

        server.start();
        return server;
    }

    /**
     * Sets up the spotify API and returns how many seconds until it expires.
     * 
     * @param code The code given by the redirect URL
     * @return The future of the request
     */
    private CompletableFuture<Integer> setupSpotifyApi(String code) {
        var authorizationCodePKCERequest = spotifyApi.authorizationCodePKCE(code, codeVerifier).build();

        return authorizationCodePKCERequest.executeAsync().thenApply(authorizationCodeCredentials -> {
            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());

            LOGGER.info("Expires in {}s", authorizationCodeCredentials.getExpiresIn());
            
            return authorizationCodeCredentials.getExpiresIn();
        }).exceptionally(new ThrowableVoid<>("Exception while getting access and refresh tokens", 0));
    }

    /**
     * Starts a loop on another thread to continuously refresh the token in {@link #spotifyApi} before it's going to be expired.
     * 
     * @param initialExpiresIn
     */
    private void beginRefreshLoop(int initialExpiresIn) {
        var authorizationCodePKCERefreshRequest = spotifyApi.authorizationCodePKCERefresh().build();
        
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(calculateExpirySleepTime(initialExpiresIn));

                while (true) {
                    try {
                        var authorizationCodeCredentials = authorizationCodePKCERefreshRequest.execute();
                        
                        synchronized (spotifyApi) {
                            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
                            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
                        }
                        
                        LOGGER.info("Expires in {}s", authorizationCodeCredentials.getExpiresIn());
                        
                        Thread.sleep(calculateExpirySleepTime(authorizationCodeCredentials.getExpiresIn()));
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
    
    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    /**
     * Calculates the time to sleep between token refresh iterations, based off of the previous token expiry time.
     * 
     * @param expiresInSeconds The time in seconds the token will expire in
     * @return The millisecond time to wait
     */
    private long calculateExpirySleepTime(int expiresInSeconds) {
        if (expiresInSeconds == 0) {
            return  0;
        }
        
        return (expiresInSeconds - 10) * 1000L;
    }
}
