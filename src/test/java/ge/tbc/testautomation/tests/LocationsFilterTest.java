package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.steps.LocationsSteps;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Zephyr scenario: "As a user, I want to filter locations by Branch or ATM
 * to quickly narrow down the results."
 * E2E journey: homepage -> click Locations quick link -> map loads -> apply filter -> results narrow.
 */
public class LocationsFilterTest extends BaseTest {

    @Test(description = "E2E: user lands on homepage, navigates to Locations, filters by Branch, and sees results",
            groups = {"locations", "e2e"})
    public void filteringByBranchShowsResults() {
        LocationsSteps steps = new LocationsSteps();
        steps.userOpensLocationsPage().userFiltersByBranchOnly();
        assertTrue(steps.numberOfResultsShown() > 0, "Expected at least one branch result after filtering");
    }

    @Test(description = "E2E: user lands on homepage, navigates to Locations, filters by ATM, and sees results",
            groups = {"locations", "e2e"})
    public void filteringByAtmShowsResults() {
        LocationsSteps steps = new LocationsSteps();
        steps.userOpensLocationsPage().userFiltersByAtmOnly();
        assertTrue(steps.numberOfResultsShown() > 0, "Expected at least one ATM result after filtering");
    }
}
