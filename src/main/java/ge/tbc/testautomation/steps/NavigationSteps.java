package ge.tbc.testautomation.steps;

import com.codeborne.selenide.Condition;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.pages.CurrencyRatesPage;
import ge.tbc.testautomation.pages.HomePage;
import ge.tbc.testautomation.pages.LocationsPage;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.open;

public class NavigationSteps extends BaseSteps {

    private final HomePage homePage = new HomePage();

    @Override
    public HomePage page() {
        return homePage;
    }

    public NavigationSteps userLandsOnHomePage() {
        open(Constants.HOME_PATH);
        dismissCookieBannerIfPresent();
        return this;
    }

    public boolean homePageLoadedSuccessfully() {
        // .is(Condition.visible) is an instant, non-retrying snapshot check - it
        // was firing right after open()/cookie-dismiss returns
        return homePage.logo.shouldBe(Condition.visible, Duration.ofMillis(Constants.DEFAULT_TIMEOUT))
                .is(Condition.visible);
    }

    public LocationsPage userNavigatesToLocationsFromHome() {
        safeClick(homePage.locationsQuickLink.shouldBe(Condition.visible));
        return new LocationsPage();
    }

    public CurrencyRatesPage userNavigatesToCurrencyRatesFromHome() {
        safeClick(homePage.currencyRatesQuickLink.shouldBe(Condition.visible));
        return new CurrencyRatesPage();
    }

    public boolean mobileBurgerMenuIsShown() {
        return isBurgerMenuVisible();
    }
}
