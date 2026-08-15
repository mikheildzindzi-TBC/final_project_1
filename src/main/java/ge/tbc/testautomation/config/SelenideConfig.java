package ge.tbc.testautomation.config;

import com.codeborne.selenide.Configuration;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.data.ExecutionProfile;
import ge.tbc.testautomation.util.MobileEmulation;
import ge.tbc.testautomation.util.ProfileContext;

/**
 * Applies browser configuration for the current thread/profile.
 */
public final class SelenideConfig {

    private SelenideConfig() {
    }

    public static void configureForProfile(ExecutionProfile profile) {
        ProfileContext.set(profile);

        Configuration.browserSize = profile.size();
        Configuration.browser = "chrome";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("headless", "true"));
        Configuration.timeout = Constants.DEFAULT_TIMEOUT;
        Configuration.pageLoadTimeout = Constants.PAGE_LOAD_TIMEOUT;
        Configuration.reportsFolder = "target/selenide-reports";
        Configuration.screenshots = true;
        Configuration.savePageSource = false;

        if (profile == ExecutionProfile.MOBILE) {
            // Emulate a touch-capable narrow viewport, not just a resized desktop window,
            // so mobile-only UI (burger menu, sticky bottom nav, etc.) actually renders.
            Configuration.browserCapabilities = MobileEmulation.chromeMobileCapabilities();
        }
    }

    public static ExecutionProfile resolveProfileFromParam(String testngParam) {
        // Priority: explicit testng.xml <parameter> > -Dprofile system property > default desktop.
        // This is what satisfies "configurable for desktop and mobile execution without
        // source-code changes" - switching profiles is a testng.xml / -D flag change only.
        String raw = (testngParam != null && !testngParam.isBlank())
                ? testngParam
                : System.getProperty("profile", Constants.PROFILE_DESKTOP);
        return ExecutionProfile.fromString(raw);
    }
}
