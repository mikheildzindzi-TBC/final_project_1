package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.steps.LocationsSteps;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Zephyr scenario: "As a user, I want to see nearby branches and ATMs on a
 * map so I can find the closest location." (Locations scenario #1)
 * E2E journey: homepage -> click Locations quick link -> interactive map renders.
 */
public class LocationsMapDisplayTest extends BaseTest {

    private final LocationsSteps locationsSteps = new LocationsSteps();

    @Test(description = "E2E: user lands on homepage, navigates to Locations, and sees the interactive branch/ATM map",
            groups = {"locations", "e2e"})
    public void locationsMapIsVisible() {
        locationsSteps.userOpensLocationsPage();
        assertTrue(locationsSteps.mapIsVisibleToUser(), "Map container should be visible on Locations page");
    }
}
