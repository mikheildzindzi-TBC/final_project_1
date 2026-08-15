package ge.tbc.testautomation.steps;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.pages.CurrencyRatesPage;
import ge.tbc.testautomation.pages.HomePage;

import static com.codeborne.selenide.Selenide.open;

/**
 * "ვალუტის კურსები" (Currency Rates) page steps - a simple, low-risk read-only table, good smoke check.
 */
public class CurrencyRatesSteps extends BaseSteps {

    private final CurrencyRatesPage ratesPage = new CurrencyRatesPage();

    @Override
    public CurrencyRatesPage page() {
        return ratesPage;
    }

    public CurrencyRatesSteps userOpensCurrencyRatesPage() {
        HomePage homePage = new HomePage();
        open(Constants.HOME_PATH);
        dismissCookieBannerIfPresent();
        safeClick(homePage.currencyRatesQuickLink.shouldBe(Condition.visible));
        ratesPage.rateRows.shouldHave(CollectionCondition.sizeGreaterThan(0));
        return this;
    }

    public boolean isUsdRateDisplayed() {
        return ratesPage.usdRow.is(Condition.visible);
    }

    public int currencyRowCount() {
        return ratesPage.rateRows.filter(Condition.visible).size();
    }
}
