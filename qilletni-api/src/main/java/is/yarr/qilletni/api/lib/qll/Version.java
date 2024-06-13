package is.yarr.qilletni.api.lib.qll;

import java.util.Objects;
import java.util.Optional;

public class Version {
    
    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    public static Optional<Version> parseVersionString(String versionString) {
        var versionSplit = versionString.split("\\.");

        if (versionSplit.length != 3) {
            return Optional.empty();
        }
        
        return Optional.of(new Version(Integer.parseInt(versionSplit[0]), Integer.parseInt(versionSplit[1]), Integer.parseInt(versionSplit[2])));
    }

    public int major() {
        return major;
    }

    public int minor() {
        return minor;
    }

    public int patch() {
        return patch;
    }
    
    public String getVersionString() {
        return "%d.%d.%d".formatted(major, minor, patch);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Version) obj;
        return this.major == that.major &&
                this.minor == that.minor &&
                this.patch == that.patch;
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch);
    }

    @Override
    public String toString() {
        return "Version[" +
                "major=" + major + ", " +
                "minor=" + minor + ", " +
                "patch=" + patch + ']';
    }

}
