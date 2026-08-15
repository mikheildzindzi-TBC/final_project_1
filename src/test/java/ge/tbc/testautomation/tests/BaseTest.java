package ge.tbc.testautomation.tests;

import com.codeborne.selenide.Selenide;
import ge.tbc.testautomation.config.SelenideConfig;
import ge.tbc.testautomation.data.ExecutionProfile;
import ge.tbc.testautomation.util.ProfileContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Every test class extends this. The "profile" parameter comes from testng.xml
 */
public abstract class BaseTest {

    @Parameters("profile")
    @BeforeClass(alwaysRun = true)
    public void setUpDriver(@Optional("desktop") String profileParam) {
        ExecutionProfile profile = SelenideConfig.resolveProfileFromParam(profileParam);
        SelenideConfig.configureForProfile(profile);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            Selenide.screenshot(result.getMethod().getMethodName() + "_FAILURE");
        }
        Selenide.closeWebDriver();
        ProfileContext.clear();
    }
}
