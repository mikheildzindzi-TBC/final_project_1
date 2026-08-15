package ge.tbc.testautomation.tests;

import ge.tbc.testautomation.steps.LoanSteps;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Zephyr scenario: "As a user, I want to use the loan calculator to estimate
 * my monthly payment based on loan amount and duration."
 * E2E journey: homepage -> click Loans quick link -> enter amount/duration -> see estimate.
 */
public class LoanCalculatorTest extends BaseTest {

    @Test(description = "E2E: user lands on homepage, navigates to the loan calculator, and gets a monthly payment estimate",
            groups = {"loans", "e2e"})
    public void calculatorReturnsMonthlyPaymentEstimate() {
        LoanSteps steps = new LoanSteps();
        String monthlyPayment = steps.userOpensLoanCalculator()
                .userEntersLoanAmount("5000")
                .userSetsDurationInMonths(24)
                .monthlyPaymentShownToUser();

        assertFalse(monthlyPayment.isBlank(), "Monthly payment result should not be blank");
        assertTrue(monthlyPayment.matches(".*\\d.*"), "Monthly payment result should contain a numeric value, was: " + monthlyPayment);
    }
}
