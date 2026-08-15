package ge.tbc.testautomation.data;

/**
 * Represents which viewport/behavior profile the current test is running under.
 */
public enum ExecutionProfile {
    DESKTOP(Constants.DESKTOP_SIZE),
    MOBILE(Constants.MOBILE_SIZE);

    private final String size;

    ExecutionProfile(String size) {
        this.size = size;
    }

    public String size() {
        return size;
    }

    public static ExecutionProfile fromString(String raw) {
        if (raw == null || raw.isBlank()) {
            return DESKTOP; // safe default
        }
        return raw.trim().equalsIgnoreCase(Constants.PROFILE_MOBILE) ? MOBILE : DESKTOP;
    }
}
