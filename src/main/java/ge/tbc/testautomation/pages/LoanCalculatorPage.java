package ge.tbc.testautomation.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * Consumer loan page elements - includes the monthly-payment calculator widget.
 */
public class LoanCalculatorPage extends BasePage {

    public final SelenideElement amountInput = $("input[type='number'][min='200'][max='80000']");
    public final SelenideElement durationSlider = $("input[type='number'][min='3'][max='48']");
    public final SelenideElement monthlyPaymentResult = $$(".tbcx-pw-calculated-info__number")
            .filter(Condition.visible)
            .first();
}