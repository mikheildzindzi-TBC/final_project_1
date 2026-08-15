package ge.tbc.testautomation.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$$;

/**
 * Homepage elements. Actions (open, click quick links, etc.) live in steps.NavigationSteps.
 */
public class HomePage extends BasePage {

    public final SelenideElement logo =
            $$("tbcx-pw-logo a[href='/ka']").filter(Condition.visible).first();
    public final SelenideElement locationsQuickLink =
            $$("a[href*='atms&branches']").filter(Condition.visible).first();
    public final SelenideElement loanCalculatorQuickLink =
            $$(".tbcx-pw-carousel__card a[href='/ka/loans']").filter(Condition.visible).first();
    public final SelenideElement currencyRatesQuickLink =
            $$(".tbcx-pw-carousel__card a[href='/ka/valutis-kursi']").filter(Condition.visible).first();
}
