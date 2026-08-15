# TBC Bank UI Regression Suite — README

**Target:** https://www.tbcbank.ge
**Stack:** Java 17 + Selenide 7.5 + TestNG 7.10 + WebDriverManager
**Profiles:** Desktop (1440x900) and Mobile (390x844, Chrome mobile emulation)

## Running the suite

```bash
mvn test                        # runs testng.xml as-is (desktop + mobile in parallel)
mvn test -Dprofile=mobile       # forces mobile profile where a class reads -Dprofile directly
mvn test -Dheadless=false       # watch the browser locally
```

Switching profiles is a `testng.xml` parameter change or a `-Dprofile` flag — never a source-code edit.

---

## 3.1 Why Automate This?

| Scenario | Risk if broken | Why automation fits |
|---|---|---|
| Homepage loads | Total site failure, zero conversions | Cheap, deterministic smoke test on every deploy |
| Locations map displays | Lost walk-in traffic, support calls | Map SDK/script-load races are a common silent regression |
| Locations filter (Branch/ATM) | Wrong/empty results erode trust | Repeatable interaction + count assertion, easy to silently regress |
| Loan calculator payment | Wrong estimate = compliance/trust risk | Numeric regressions are invisible without an assertion |
| Currency rates display | Stale rates mislead customers | High-traffic page; cheap to check every build |
| Mobile nav collapses to burger menu | Locks out majority-mobile banking traffic | Tedious to check both viewports by hand every cycle |

Common thread: every scenario is high-traffic, financially sensitive, or easy to silently regress — worth the cost of automation over periodic manual checks.

---

## 3.2 Selector Strategy

**Priority (highest to lowest):**
1. `data-testid` / `aria-label` — most stable, survives redesigns
2. Visible text (`Condition.text`) — used for the Locations filter tabs
3. Stable functional attributes (e.g. `min`/`max`) — used where `id` is a random UUID per page load, as on the loan calculator
4. Component library class names (`.tbcx-pw-*`) — the site is an Angular SPA built entirely on a custom `tbcx-pw-*` component library, so there's no native `<table>`, plain `<button>`, or `<input type="range">` to target; these classes come from the shared library rather than hashed CSS-in-JS, so they're more stable than typical utility classes

**Key selectors:**

| Selector | Confirmed via | What could break it |
|---|---|---|
| `[aria-label='burger-menu-alt-outlined']` | Confirmed nav structure | Icon library rebrand |
| Nav link `Condition.exactText(...)` | Confirmed nav structure | Copy/localization change |
| `button.tbcx-pw-tab-menu__item` filtered by text (Locations filter) | Live DevTools | Georgian label copy change |
| `div.tbcx-pw-atm-branches-section__list-item` (result cards) | Live DevTools | Component version bump |
| `tbcx-pw-popular-currency-item` (rates rows) | Live DevTools | Widget replaced entirely |
| `input[type='number'][min='200'][max='80000']` / `[min='3'][max='48']` (loan fields) | Live DevTools | Bank changing the actual min/max loan terms |
| `.tbcx-pw-calculated-info__number` filtered to visible (payment result) | Live DevTools | Result-display redesign |

---

## 3.3 Flaky Test Awareness

**Test:** `LocationsMapDisplayTest.locationsMapIsVisible`

1. **Map SDK load timing** — async third-party map init can race a plain assertion. *Mitigation:* dedicated `MAP_RENDER_TIMEOUT` (15s) explicit wait, no `Thread.sleep()`.
2. **Geolocation prompts** — a native browser dialog would block the page. *Mitigation (follow-up):* deny geolocation via ChromeOptions prefs at driver creation.
3. **Cookie banner overlap** — could intercept clicks meant for the map. *Mitigation:* `dismissCookieBannerIfPresent()` runs right after every `open()`.
4. **Shared static Selenide `Configuration` under parallel execution** — desktop/mobile threads writing browser-size config close together creates a narrow race window. *Mitigation:* profile set and browser opened first in `@BeforeClass`; `MobileResponsiveNavTest` sidesteps this with two independent `SelenideDriver` instances.

---

## 3.4 Mobile is not Desktop

**Test:** `MobileResponsiveNavTest`

- **Desktop:** nav links render inline with hover dropdowns, directly clickable.
- **Mobile:** nav collapses behind a burger menu; test asserts the *inverse* conditions per viewport (not just "a nav exists"), and that the burger reveals a menu panel before links become clickable.
- Mobile uses real Chrome device emulation (touch + user-agent), not just a resized window — some CSS/JS keys off `navigator.userAgent`/touch support, not viewport width alone.

**Risk if this regresses silently:** most banking traffic is mobile — a broken burger menu locks users out of the whole site with no desktop-visible symptom.

---

## Project structure

```
src/
├── main/java/ge/tbc/testautomation/
│   ├── config/    # SelenideConfig — profile-driven browser configuration
│   ├── data/      # Constants (URLs, timeouts) + ExecutionProfile enum
│   ├── pages/     # Page Objects — selectors/elements ONLY, no actions
│   ├── steps/     # Actual steps — clicks, typing, waits, business actions
│   └── util/      # Mobile emulation, thread-safe profile context
└── test/java/ge/tbc/testautomation/
    └── tests/     # TestNG test classes ONLY (5+ automated scenarios)
testng.xml          # parallel="tests", desktop + mobile profiles
```

## Known limitations / follow-ups

- Geolocation permission auto-deny (3.3) not yet wired into `SelenideConfig`.
- `LocationsMapDisplayTest`'s mobile-profile run has shown intermittent pass/fail across runs with no code change — worth revisiting after the geolocation fix.