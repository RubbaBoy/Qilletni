package dev.qilletni.api.lib.qll;

import java.util.Objects;
import java.util.Optional;

/**
 * A version of a Qilletni library.
 */
public class Version {
    
    private final int major;
    private final int minor;
    private final int patch;

    /**
     * Creates a new {@link Version} with the given major, minor, and patch versions.
     * 
     * @param major The major version
     * @param minor The minor version
     * @param patch The patch version
     */
    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    /**
     * Parses a {@link Version} from a string, such as {@code 1.0.0}.
     * 
     * @param versionString The version string to parse
     * @return The created {@link Version}, if the string was valid
     */
    public static Optional<Version> parseVersionString(String versionString) {
        var versionSplit = versionString.split("\\.");

        if (versionSplit.length != 3) {
            return Optional.empty();
        }
        
        return Optional.of(new Version(Integer.parseInt(versionSplit[0]), Integer.parseInt(versionSplit[1]), Integer.parseInt(versionSplit[2])));
    }

    /**
     * Gets the major version of the {@link Version}.
     * 
     * @return The major version
     */
    public int major() {
        return major;
    }

    /**
     * Gets the minor version of the {@link Version}.
     * 
     * @return The minor version
     */
    public int minor() {
        return minor;
    }

    /**
     * Gets the patch version of the {@link Version}.
     * 
     * @return The patch version
     */
    public int patch() {
        return patch;
    }

    /**
     * Gets the version string of the {@link Version}, such as {@code 1.0.0}.
     * 
     * @return The version string
     */
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
