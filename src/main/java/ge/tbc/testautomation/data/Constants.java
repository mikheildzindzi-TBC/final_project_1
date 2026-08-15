package ge.tbc.testautomation.data;

/**
 * Central place for URLs, timeouts and other static values.
 */
public final class Constants {

    private Constants() {
    }

    public static final String BASE_URL = "https://www.tbcbank.ge/ka";

    public static final String HOME_PATH = "https://tbcbank.ge/ka";

    // Timeouts (ms)
    public static final long DEFAULT_TIMEOUT = 10_000;
    public static final long PAGE_LOAD_TIMEOUT = 20_000;
    public static final long MAP_RENDER_TIMEOUT = 15_000;

    // Execution profiles, driven by testng.xml <parameter name="profile">
    public static final String PROFILE_DESKTOP = "desktop";
    public static final String PROFILE_MOBILE = "mobile";

    public static final String DESKTOP_SIZE = "1440x900";
    public static final String MOBILE_SIZE = "390x844";
}
