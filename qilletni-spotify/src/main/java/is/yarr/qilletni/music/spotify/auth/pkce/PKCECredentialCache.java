package is.yarr.qilletni.music.spotify.auth.pkce;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class PKCECredentialCache {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(PKCECredentialCache.class);

    private final static Path DEFAULT_PATH = Paths.get("creds");
    private final static Gson gson = new Gson();
    
    private final Path credPath;
    
    public PKCECredentialCache() {
        this(DEFAULT_PATH);
    }

    public PKCECredentialCache(Path basePath) {
        this.credPath = basePath.resolve("credentials.json");
    }
    
    public boolean hasCachedCredentials() {
        return Files.exists(credPath);
    }
    
    public SpotifyPKCEAuthorizer.AuthCodeCredentials getCachedCredentials() throws IOException {
        var jsonRefreshToken = gson.fromJson(Files.readString(credPath), JsonRefreshToken.class);
        return SpotifyPKCEAuthorizer.AuthCodeCredentials.fromRefreshToken(jsonRefreshToken.getRefreshToken());
    }
    
    public void writeCache(String refreshToken) {
        LOGGER.debug("Write cache: {} to: {}", refreshToken, credPath);
        try {
            if (!Files.exists(credPath.getParent())) {
                Files.createDirectories(credPath.getParent());
            }
            
            Files.deleteIfExists(credPath);
            Files.writeString(credPath, gson.toJson(new JsonRefreshToken(refreshToken)), StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void resetCache() {
        try {
            Files.delete(credPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static class JsonRefreshToken {
        private String refreshToken;

        private JsonRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
    
}
