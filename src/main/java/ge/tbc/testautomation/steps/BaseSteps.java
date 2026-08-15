package ge.tbc.testautomation.steps;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ge.tbc.testautomation.pages.BasePage;
import ge.tbc.testautomation.util.ProfileContext;
import org.openqa.selenium.ElementClickInterceptedException;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.executeJavaScript;

/**
 * Shared step actions every steps class inherits: dismissing the cookie banner
 * and opening a nav item (burger menu on mobile, direct link on desktop).
 * Pages only expose elements (see pages.BasePage) - the actual click/type/wait
 * logic that turns those elements into user actions lives here.
 */
public abstract class BaseSteps {

    /** Each concrete steps class returns the page whose elements it acts on. */
    protected abstract BasePage page();

    /**
     * Clicks a quick-link card reliably.
     */
    protected void safeClick(SelenideElement element) {
        element.scrollIntoView("{block: 'center'}");
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            executeJavaScript("arguments[0].click();", element);
        }
    }

    public void dismissCookieBannerIfPresent() {
        BasePage page = page();

        if (page.cookieAcceptButton.is(Condition.visible, Duration.ofSeconds(5))) {
            page.cookieAcceptButton.click();
            page.cookieAcceptButton.shouldNot(Condition.exist);
        }
    }

    /** Opens the given top-nav item, using the burger menu on mobile and a direct link on desktop. */
    public void openNavItem(String visibleLinkText) {
        BasePage page = page();
        if (ProfileContext.isMobile()) {
            page.burgerMenuButton.shouldBe(Condition.visible).click();
            page.mobileMenuPanel.shouldBe(Condition.visible);
            $$("a").findBy(Condition.exactText(visibleLinkText)).shouldBe(Condition.visible).click();
        } else {
            $$("a").findBy(Condition.exactText(visibleLinkText)).shouldBe(Condition.visible).click();
        }
    }

    public boolean isBurgerMenuVisible() {
        BasePage page = page();
        return page.burgerMenuButton.exists() && page.burgerMenuButton.is(Condition.visible);
    }
}
