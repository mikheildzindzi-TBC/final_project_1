package ge.tbc.testautomation.steps;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.pages.HomePage;
import ge.tbc.testautomation.pages.LocationsPage;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.open;

/**
 * Step layer: translates page-object elements into named business actions.
 */
public class LocationsSteps extends BaseSteps {

    private final LocationsPage locationsPage = new LocationsPage();

    @Override
    public LocationsPage page() {
        return locationsPage;
    }

    public LocationsSteps userOpensLocationsPage() {
        HomePage homePage = new HomePage();
        open(Constants.HOME_PATH);
        dismissCookieBannerIfPresent();
        safeClick(homePage.locationsQuickLink.shouldBe(Condition.visible));
        locationsPage.mapContainer.shouldBe(Condition.visible, Duration.ofMillis(Constants.MAP_RENDER_TIMEOUT));
        return this;
    }

    public LocationsSteps userFiltersByBranchOnly() {
        locationsPage.branchFilterToggle.shouldBe(Condition.visible).click();
        return this;
    }

    public LocationsSteps userFiltersByAtmOnly() {
        locationsPage.atmFilterToggle.shouldBe(Condition.visible).click();
        return this;
    }

    public boolean mapIsVisibleToUser() {
        return locationsPage.mapContainer.is(Condition.visible);
    }

    public int numberOfResultsShown() {
        locationsPage.resultCards.shouldHave(CollectionCondition.sizeGreaterThan(0));
        return locationsPage.resultCards.filter(Condition.visible).size();
    }

    public LocationsSteps userSearchesByAddress(String query) {
        locationsPage.searchInput.shouldBe(Condition.visible).setValue(query).pressEnter();
        return this;
    }
}
