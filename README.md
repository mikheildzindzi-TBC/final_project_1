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

Switching between desktop and mobile execution is a **testng.xml parameter change**
(`<parameter name="profile" value="mobile"/>`) or a **`-Dprofile` flag** — never a
source-code edit. See `config/SelenideConfig.java` and `tests/BaseTest.java`.

---

## 3.1 Why Automate This?

| Scenario | Risk if broken | Business value | Why automation (not manual) fits |
|---|---|---|---|
| Homepage loads / logo & nav render | Total site failure = zero conversions, brand damage on first impression | Homepage is the highest-traffic, highest-visibility page on the site | Simple, deterministic pass/fail check that's cheap to run on every deploy — ideal smoke test |
| Locations map displays | Users can't find a branch/ATM → walk-in traffic loss, support calls | Directly drives foot traffic to physical branches, a core conversion path for a bank | Map rendering is a common regression point (3rd-party map SDK, script load order) — worth checking every build, tedious to do manually every time |
| Locations filter (Branch/ATM) | Wrong or empty results erode trust in the tool and waste the user's time | Filtering is the main interaction on the page — most users don't want all 300+ locations | Involves interaction + result-count assertion — repeatable, exact, and easy to regress silently (e.g. a filter that silently returns 0 results) |
| Loan calculator monthly payment | Wrong estimate is a **trust and compliance risk** for a bank — could mislead a borrower | Calculator is a lead-generation and self-service tool that reduces branch/call-center load | High-risk of silent numeric regression (rounding, currency formatting) that's invisible without an assertion; manual testers rarely re-check every input combination |
| Currency rates table | Stale/wrong rates mislead customers making real financial decisions | High-traffic utility page, often used daily by the same users | Data-freshness and rendering checks are exactly the kind of "did the numbers still show up" regression automation catches cheaply and often |
| Mobile nav collapses to burger menu | Broken mobile nav locks out the majority of banking-app traffic (increasingly mobile-first) | Protects the mobile experience specifically, which is easy to break with a desktop-only manual test pass | Requires comparing two viewports side-by-side — tedious and inconsistent to do by hand every regression cycle, trivial to automate once |

Common thread: every scenario above is either **high-traffic**, **financially
sensitive**, or **easy to silently regress** (JS errors, script load races,
CSS breakpoint changes) — the three signals that make a scenario worth the
cost of writing and maintaining automation, rather than leaving it to
periodic manual spot-checks.

---

## 3.2 Selector Strategy

**Locator priority used throughout this project** (highest to lowest preference):

1. **`data-testid` / `aria-label`** — most stable, explicitly meant for tooling, survives redesigns and copy changes. Used where confirmed present (e.g. burger menu button).
2. **Visible text (`Condition.text`, `Condition.exactText`)** — stable for a bank site because legal/marketing copy changes less often than CSS class names, and it doubles as a light content check. Used for the Locations filter tabs (matched on their actual Georgian labels).
3. **Stable functional attributes** (`min`/`max` on the loan calculator's number inputs) — used where the framework generates random per-load `id`s (e.g. `id="tbcx-text-input-9d3ccb2c-..."`), making `id`/`name` matching useless, but a functional attribute (the field's numeric range) stays constant across reloads.
4. **Component/library CSS class names** (`.tbcx-pw-tab-menu__item`, `.tbcx-pw-atm-branches-section__list-item`, `.tbcx-pw-popular-currency-item`, `.tbcx-pw-calculated-info__number`) — the whole site is an Angular SPA built entirely from a custom `tbcx-pw-*` component library, not semantic HTML (no native `<table>`, no plain `<button>` without a library class, no `<input type="range">`). Because these class names come from a shared component library rather than a hashed CSS-in-JS build, they're markedly more stable than typical utility classes — the same `tbcx-pw-*` prefix and BEM-style suffix (`__list-item`, `__number`) recurs across every page on the site, so a redesign is more likely to be a library-wide version bump (which would break everything at once, and be caught immediately) than a silent one-page class rename.

### Key selectors, how they were confirmed, and what could break them

| Selector | How confirmed | Why (relatively) stable | What could break it |
|---|---|---|---|
| `[aria-label='burger-menu-alt-outlined']` (mobile burger menu) | Confirmed navigation structure | `aria-label` is an accessibility contract, unlikely to be removed | A rebrand of the icon library changing the aria-label string |
| Nav link `Condition.exactText("...")` | Confirmed navigation structure | Menu label text is user-facing content, rarely churns | Localization/copy update, or EN/KA language toggle changing the text mid-suite |
| `button.tbcx-pw-tab-menu__item` filtered by text (Locations Branch/ATM filter) | Live DevTools inspection | Matches the site's own component class plus real visible label text, not a guessed attribute | A copy change to the Georgian tab labels, or the tab-menu component being swapped for a different one |
| `div.tbcx-pw-atm-branches-section__list-item` (Locations result cards) | Live DevTools inspection | Direct match on the actual result-row wrapper rendered by `<app-atm-branches-section-list-item>` | A version bump of that specific Angular component renaming its output classes |
| `tbcx-pw-popular-currency-item` (Currency Rates rows) | Live DevTools inspection | There's no native `<table>` on this page at all — matches the actual custom element the rates widget renders | Currency-rates widget being replaced with a different component |
| `input[type='number'][min='200'][max='80000']` (loan amount) / `input[type='number'][min='3'][max='48']` (loan duration) | Live DevTools inspection | The field's `id` is a random UUID regenerated per page load, so `min`/`max` (which encode the actual business range shown on the slider) are the only stable functional attributes available | The bank changing the minimum/maximum loan amount or term, which would also change the product itself, not just the markup |
| `.tbcx-pw-calculated-info__number` filtered to the visible one (monthly payment result) | Live DevTools inspection | Matches the shared base class of a digit-flip animation with two sibling containers (`--old`/`--new`); filtering to `Condition.visible` handles either one being shown without caring which | A redesign of the result-display animation to a different DOM pattern entirely |

---

## 3.3 Flaky Test Awareness

**Test selected:** `LocationsMapDisplayTest.locationsMapIsVisible`

**Why this one is the most flake-prone in the suite:**

1. **Third-party map SDK load timing.** The map is very likely rendered by an
   embedded map provider (Google Maps or similar) loaded asynchronously after
   the initial page HTML. A plain `Selenide.open()` + immediate assertion
   would race the map's own JS initialization.
   *Mitigation:* `waitForMapToRender()` uses an explicit `shouldBe(visible, timeout)`
   with a longer, dedicated `MAP_RENDER_TIMEOUT` (15s) rather than reusing the
   default 10s timeout, and rather than a hard `Thread.sleep()`.

2. **Geolocation permission prompts.** "Nearby" location features often try to
   request the browser's geolocation, which can pop a native browser dialog
   that Selenide can't interact with and that blocks the page.
   *Mitigation:* ChromeOptions should explicitly deny the geolocation
   permission prompt at driver-creation time (recommended follow-up: add
   `"profile.default_content_setting_values.geolocation": 2` to Chrome prefs
   in `DriverSetup`) so the test never depends on a dialog being dismissed.

3. **Cookie-consent banner overlapping the map.** If the cookie banner isn't
   dismissed before interacting with the map area, clicks/asserts can hit the
   banner instead of the map underneath it.
   *Mitigation:* every page object calls `dismissCookieBannerIfPresent()`
   immediately after `open()`, before any other interaction.

4. **Shared static `Configuration` under parallel execution** (suite-level
   flakiness, not just this test). Selenide's `Configuration.browserSize` and
   related fields are static/JVM-global. Running desktop and mobile `<test>`
   blocks in parallel (`parallel="tests"`) means two threads write to that
   static state close together in time; there's a narrow window where a
   thread's driver could be created with the *other* thread's browser size.
   *Mitigation:* the profile is set and the browser opened as the first two
   actions in `@BeforeClass`, minimizing the race window, and the one test
   that needs a guaranteed side-by-side comparison
   (`MobileResponsiveNavTest`) avoids the shared static config entirely by
   using two independent `SelenideDriver` instances instead.

5. **Observed in practice:** across repeated test runs during development,
   `LocationsMapDisplayTest`'s mobile-profile execution flipped between pass
   and fail with no code change in between — direct evidence this is a
   genuine timing-sensitive flake rather than a broken selector (unlike the
   other failures this suite hit, which were consistent every run until the
   selector itself was fixed).

---

## 3.4 Mobile is not Desktop

**Scenario:** Primary site navigation (`MobileResponsiveNavTest`)

- **Desktop (1440x900):** top-level nav items ("სესხები", "ბარათები", "ანაბრები",
  etc.) render inline in the header with hover-triggered dropdown submenus.
  The user never needs an extra click to see the top-level categories.
- **Mobile (390x844):** that same set of links collapses behind a single
  burger-menu icon. The test asserts the **inverse** conditions on each
  viewport — burger menu absent/inline-nav-present on desktop, and
  burger-menu-present on mobile — rather than just checking "does *a* nav
  exist," which would pass even if mobile had silently kept (and broken) the
  desktop layout.

**Different assertions needed, not just a resized window:**
- Desktop asserts nav **links are directly clickable** without an extra step.
- Mobile asserts a **burger menu control exists and is the entry point**,
  and (in `BasePage.openNavItem`) that clicking it reveals a menu panel
  before the link becomes clickable — an extra interaction step that has no
  desktop equivalent and would silently be skipped if the test only ever ran
  in a desktop-shaped window.
- Mobile uses real Chrome **device emulation** (touch events + mobile
  user-agent via `MobileEmulation.chromeMobileCapabilities()`), not just a
  390px-wide desktop Chrome window — many responsive breakpoints and touch-
  only interactions don't activate from a resize alone, since some CSS/JS
  keys off `navigator.userAgent` or touch-event support, not just viewport
  width.

**UX risk if this regresses silently:** a majority of retail banking traffic
is mobile. If the burger menu breaks (e.g. becomes unclickable, or the panel
doesn't open) users lose access to every primary section of the site with no
visible desktop-side symptom — the kind of regression a desktop-only manual
test pass would never catch.

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
testng.xml         # parallel="tests", desktop + mobile profiles
```
