package ge.tbc.testautomation.pages;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

/**
 * Shared elements every page inherits: the cookie consent banner
 * (tbcbank.ge shows one on first visit) and the mobile burger menu,
 * which replaces the top nav on the 390x844 profile.
 */
public abstract class BasePage {

    public static final String COOKIE_ACCEPT_SELECTOR = ".tbcx-pw-cookie-consent__actions button.primary";

    public static final String BURGER_MENU_SELECTOR = "tbcx-pw-hamburger-menu button";

    public static final String MOBILE_MENU_PANEL_SELECTOR = "tbcx-pw-mega-menu .tbcx-pw-mega-menu";

    // Primary desktop nav: <tbcx-pw-navigation class="show-desktop-only ..."><div class="tbcx-pw-navigation">
    public static final String PRIMARY_NAV_LINKS_SELECTOR = "tbcx-pw-navigation a";

    public final SelenideElement cookieAcceptButton = $(COOKIE_ACCEPT_SELECTOR);
    public final SelenideElement burgerMenuButton = $(BURGER_MENU_SELECTOR);
    public final SelenideElement mobileMenuPanel = $(MOBILE_MENU_PANEL_SELECTOR);
}
