package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.steps.CurrencyRatesSteps;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Zephyr scenario: "As a user, I want to see the current currency exchange
 * rates so I can check the USD rate." (Zephyr TC-06)
 */
public class CurrencyRatesTest extends BaseTest {

    @Test(description = "E2E: user lands on homepage, navigates to Currency Rates, and sees a USD row",
            groups = {"smoke", "rates", "e2e"})
    public void usdRateIsDisplayed() {
        CurrencyRatesSteps steps = new CurrencyRatesSteps().userOpensCurrencyRatesPage();
        assertTrue(steps.isUsdRateDisplayed(), "USD row should be visible in the currency rates table");
        assertTrue(steps.currencyRowCount() > 1, "Expected multiple currency rows to be listed");
    }
}
