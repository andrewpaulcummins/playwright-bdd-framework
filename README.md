# Playwright BDD Framework

> An enterprise-grade BDD test automation framework built with **Playwright for Java**, **Cucumber 7**, **JUnit 5**, and **PicoContainer** : the second of two parallel portfolio frameworks demonstrating deep automation architecture capability across both Selenium and Playwright ecosystems.

[![Allure Report](https://img.shields.io/badge/Allure-Report-brightgreen)](https://andrewpaulcummins.github.io/playwright-bdd-framework/)

[![CI Pipeline](https://github.com/andrewpaulcummins/playwright-bdd-framework/actions/workflows/ci.yml/badge.svg)](https://github.com/andrewpaulcummins/playwright-bdd-framework/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange)
![Playwright](https://img.shields.io/badge/Playwright-1.47.0-green)
![Cucumber](https://img.shields.io/badge/Cucumber-7.15.0-brightgreen)
![JUnit](https://img.shields.io/badge/JUnit-5.10.2-red)
![License](https://img.shields.io/badge/License-MIT-blue)

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Selenium vs Playwright : Framework Comparison](#2-selenium-vs-playwright--framework-comparison)
3. [Tech Stack](#3-tech-stack)
4. [Prerequisites](#4-prerequisites)
5. [Getting Started](#5-getting-started)
6. [Playwright Object Hierarchy](#6-playwright-object-hierarchy)
7. [Project Architecture](#7-project-architecture)
8. [Folder Structure](#8-folder-structure)
9. [Configuration](#9-configuration)
10. [Test Data & Encryption](#10-test-data--encryption)
11. [Writing Tests](#11-writing-tests)
12. [Running Tests](#12-running-tests)
13. [Tagging Strategy](#13-tagging-strategy)
14. [Reporting](#14-reporting)
15. [Trace Viewer](#15-trace-viewer)
16. [JavaDocs](#16-javadocs)
17. [CI/CD](#17-cicd)
18. [Debugging](#18-debugging)
19. [Contributing & Team Usage](#19-contributing--team-usage)
20. [Roadmap](#20-roadmap)

---

## 1. Project Overview

This framework is the Playwright counterpart to the [Selenium BDD Framework](https://github.com/andrewpaulcummins/selenium-bdd-framework). Both frameworks share identical BDD architecture, tagging strategy, encryption approach, and CI/CD pipeline design : the only difference is the browser automation layer underneath.

Having both frameworks demonstrates that the architectural thinking is tool-agnostic. The BDD layer, dependency injection, test data management, and pipeline integration are all framework patterns : not Selenium or Playwright specifics.

### Why Playwright alongside Selenium?

Most organisations have standardised on one tool or the other. By maintaining both:
- **Selenium** covers the vast majority of enterprise Java shops with existing frameworks
- **Playwright** covers modern cloud-native teams moving away from Selenium
- Both demonstrate the same architectural rigour, proving the patterns are portable

### Key Playwright advantages demonstrated in this framework

- **Auto-waiting** : no explicit waits, no `WebDriverWait`, no `ExpectedConditions`
- **BrowserContext isolation** : each scenario gets a fresh browser session without restarting the browser
- **Trace viewer** : full video, network, and DOM snapshot capture on failure
- **WebKit support** : test against Safari's rendering engine on Linux without owning a Mac
- **Locator API** : lazy element resolution eliminates `StaleElementReferenceException` entirely

### Applications under test

| Application | Type | URL |
|---|---|---|
| SauceDemo | UI / E2E | https://www.saucedemo.com |
| ReqRes | REST API | https://reqres.in/api |
| JSONPlaceholder | REST API | https://jsonplaceholder.typicode.com |

---

## 2. Selenium vs Playwright : Framework Comparison

| Aspect | Selenium Framework | Playwright Framework |
|---|---|---|
| **Browser automation** | Selenium WebDriver 4 | Microsoft Playwright for Java |
| **Test runner** | TestNG 7 | JUnit 5 |
| **Driver management** | WebDriverManager | Playwright manages own binaries |
| **Element model** | `WebElement` + `@FindBy` | `Locator` (lazy, re-evaluated) |
| **Waiting strategy** | Explicit waits (`WebDriverWait`) | Auto-waiting built in |
| **Session isolation** | Browser restart per scenario | `BrowserContext` per scenario |
| **Failure debugging** | Screenshots | Screenshots + full trace (video, network, DOM) |
| **Browser support** | Chrome, Firefox, Edge | Chromium, Firefox, WebKit (Safari) |
| **StaleElementException** | Possible | Eliminated by Locator design |
| **Runner annotation** | `@CucumberOptions` | `@Suite` + `@ConfigurationParameter` |
| **Assertion library** | TestNG `Assert` | JUnit 5 `Assertions` |

### Key code differences

**Element declaration:**
```java
// Selenium : @FindBy field, resolved at PageFactory.initElements time
@FindBy(id = "user-name")
private WebElement usernameField;

// Playwright : method returning a Locator, resolved lazily on each call
private Locator usernameField() { return locate("[data-test='username']"); }
```

**Waiting:**
```java
// Selenium : explicit wait required
waitForClickability(loginButton).click();

// Playwright : auto-waits before every action
loginButton().click();
```

**Assertions:**
```java
// TestNG (Selenium framework)
Assert.assertTrue(condition, "message");
Assert.assertEquals(actual, expected, "message");

// JUnit 5 (Playwright framework) : note parameter order flipped for assertEquals
assertTrue(condition, "message");
assertEquals(expected, actual, "message");
```

---

## 3. Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 (LTS) | Primary language |
| Playwright for Java | 1.47.0 | Browser automation |
| Cucumber | 7.15.0 | BDD framework |
| JUnit 5 | 5.10.2 | Test runner |
| PicoContainer | 2.15 | Dependency injection |
| REST Assured | 5.4.0 | API test automation |
| Jackson | 2.16.1 | JSON parsing |
| Allure | 2.25.0 | Test reporting |
| Gatling | 3.10.3 | Performance testing |
| Maven | 3.9.x | Build tool |
| GitHub Actions | : | Cloud CI/CD |
| Jenkins | : | Enterprise CI/CD |

---

## 4. Prerequisites

### Required

| Tool | Version | Download |
|---|---|---|
| Java JDK | 17 (LTS) | [Eclipse Temurin](https://adoptium.net/temurin/releases/?version=17) |
| Apache Maven | 3.9.x | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Node.js | 18+ | [nodejs.org](https://nodejs.org) : required for Playwright browser installation |
| Git | Latest | [git-scm.com](https://git-scm.com) |
| IntelliJ IDEA | Latest | [jetbrains.com/idea](https://www.jetbrains.com/idea) |

### Verifying your installation

```bash
java -version      # Should show openjdk 17.x.x
mvn --version      # Should show Apache Maven 3.9.x with Java 17
node --version     # Should show v18.x.x or higher
```

### Environment variable

```bash
# Windows (PowerShell)
[System.Environment]::SetEnvironmentVariable("TEST_DATA_SECRET_KEY", "YourKey1234567!", "User")

# macOS / Linux
export TEST_DATA_SECRET_KEY="YourKey1234567!"
```

The key must be exactly **16 characters**. Contact the framework maintainer for the correct key.

### Installing Playwright browsers

Playwright manages its own browser binaries independently of your system browsers. Run this once after cloning:

```bash
npx playwright install
```

This downloads Chromium, Firefox, and WebKit binaries to `~/.cache/ms-playwright/`. You only need to run this once per machine, or when upgrading the Playwright version.

To install only Chromium (faster):

```bash
npx playwright install chromium
```

---

## 5. Getting Started

### Clone the repository

```bash
git clone https://github.com/andrewpaulcummins/playwright-bdd-framework.git
cd playwright-bdd-framework
```

### Open in IntelliJ

1. **File → Open** and navigate to the cloned folder
2. Select `pom.xml` and click **Open as Project**
3. Click **Load Maven Project** when prompted
4. Wait for Maven to download all dependencies

### Install Playwright browsers

```bash
npx playwright install chromium
```

### Configure the run environment

1. **Run → Edit Configurations**
2. Select **RunCukesTest**
3. In **Environment variables** add:
```
TEST_DATA_SECRET_KEY=YourKey1234567!
```
4. Click **OK**

### Run the tests

Right-click `RunCukesTest.java` → **Run 'RunCukesTest'**

You should see Chromium launch, tests execute against SauceDemo, and a green result summary in the console.

---

## 6. Playwright Object Hierarchy

Unlike Selenium which has a single `WebDriver` object, Playwright uses a four-level hierarchy. Understanding this is essential for working with the framework.

```
Playwright  (root instance : manages browser processes)
    └── Browser  (launched browser : Chromium, Firefox, or WebKit)
            └── BrowserContext  (isolated session : like incognito window)
                    └── Page  (active browser tab : used for all interactions)
```

### Why BrowserContext matters

`BrowserContext` is Playwright's killer feature for test isolation. Each scenario in this framework gets a **fresh BrowserContext** : meaning:

- Cookies are cleared between scenarios
- LocalStorage is cleared between scenarios
- Authentication state never leaks between tests
- Sessions are completely isolated

This is achieved **without restarting the browser** : the `Browser` instance stays open between scenarios and only the `BrowserContext` is recycled. This makes scenario isolation significantly faster than Selenium's approach of quitting and relaunching the browser.

### Teardown order

Objects must be closed in strict reverse creation order:

```
1. Stop tracing on BrowserContext
2. Close BrowserContext  (automatically closes Page)
3. Close Browser
4. Close Playwright
```

This is handled automatically in `Hooks.java`.

---

## 7. Project Architecture

The framework uses the same strict layered BDD architecture as the Selenium framework:

```
┌─────────────────────────────────────────────────────┐
│                   FEATURE FILES                     │
│         Plain English Gherkin scenarios             │
│      Written by anyone : devs, QA, BAs, PMs        │
└─────────────────────┬───────────────────────────────┘
                      │ Cucumber maps steps
┌─────────────────────▼───────────────────────────────┐
│                STEP DEFINITIONS                     │
│    Orchestrates factories, navigator, POMs          │
│              Assertions live here                   │
└──────┬──────────────┬──────────────┬────────────────┘
       │              │              │
┌──────▼──────┐ ┌─────▼──────┐ ┌────▼───────────────┐
│ UserFactory │ │PageNavigator│ │   ScenarioContext   │
│             │ │ (reflection)│ │  (PicoContainer DI) │
└──────┬──────┘ └─────┬──────┘ └────────────────────┘
       │              │
┌──────▼──────────────▼───────────────────────────────┐
│                   PAGE POMs                         │
│   Locator-based : no @FindBy, no StaleElement       │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                 WIDGET POMs                         │
│         Reusable UI components                      │
└─────────────────────┬───────────────────────────────┘
                      │
┌─────────────────────▼───────────────────────────────┐
│                   BASE PAGE                         │
│   Playwright Page API : auto-waiting built in       │
└─────────────────────────────────────────────────────┘
```

### Design principles

**No static state** : PicoContainer injects one `ScenarioContext` per scenario containing the Playwright `Page` object. No static fields anywhere.

**Locator-based POMs** : element locators are defined as private methods returning `Locator` objects, evaluated lazily on every interaction. `StaleElementReferenceException` does not exist in Playwright.

**Reflection-based PageNavigator** : adding a new page requires only two map entries. No switch statement to update.

**Fail fast** : descriptive `RuntimeException` thrown immediately for missing environment variables, unrecognised user types, unregistered pages, and misconfigured properties.

**Auto-waiting** : Playwright polls the DOM until every element is ready before interacting. No `WebDriverWait`, no `ExpectedConditions`, no manual waits.

---

## 8. Folder Structure

```
playwright-bdd-framework/
├── .github/
│   └── workflows/
│       └── ci.yml                         # GitHub Actions CI pipeline
├── src/
│   └── test/
│       ├── java/
│       │   └── com/andrewcummins/framework/
│       │       ├── context/
│       │       │   └── ScenarioContext.java        # Playwright objects + shared state
│       │       ├── factory/
│       │       │   ├── BrowserFactory.java         # Playwright hierarchy creation
│       │       │   └── UserFactory.java            # User object creation
│       │       ├── hooks/
│       │       │   └── Hooks.java                  # @Before/@After lifecycle
│       │       ├── models/
│       │       │   └── User.java                   # Test user model
│       │       ├── navigation/
│       │       │   └── PageNavigator.java          # Reflection-based routing
│       │       ├── poms/
│       │       │   ├── BasePage.java               # Playwright Page API base
│       │       │   ├── pages/
│       │       │   │   ├── LoginPage.java
│       │       │   │   ├── InventoryPage.java
│       │       │   │   ├── CartPage.java
│       │       │   │   ├── CheckoutPage.java
│       │       │   │   ├── OverviewPage.java
│       │       │   │   └── CompletePage.java
│       │       │   └── widgets/
│       │       │       └── HeaderWidget.java
│       │       ├── runners/
│       │       │   └── RunCukesTest.java           # JUnit 5 Suite runner
│       │       ├── stepdefs/
│       │       │   └── LoginSteps.java
│       │       └── utils/
│       │           ├── ConfigReader.java
│       │           ├── EncryptionUtil.java
│       │           ├── EncryptionRunner.java
│       │           └── JsonDataReader.java
│       └── resources/
│           ├── config.properties
│           ├── features/
│           │   └── login/
│           │       └── login.feature
│           └── testdata/
│               └── users.json
├── .gitignore
├── Jenkinsfile
└── pom.xml
```

---

## 9. Configuration

All runtime configuration lives in `src/test/resources/config.properties`.

### Key Playwright-specific properties

```properties
# Browser : chromium | firefox | webkit
browser=chromium
headless=false

# Slow motion : delay in ms between actions (0 = normal speed)
# Useful for visually following test execution during debugging
slow.motion=0

# Single timeout in milliseconds : replaces Selenium's multiple timeouts
default.timeout=30000
navigation.timeout=30000

# Playwright trace viewer
# on | off | retain-on-failure (recommended)
trace.mode=retain-on-failure
trace.path=target/traces
```

### Switching browsers

```properties
browser=firefox    # Mozilla Firefox
browser=webkit     # Apple WebKit (Safari engine) : runs on Linux, no Mac needed
browser=chromium   # Chromium (default)
```

### CI/CD override

Browser and headless settings can be overridden at runtime without changing the file:

```bash
mvn test -Dbrowser=firefox -Dheadless=true
```

---

## 10. Test Data & Encryption

Identical to the Selenium framework : see the [Selenium README](https://github.com/andrewpaulcummins/selenium-bdd-framework#8-test-data--encryption) for full details.

### Quick reference

```bash
# Generate encrypted values
# Right-click EncryptionRunner.java → Run in IntelliJ
```

Users are stored in `src/test/resources/testdata/users.json` with AES-128 encrypted credentials. The decryption key lives only in the `TEST_DATA_SECRET_KEY` environment variable.

---

## 11. Writing Tests

### Feature file structure

Identical to the Selenium framework : same Gherkin, same tags, same dynamic step pattern:

```gherkin
@login
Feature: Login functionality

  @TC001 @sanity @regression
  Scenario: Standard user can log in successfully
    Given a "standard" user is on the "login" page
    When the user logs in
    Then the user should be on the "inventory" page
    And the inventory page should be displayed
```

### Adding a new Page POM

1. Create the class in `poms/pages/` extending `BasePage`
2. Define locators as private methods returning `Locator`
3. Add public interaction methods with JavaDoc
4. Register in `PageNavigator.buildUrlMap()` and `buildPageClassMap()`

```java
public class CartPage extends BasePage {

    public CartPage(ScenarioContext context) {
        super(context);
    }

    // Locator as a method : lazy, re-evaluated on every call
    private Locator checkoutButton() {
        return locate("[data-test='checkout']");
    }

    /**
     * Clicks the checkout button to proceed to the checkout page.
     */
    public void clickCheckout() {
        click(checkoutButton());
    }
}
```

### Adding a new page to PageNavigator

```java
// In buildUrlMap()
map.put("cart", baseUrl + "/cart.html");

// In buildPageClassMap()
map.put("cart", CartPage.class);
```

No other changes needed : reflection handles instantiation automatically.

---

## 12. Running Tests

### From IntelliJ (recommended)

Right-click `RunCukesTest.java` → **Run 'RunCukesTest'**

### Changing which tests run

Modify `FILTER_TAGS_PROPERTY_NAME` in `RunCukesTest.java`:

```java
@ConfigurationParameter(
    key = FILTER_TAGS_PROPERTY_NAME,
    value = "@regression"   // change this
)
```

### From Maven

```bash
# Default tags from RunCukesTest.java
mvn test

# Override tags
mvn test -Dcucumber.filter.tags="@sanity"

# Override browser
mvn test -Dbrowser=firefox

# Override headless
mvn test -Dheadless=true
```

### JUnit 5 vs TestNG runner : key difference

The Playwright framework uses `@Suite` with `@ConfigurationParameter` instead of `@CucumberOptions`:

```java
// Selenium framework (TestNG)
@CucumberOptions(tags = "@regression", features = "...", glue = "...")
public class RunCukesTest extends AbstractTestNGCucumberTests {}

// Playwright framework (JUnit 5)
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@regression")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.andrewcummins...")
public class RunCukesTest {}
```

---

## 13. Tagging Strategy

Identical to the Selenium framework:

| Tag Type | Examples | Purpose |
|---|---|---|
| Feature tag | `@login`, `@checkout` | Functional grouping |
| Suite tag | `@regression`, `@sanity`, `@WIP` | CI suite selection |
| Test ID | `@TC001`, `@TC002` | Unique permanent identifier |

---

## 14. Reporting

### Allure Report

```bash
# Generate and open in browser
mvn allure:serve

# Generate only
mvn allure:report
# Output: target/allure-report/index.html
```

### Cucumber HTML Report

Generated automatically after every run:
```
target/cucumber-reports/cucumber.html
```

---

## 15. Trace Viewer

The Playwright trace viewer is one of the most powerful debugging tools available in modern test automation. It captures a complete recording of every scenario : including screenshots at each step, full network traffic, console logs, and DOM snapshots.

### How it works

Tracing is configured via `config.properties`:

```properties
# retain-on-failure : only saves trace when scenario fails (recommended)
# on : always saves trace
# off : never saves trace
trace.mode=retain-on-failure
trace.path=target/traces
```

When a scenario fails, a `.zip` trace file is saved to `target/traces/`.

### Viewing a trace locally

```bash
npx playwright show-trace target/traces/ScenarioName.zip
```

This opens an interactive browser-based viewer showing:
- **Timeline** : visual timeline of every action
- **Screenshots** : screenshot at every step
- **DOM snapshots** : inspect the DOM at any point in time
- **Network** : every HTTP request and response
- **Console** : browser console output

### Viewing traces from CI

When a test fails in GitHub Actions, the trace file is uploaded as an artifact. Download `playwright-traces-chromium` from the Actions run page, extract it, and run:

```bash
npx playwright show-trace ScenarioName.zip
```

This is significantly more powerful than a screenshot alone : you can see exactly what happened, what network requests were made, and what the DOM looked like at every step of the failing scenario.

---

## 16. JavaDocs

All public methods on Page POMs and Widget POMs are documented.

### Generating JavaDocs

```bash
mvn javadoc:javadoc
```

Output: `target/site/apidocs/index.html`

Open in any browser to view the full API documentation.

---

## 17. CI/CD

### GitHub Actions

Pipeline defined in `.github/workflows/ci.yml`.

**Triggers:**
- Push to `main` or `develop`
- Pull request targeting `main`
- Daily at 6am UTC (scheduled)
- Manual via **Actions → CI Pipeline → Run workflow**

**Key difference from Selenium pipeline:**

Playwright installs its own browsers via npx rather than using `setup-chrome`:

```yaml
- name: Install Playwright browsers
  run: |
    npm install -g @playwright/test@1.47.0
    npx playwright install chromium
    sudo npx playwright install-deps chromium
```

**Artifacts uploaded on every run:**
- `allure-results-chromium` : raw Allure results (30 day retention)
- `cucumber-reports-chromium` : HTML and JSON reports (30 day retention)

**Artifacts uploaded on failure only:**
- `playwright-traces-chromium` : trace zip files (7 day retention)
- `failure-screenshots-chromium` : PNG screenshots (7 day retention)

**Adding the secret:**
1. Repository → **Settings → Secrets and variables → Actions**
2. **New repository secret**
3. Name: `TEST_DATA_SECRET_KEY`, Value: your 16 character key

**Manual run with custom tags:**
1. **Actions → CI Pipeline → Run workflow**
2. Enter tag expression (e.g. `@sanity`)
3. Select browser (`chromium`, `firefox`, `webkit`)
4. Click **Run workflow**

### Jenkins

Pipeline defined in `Jenkinsfile`. Stages:
1. Checkout
2. Build : `mvn test-compile`
3. Install Playwright Browsers
4. Test : `mvn test` with tag and browser parameters
5. Reports : Allure report generation

**Prerequisites:**
- JDK 17 tool named `JDK17`
- Maven 3.9.x tool named `Maven3`
- Node.js available on agent PATH
- Secret text credential named `TEST_DATA_SECRET_KEY`

---

## 18. Debugging

### Running a single test

In `RunCukesTest.java`:
```java
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@TC001")
```

### Running in headed mode (visible browser)

In `config.properties`:
```properties
headless=false
```

### Slow motion : follow execution visually

```properties
slow.motion=500   # 500ms delay between actions
```

This makes every Playwright action pause for 500ms, letting you watch exactly what the framework is doing in the browser.

### Using the IntelliJ debugger

1. Set a breakpoint in any step definition method
2. Right-click `RunCukesTest.java` → **Debug 'RunCukesTest'**
3. Execution pauses at the breakpoint
4. Use **Step Over** (F8) to advance line by line

### Viewing traces after local failure

```bash
npx playwright show-trace target/traces/ScenarioName.zip
```

### Common errors and fixes

| Error | Cause | Fix |
|---|---|---|
| `TEST_DATA_SECRET_KEY is not set` | Missing env var | Add to IntelliJ run configuration |
| `Missing X server or $DISPLAY` | Headed mode in headless environment | Set `headless=true` in config |
| `Page has not been initialised` | `@Before` hook failed | Check `BrowserFactory` : likely browser binary missing |
| `Page 'x' is not registered` | Missing PageNavigator entry | Add to `buildUrlMap()` and `buildPageClassMap()` |
| `User type 'x' not found` | Missing users.json entry | Add encrypted entry to users.json |
| `Playwright browsers not found` | Binaries not installed | Run `npx playwright install chromium` |

### Checking Playwright browser installation

```bash
npx playwright --version
npx playwright install --dry-run
```

---

## 19. Contributing & Team Usage

### For non-technical team members

Feature files live in `src/test/resources/features/`. You can write scenarios without any Java knowledge using the existing step definitions.

**Available steps:**
```gherkin
Given a "{userType}" user is on the "{pageName}" page
When the user logs in
When the user enters their username
When the user enters their password
When the user clicks the login button
Then the user should be on the "{pageName}" page
Then an error message should be displayed
Then the error message should contain "{text}"
Then the inventory page should be displayed
Then no error message should be displayed
```

### For developers

**Adding a new page:**
1. Create POM in `poms/pages/` extending `BasePage`
2. Define locators as private `Locator` methods
3. Add public methods with JavaDoc
4. Register in `PageNavigator` : two map entries only

**Pull request checklist:**
- [ ] All existing tests pass locally
- [ ] New scenarios follow tagging convention
- [ ] New public POM methods have JavaDoc
- [ ] No static variables introduced
- [ ] No hardcoded credentials
- [ ] `config.properties` updated if new properties added

---

## 20. Roadmap

### In progress
- [ ] API testing module with REST Assured
- [ ] Performance testing module with Gatling
- [ ] Additional page coverage : Cart, Checkout, Order Complete

### Planned
- [ ] Cross-browser matrix in CI (Chromium + Firefox + WebKit simultaneously)
- [ ] Playwright network interception for API mocking
- [ ] Accessibility testing using Playwright's built-in axe integration
- [ ] Database validation utilities

### Completed
- [x] Core BDD framework : Playwright, Cucumber, JUnit 5, PicoContainer
- [x] Zero static state : BrowserContext isolation per scenario
- [x] AES-128 encrypted test data
- [x] Dynamic user and page routing
- [x] Locator-based Page POMs : no StaleElementReferenceException
- [x] Widget POM pattern
- [x] Reflection-based PageNavigator
- [x] Playwright trace viewer on failure
- [x] Allure reporting with failure screenshots
- [x] GitHub Actions CI pipeline
- [x] Jenkinsfile CI pipeline
- [x] Full JavaDoc on public POM methods

---

## Author

**Andrew Cummins** : SDET / Automation Engineer

[![GitHub](https://img.shields.io/badge/GitHub-andrewpaulcummins-black?logo=github)](https://github.com/andrewpaulcummins)
[![Selenium Framework](https://img.shields.io/badge/Also%20see-Selenium%20BDD%20Framework-blue)](https://github.com/andrewpaulcummins/selenium-bdd-framework)

---

> *This framework demonstrates that enterprise-grade BDD automation architecture is tool-agnostic. The same patterns : layered BDD, PicoContainer DI, encrypted test data, dynamic routing, CI/CD integration : apply equally to Selenium and Playwright. The ability to implement these patterns across multiple ecosystems is what separates an automation engineer from an automation architect.*