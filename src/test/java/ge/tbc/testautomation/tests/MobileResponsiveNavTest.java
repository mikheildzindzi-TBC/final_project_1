package ge.tbc.testautomation.tests;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import ge.tbc.testautomation.data.Constants;
import ge.tbc.testautomation.pages.BasePage;
import ge.tbc.testautomation.util.MobileEmulation;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Behavior under test: on desktop the primary navigation links are rendered
 * directly in the header; on mobile (390x844) they collapse behind a burger
 * menu button instead.
 */
public class MobileResponsiveNavTest {

    private SelenideDriver desktopDriver;
    private SelenideDriver mobileDriver;

    @Test(description = "E2E: user lands on the homepage - nav is inline on desktop but collapses to a burger menu on mobile",
            groups = {"navigation", "mobile-vs-desktop", "e2e"})
    public void navCollapsesToBurgerMenuOnMobileOnly() {
        SelenideConfig desktopConfig = new SelenideConfig();
        desktopConfig.browserSize(Constants.DESKTOP_SIZE);
        desktopConfig.headless(true);
        desktopDriver = new SelenideDriver(desktopConfig);

        SelenideConfig mobileConfig = new SelenideConfig();
        mobileConfig.browserSize(Constants.MOBILE_SIZE);
        mobileConfig.headless(true);
        mobileConfig.browserCapabilities(MobileEmulation.chromeMobileCapabilities());
        mobileDriver = new SelenideDriver(mobileConfig);

        desktopDriver.open(Constants.HOME_PATH);
        // The header is an Angular SPA component - it isn't present in the DOM
        // immediately after open() returns (open() only waits for the initial
        // page load event, not client-side hydration). Wait for something in
        // the header to actually render before reading nav/burger state,
        // otherwise these checks race the SPA and can read a not-yet-rendered DOM.
        desktopDriver.$("tbcx-pw-logo").shouldBe(Condition.exist);
        boolean burgerVisibleOnDesktop = desktopDriver.$(BasePage.BURGER_MENU_SELECTOR).exists()
                && desktopDriver.$(BasePage.BURGER_MENU_SELECTOR).isDisplayed();
        boolean inlineNavVisibleOnDesktop = desktopDriver.$(BasePage.PRIMARY_NAV_LINKS_SELECTOR).exists();

        mobileDriver.open(Constants.HOME_PATH);
        mobileDriver.$("tbcx-pw-logo").shouldBe(Condition.exist);
        // Waiting for the logo to exist is not the same as waiting for the burger
        // button specifically - the header can hydrate incrementally, so the logo
        // being present doesn't guarantee the burger button has rendered yet. That
        // gap was enough for the immediate exists()/isDisplayed() snapshot below to
        // read "not there yet" and fail. Wait on the burger button itself first.
        mobileDriver.$(BasePage.BURGER_MENU_SELECTOR).shouldBe(Condition.visible);
        boolean burgerVisibleOnMobile = mobileDriver.$(BasePage.BURGER_MENU_SELECTOR).exists()
                && mobileDriver.$(BasePage.BURGER_MENU_SELECTOR).isDisplayed();

        assertFalse(burgerVisibleOnDesktop, "Burger menu should NOT be the primary nav control at 1440x900");
        assertTrue(inlineNavVisibleOnDesktop, "Desktop header should expose nav links directly");
        assertTrue(burgerVisibleOnMobile, "Burger menu should be shown as the primary nav control at 390x844");
    }

    @AfterMethod(alwaysRun = true)
    public void closeDrivers() {
        if (desktopDriver != null) desktopDriver.close();
        if (mobileDriver != null) mobileDriver.close();
    }
}
