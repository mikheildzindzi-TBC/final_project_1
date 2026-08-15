package ge.tbc.testautomation.util;

import ge.tbc.testautomation.data.ExecutionProfile;

/**
 * Holds the current test's execution profile (DESKTOP/MOBILE) per thread.
 */
public final class ProfileContext {

    private static final ThreadLocal<ExecutionProfile> CURRENT = ThreadLocal.withInitial(() -> ExecutionProfile.DESKTOP);

    private ProfileContext() {
    }

    public static void set(ExecutionProfile profile) {
        CURRENT.set(profile);
    }

    public static ExecutionProfile get() {
        return CURRENT.get();
    }

    public static boolean isMobile() {
        return CURRENT.get() == ExecutionProfile.MOBILE;
    }

    public static void clear() {
        CURRENT.remove();
    }
}
