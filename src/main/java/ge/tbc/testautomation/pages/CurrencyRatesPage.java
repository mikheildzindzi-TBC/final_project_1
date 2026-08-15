package ge.tbc.testautomation.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;

/**
 * "ვალუტის კურსები" (Currency Rates) page elements - a simple, low-risk read-only table.
 */
public class CurrencyRatesPage extends BasePage {

    public final ElementsCollection rateRows = $$("tbcx-pw-popular-currency-item");
    public final SelenideElement usdRow = $$("tbcx-pw-popular-currency-item")
            .filter(Condition.text("USD"))
            .first();
}