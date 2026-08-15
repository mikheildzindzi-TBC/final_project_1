package ge.tbc.testautomation.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

/**
 * "მისამართები" (Locations) page elements: interactive map + Branch/ATM filter
 * + result list.
 */
public class LocationsPage extends BasePage {

    public final SelenideElement mapContainer = $("[class*='map'], #map, div[id*='gmap']");
    public final SelenideElement branchFilterToggle = $$("button.tbcx-pw-tab-menu__item")
            .filter(Condition.text("ფილიალები"))
            .first();
    public final SelenideElement atmFilterToggle = $$("button.tbcx-pw-tab-menu__item")
            .filter(Condition.text("ბანკომატები"))
            .first();

    public final ElementsCollection resultCards = $$("div.tbcx-pw-atm-branches-section__list-item");
    public final SelenideElement searchInput = $("input[type='search'], input[placeholder*='მისამართი'], input[placeholder*='Search']");
}