package com.andrewcummins.framework.context;

import com.andrewcummins.framework.models.User;
import com.andrewcummins.framework.utils.ConfigReader;
import com.andrewcummins.framework.utils.JsonDataReader;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Central shared state container for a single Cucumber scenario execution.
 *
 * <p>Identical in design to the Selenium framework's ScenarioContext but holds
 * Playwright objects instead of a WebDriver instance. PicoContainer creates one
 * instance per scenario and injects it into all step definition classes.</p>
 *
 * <h2>Playwright object hierarchy</h2>
 * <ul>
 *   <li>{@code Playwright} — the root instance, manages browser processes</li>
 *   <li>{@code Browser} — a launched browser instance (Chromium, Firefox, WebKit)</li>
 *   <li>{@code BrowserContext} — an isolated browser session (like an incognito window)</li>
 *   <li>{@code Page} — a single browser tab, used for all interactions</li>
 * </ul>
 *
 * <p>Using a fresh {@code BrowserContext} per scenario ensures complete isolation
 * between scenarios — cookies, local storage, and session state are never shared.</p>
 */
public class ScenarioContext {

    /** Playwright root instance — manages browser processes. */
    private Playwright playwright;

    /** Browser instance — launched once per scenario. */
    private Browser browser;

    /** BrowserContext — isolated session, fresh per scenario. */
    private BrowserContext browserContext;

    /** Page — the active browser tab used for all interactions. */
    private Page page;

    /** Current user for this scenario. */
    private User currentUser;

    /** Current page name for this scenario. */
    private String currentPageName;

    /** ConfigReader instance — loaded once per scenario. */
    private final ConfigReader configReader;

    /** JsonDataReader instance — loaded once per scenario. */
    private final JsonDataReader jsonDataReader;

    /** General purpose data store for passing values between steps. */
    private final Map<String, Object> scenarioData;

    /**
     * Constructs a new {@code ScenarioContext} for a single scenario execution.
     * PicoContainer calls this constructor automatically at the start of each scenario.
     */
    public ScenarioContext() {
        this.configReader = new ConfigReader();
        this.jsonDataReader = new JsonDataReader(configReader.getTestDataPath());
        this.scenarioData = new HashMap<>();
    }

    /**
     * Returns the Playwright root instance.
     *
     * @return the {@code Playwright} instance
     * @throws RuntimeException if Playwright has not been initialised
     */
    public Playwright getPlaywright() {
        if (playwright == null) {
            throw new RuntimeException(
                    "[ScenarioContext] Playwright has not been initialised. " +
                            "Ensure the @Before hook is running correctly."
            );
        }
        return playwright;
    }

    /**
     * Sets the Playwright root instance.
     *
     * @param playwright the initialised {@code Playwright} instance
     */
    public void setPlaywright(Playwright playwright) {
        this.playwright = playwright;
    }

    /**
     * Returns the Browser instance for this scenario.
     *
     * @return the {@code Browser} instance
     * @throws RuntimeException if the browser has not been initialised
     */
    public Browser getBrowser() {
        if (browser == null) {
            throw new RuntimeException(
                    "[ScenarioContext] Browser has not been initialised. " +
                            "Ensure the @Before hook is running correctly."
            );
        }
        return browser;
    }

    /**
     * Sets the Browser instance for this scenario.
     *
     * @param browser the initialised {@code Browser} instance
     */
    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    /**
     * Returns the BrowserContext for this scenario.
     *
     * <p>Each scenario gets a fresh BrowserContext ensuring complete
     * isolation of cookies, storage, and session state.</p>
     *
     * @return the {@code BrowserContext} instance
     * @throws RuntimeException if the browser context has not been initialised
     */
    public BrowserContext getBrowserContext() {
        if (browserContext == null) {
            throw new RuntimeException(
                    "[ScenarioContext] BrowserContext has not been initialised. " +
                            "Ensure the @Before hook is running correctly."
            );
        }
        return browserContext;
    }

    /**
     * Sets the BrowserContext for this scenario.
     *
     * @param browserContext the initialised {@code BrowserContext} instance
     */
    public void setBrowserContext(BrowserContext browserContext) {
        this.browserContext = browserContext;
    }

    /**
     * Returns the active Page for this scenario.
     *
     * <p>The Page is used for all browser interactions in page objects.
     * It is created from the BrowserContext in the @Before hook.</p>
     *
     * @return the active {@code Page} instance
     * @throws RuntimeException if the page has not been initialised
     */
    public Page getPage() {
        if (page == null) {
            throw new RuntimeException(
                    "[ScenarioContext] Page has not been initialised. " +
                            "Ensure the @Before hook is running correctly."
            );
        }
        return page;
    }

    /**
     * Sets the active Page for this scenario.
     *
     * @param page the initialised {@code Page} instance
     */
    public void setPage(Page page) {
        this.page = page;
    }

    /**
     * Returns the current user for this scenario.
     *
     * @return the current {@code User} object
     * @throws RuntimeException if no user has been set
     */
    public User getCurrentUser() {
        if (currentUser == null) {
            throw new RuntimeException(
                    "[ScenarioContext] No user has been set for this scenario. " +
                            "Ensure a Given step sets the user before any user-dependent steps."
            );
        }
        return currentUser;
    }

    /**
     * Sets the current user for this scenario.
     *
     * @param currentUser the {@code User} object to set
     */
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    /**
     * Returns the name of the current page.
     *
     * @return the current page name string
     */
    public String getCurrentPageName() {
        return currentPageName;
    }

    /**
     * Sets the name of the current page.
     *
     * @param currentPageName the page name to set
     */
    public void setCurrentPageName(String currentPageName) {
        this.currentPageName = currentPageName;
    }

    /**
     * Returns the {@code ConfigReader} instance for this scenario.
     *
     * @return the {@code ConfigReader} instance
     */
    public ConfigReader getConfigReader() {
        return configReader;
    }

    /**
     * Returns the {@code JsonDataReader} instance for this scenario.
     *
     * @return the {@code JsonDataReader} instance
     */
    public JsonDataReader getJsonDataReader() {
        return jsonDataReader;
    }

    /**
     * Stores an arbitrary value in the scenario data map.
     *
     * @param key   a descriptive string key
     * @param value the value to store
     */
    public void setData(String key, Object value) {
        scenarioData.put(key, value);
    }

    /**
     * Retrieves a value from the scenario data map.
     *
     * @param <T>  the expected return type
     * @param key  the key under which the value was stored
     * @param type the {@code Class} to cast the value to
     * @return the stored value cast to type {@code T}
     * @throws RuntimeException if no value exists for the given key
     */
    public <T> T getData(String key, Class<T> type) {
        Object value = scenarioData.get(key);
        if (value == null) {
            throw new RuntimeException(
                    "[ScenarioContext] No data found for key '" + key + "'. " +
                            "Available keys: " + scenarioData.keySet()
            );
        }
        return type.cast(value);
    }

    /**
     * Returns whether a value exists in the scenario data map for the given key.
     *
     * @param key the key to check
     * @return {@code true} if a value exists for the key
     */
    public boolean hasData(String key) {
        return scenarioData.containsKey(key);
    }

    // =========================================================================
    // PLAYWRIGHT INITIALISATION STATE CHECKS
    // =========================================================================

    /**
     * Returns {@code true} if a {@code Page} has been initialised for this scenario.
     *
     * <p>Use this instead of {@link #getPage()} when you only need to know whether
     * a browser was launched (e.g. in teardown hooks that must support API-only scenarios).</p>
     *
     * @return {@code true} if {@code page} is not null
     */
    public boolean isPageInitialised() {
        return page != null;
    }

    /**
     * Returns {@code true} if a {@code BrowserContext} has been initialised for this scenario.
     *
     * @return {@code true} if {@code browserContext} is not null
     */
    public boolean isBrowserContextInitialised() {
        return browserContext != null;
    }

    /**
     * Returns {@code true} if a {@code Browser} has been initialised for this scenario.
     *
     * @return {@code true} if {@code browser} is not null
     */
    public boolean isBrowserInitialised() {
        return browser != null;
    }

    /**
     * Returns {@code true} if a {@code Playwright} instance has been initialised for this scenario.
     *
     * @return {@code true} if {@code playwright} is not null
     */
    public boolean isPlaywrightInitialised() {
        return playwright != null;
    }

    // =========================================================================
    // API RESPONSE STATE
    // =========================================================================

    private Response lastResponse;

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response response) {
        this.lastResponse = response;
    }
}