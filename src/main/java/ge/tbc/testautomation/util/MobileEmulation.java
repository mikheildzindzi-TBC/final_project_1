package ge.tbc.testautomation.util;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.MutableCapabilities;

import java.util.HashMap;
import java.util.Map;

/**
 * Builds Chrome DevTools mobile-emulation capabilities (device metrics + touch +
 * a mobile user-agent) for the 390x844 profile
 */
public final class MobileEmulation {

    private MobileEmulation() {
    }

    public static MutableCapabilities chromeMobileCapabilities() {
        Map<String, Object> deviceMetrics = new HashMap<>();
        deviceMetrics.put("width", 390);
        deviceMetrics.put("height", 844);
        deviceMetrics.put("pixelRatio", 3.0);

        Map<String, Object> mobileEmulation = new HashMap<>();
        mobileEmulation.put("deviceMetrics", deviceMetrics);
        mobileEmulation.put("userAgent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 17_5 like Mac OS X) AppleWebKit/605.1.15 "
                        + "(KHTML, like Gecko) Version/17.5 Mobile/15E148 Safari/604.1");

        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("mobileEmulation", mobileEmulation);
        options.addArguments("--force-device-scale-factor=3");
        return options;
    }
}
