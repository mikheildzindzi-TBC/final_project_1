package ge.tbc.testautomation.steps;

import com.codeborne.selenide.Condition;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.pages.HomePage;
import ge.tbc.testautomation.pages.LoanCalculatorPage;

import static com.codeborne.selenide.Selenide.executeJavaScript;
import static com.codeborne.selenide.Selenide.open;

/**
 * E2E: journey starts on the homepage and reaches the loan calculator via the
 * homepage's Loans quick link (real user path), instead of deep-linking straight
 * to the Consumer Loan URL.
 */
public class LoanSteps extends BaseSteps {

    private final LoanCalculatorPage loanPage = new LoanCalculatorPage();

    @Override
    public LoanCalculatorPage page() {
        return loanPage;
    }

    public LoanSteps userOpensLoanCalculator() {
        HomePage homePage = new HomePage();
        open(Constants.HOME_PATH);
        dismissCookieBannerIfPresent();
        safeClick(homePage.loanCalculatorQuickLink.shouldBe(Condition.visible));
        return this;
    }

    public LoanSteps userEntersLoanAmount(String amount) {
        loanPage.amountInput.shouldBe(Condition.visible).setValue(amount);
        return this;
    }

    public LoanSteps userSetsDurationInMonths(int months) {
        loanPage.durationSlider.shouldBe(Condition.visible);
        executeJavaScript(
                "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('input', {bubbles: true}));",
                loanPage.durationSlider, months);
        return this;
    }

    public String monthlyPaymentShownToUser() {
        return loanPage.monthlyPaymentResult.shouldBe(Condition.visible).getText();
    }
}
