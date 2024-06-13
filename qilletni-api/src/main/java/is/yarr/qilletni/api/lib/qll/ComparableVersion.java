package is.yarr.qilletni.api.lib.qll;

import java.util.Arrays;
import java.util.Optional;

/**
 * A {@link Version} that may be compared with others, fitting a range.
 */
public class ComparableVersion extends Version {

    private final RangeSpecifier rangeSpecifier;
    
    public ComparableVersion(int major, int minor, int patch, RangeSpecifier rangeSpecifier) {
        super(major, minor, patch);
        this.rangeSpecifier = rangeSpecifier;
    }
    
    public ComparableVersion(Version version, RangeSpecifier rangeSpecifier) {
        this(version.major(), version.minor(), version.patch(), rangeSpecifier);
    }

    /**
     * Parses a {@link ComparableVersion} from a string, such as {@code ^1.0.0}. If the version doesn't contain a range
     * specifier, then {@link RangeSpecifier#EXACT} is set.
     * 
     * @param versionString The version string to parse
     * @return The created {@link ComparableVersion}, if the string was valid
     */
    public static Optional<ComparableVersion> parseComparableVersionString(String versionString) {
        if (versionString.isEmpty()) {
            return Optional.empty();
        }
        
        var rangeSpecifierOptional = RangeSpecifier.getRangeSpecifierFromString(versionString.charAt(0));
        RangeSpecifier rangeSpecifier = rangeSpecifierOptional.orElse(RangeSpecifier.EXACT);
        
        if (rangeSpecifierOptional.isPresent()) {
            versionString = versionString.substring(1);
        }
        
        return Version.parseVersionString(versionString)
                .map(version -> new ComparableVersion(version, rangeSpecifier));
    }

    /**
     * Checks if the current {@link ComparableVersion} permits the given {@link Version} to be used in the current
     * range.
     * 
     * @param checkVersion The version to compare
     * @return If the version fits the bounds of the current {@link RangeSpecifier}
     */
    public boolean permitsVersion(Version checkVersion) {
        return rangeSpecifier.permitsVersion(this, checkVersion);
    }

    public enum RangeSpecifier {
        /**
         * Permits newer minor and match versions, so the {@code x} of {@code 1.x.x}.
         */
        CARET('^') {
            @Override
            public boolean permitsVersion(Version version, Version checkVersion) {
                if (checkVersion.major() != version.major()) {
                    return false;
                }
                
                if (checkVersion.minor() >= version.minor()) {
                    return true;
                }
                
                return checkVersion.patch() >= version.patch();
            }
        },
        /**
         * Permits newer patch versions, so the {@code x} of {@code 1.0.x}.
         */
        TILDE('~') {
            @Override
            public boolean permitsVersion(Version version, Version checkVersion) {
                if (checkVersion.major() != version.major() || checkVersion.minor() != version.minor()) {
                    return false;
                }

                return checkVersion.patch() >= version.patch();
            }
        },
        /**
         * Requires the exact version specified.
         */
        EXACT('\0') {
            @Override
            public boolean permitsVersion(Version version, Version checkVersion) {
                return !(checkVersion.major() != version.major() || checkVersion.minor() != version.minor()
                        || checkVersion.patch() != version.patch());
            }
        };
        
        private final char specifier;

        RangeSpecifier(char specifier) {
            this.specifier = specifier;
        }

        public static Optional<RangeSpecifier> getRangeSpecifierFromString(char specifierChar) {
            return Arrays.stream(values())
                    .filter(rangeSpecifier -> rangeSpecifier.specifier == specifierChar)
                    .findFirst();
        }

        /**
         * Checks if {@code #version} {@link ComparableVersion} will permit the given {@link Version} to be used in
         * accordance to the version's range specifier.
         * 
         * @param version The version to check against
         * @param checkVersion The version to check matches {@code version}
         * @return If the version may be used to fulfill the 
         */
        public abstract boolean permitsVersion(Version version, Version checkVersion);
    }

    @Override
    public String toString() {
        return "ComparableVersion{" +
                "v" + major() + "." + minor() + "." + patch() + ", " +
                "rangeSpecifier=" + rangeSpecifier +
                '}';
    }
}
