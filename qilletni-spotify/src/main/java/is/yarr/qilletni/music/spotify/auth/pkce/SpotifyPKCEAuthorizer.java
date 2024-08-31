package is.yarr.qilletni.music.spotify.auth.pkce;

import is.yarr.qilletni.async.ThrowableVoid;
import is.yarr.qilletni.music.spotify.SpotifyAuthUtility;
import is.yarr.qilletni.music.spotify.auth.SpotifyAuthorizer;
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
import se.michaelthelin.spotify.SpotifyApiThreading;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class SpotifyPKCEAuthorizer implements SpotifyAuthorizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpotifyPKCEAuthorizer.class);

    private static final String CLIENT_ID = System.getenv("CLIENT_ID");
    private static final URI REDIRECT_URI = SpotifyHttpManager.makeUri(System.getenv("REDIRECT_URI"));

    private static final SpotifyApi spotifyApi = new SpotifyApi.Builder()
            .setClientId(CLIENT_ID)
            .setRedirectUri(REDIRECT_URI)
            .build();

    private final String codeChallenge;
    private final String codeVerifier;
    private final PKCECredentialCache credentialCache;
    private final ExecutorService executorService;
    
    private se.michaelthelin.spotify.model_objects.specification.User currentUser;

    public SpotifyPKCEAuthorizer(String codeChallenge, String codeVerifier) {
        this.codeChallenge = codeChallenge;
        this.codeVerifier = codeVerifier;
        this.credentialCache = new PKCECredentialCache();
        this.executorService = Executors.newCachedThreadPool();
        
    }

    /**
     * Creates a {@link SpotifyPKCEAuthorizer} with generated code verifier and challenges.
     *
     * @return The created authorizer
     */
    public static SpotifyPKCEAuthorizer createWithCodes() {
        var codeVerifier = SpotifyAuthUtility.generateCodeVerifier(43, 128);
        var codeChallenge = SpotifyAuthUtility.generateCodeChallenge(codeVerifier);

        return new SpotifyPKCEAuthorizer(codeChallenge, codeVerifier);
    }

    /**
     * Populates {@link #spotifyApi} with correct access and refresh tokens, starting a loop to automatically refresh.
     *
     * @return The populated {@link SpotifyApi}
     */
    @Override
    public CompletableFuture<SpotifyApi> authorizeSpotify() {
        var completableFuture = new CompletableFuture<SpotifyApi>();

        try {
            try {
                if (credentialCache.hasCachedCredentials()) {
                    LOGGER.debug("Using cached credentials!");

                    refreshTokens(credentialCache.getCachedCredentials());
                    var credentials = performImmediateRefresh();
                    beginRefreshLoop(credentials.expiresIn());
                    updateCurrentUser().thenRun(() -> completableFuture.complete(spotifyApi));

                    return completableFuture;
                }
            } catch (IOException | ParseException | SpotifyWebApiException e) {
                LOGGER.debug("Exception occurred while using cached creds, resetting and trying again");
                credentialCache.resetCache();
            }
            
            LOGGER.debug("Manual creds!");

            getCodeFromUser().thenCompose(this::setupSpotifyApi)
                    .thenAccept(this::beginRefreshLoop)
                    .thenCompose($ -> updateCurrentUser())
                    .thenRun(() -> completableFuture.complete(spotifyApi));

        } catch (Exception e) {
            completableFuture.completeExceptionally(e);
        }

        return completableFuture;
    }

    @Override
    public void shutdown() {
        shutdownExecutorService(executorService);
        shutdownExecutorService(SpotifyApiThreading.THREAD_POOL);
    }
    
    private void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdownNow();

        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                LOGGER.debug("ExecutorService did not terminate in the specified time.");
            }
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted while waiting for termination.");
            Thread.currentThread().interrupt();
        }
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
          .scope("user-read-email,user-library-modify,user-library-read,user-read-playback-position,playlist-read-private,playlist-modify-private,playlist-modify-public")
//          .show_dialog(true)D
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
            
            credentialCache.writeCache(authorizationCodeCredentials.getRefreshToken());

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
        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(calculateExpirySleepTime(initialExpiresIn));

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        var authCodeCredentials = performImmediateRefresh();

                        LOGGER.info("Expires in {}s", authCodeCredentials.expiresIn());

                        Thread.sleep(calculateExpirySleepTime(authCodeCredentials.expiresIn()));
                    } catch (IOException | SpotifyWebApiException | ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, executorService);
    }

    /**
     * Updates the {@link #spotifyApi} with the new {@link AuthCodeCredentials} values.
     *
     * @param authCodeCredentials The {@link AuthCodeCredentials} to update with
     */
    private void refreshTokens(AuthCodeCredentials authCodeCredentials) {
        synchronized (spotifyApi) {
            spotifyApi.setAccessToken(authCodeCredentials.accessToken());
            spotifyApi.setRefreshToken(authCodeCredentials.refreshToken());
        }
    }

    /**
     * Immediately refreshes the tokens and updates {@link #spotifyApi}.
     *
     * @return The refreshed credentials
     */
    private AuthCodeCredentials performImmediateRefresh() throws IOException, ParseException, SpotifyWebApiException {
        var authorizationCodePKCERefreshRequest = spotifyApi.authorizationCodePKCERefresh().build();

        var authorizationCodeCredentials = authorizationCodePKCERefreshRequest.execute();

        var authCodeCredentials = AuthCodeCredentials.from(authorizationCodeCredentials);
        refreshTokens(authCodeCredentials);

        credentialCache.writeCache(authCodeCredentials.refreshToken());

        return authCodeCredentials;
    }

    /**
     * Sets the {@link #currentUser} to the current user that has been authenticated.
     * 
     * @return The future of the user request
     */
    private CompletableFuture<Void> updateCurrentUser() {
        return spotifyApi.getCurrentUsersProfile().build().executeAsync()
                .thenAccept(user -> currentUser = user);
    }

    @Override
    public SpotifyApi getSpotifyApi() {
        return spotifyApi;
    }

    @Override
    public Optional<User> getCurrentUser() {
        return Optional.ofNullable(currentUser);
    }

    /**
     * Calculates the time to sleep between token refresh iterations, based off of the previous token expiry time.
     *
     * @param expiresInSeconds The time in seconds the token will expire in
     * @return The millisecond time to wait
     */
    private long calculateExpirySleepTime(int expiresInSeconds) {
        if (expiresInSeconds == 0) {
            return 0;
        }

        return (expiresInSeconds - 10) * 1000L;
    }

    public record AuthCodeCredentials(String accessToken, String refreshToken, int expiresIn) {
        static AuthCodeCredentials from(AuthorizationCodeCredentials authorizationCodeCredentials) {
            return new AuthCodeCredentials(authorizationCodeCredentials.getAccessToken(), authorizationCodeCredentials.getRefreshToken(), authorizationCodeCredentials.getExpiresIn());
        }

        public static AuthCodeCredentials fromRefreshToken(String refreshToken) {
            return new AuthCodeCredentials("", refreshToken, -1);
        }
    }
}
