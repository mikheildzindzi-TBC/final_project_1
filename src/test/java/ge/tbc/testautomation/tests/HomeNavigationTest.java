package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.steps.NavigationSteps;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/** E2E entry point: every other journey in this suite begins from this homepage load. */
public class HomeNavigationTest extends BaseTest {

    private final NavigationSteps navigationSteps = new NavigationSteps();

    @Test(description = "E2E: user lands on homepage and the TBC logo/header renders",
            groups = {"smoke", "navigation", "e2e"})
    public void homePageLoadsSuccessfully() {
        navigationSteps.userLandsOnHomePage();
        assertTrue(navigationSteps.homePageLoadedSuccessfully(), "Homepage logo should be visible after load");
    }
}
