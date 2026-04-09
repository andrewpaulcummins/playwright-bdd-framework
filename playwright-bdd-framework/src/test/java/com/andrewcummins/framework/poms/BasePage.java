package com.andrewcummins.framework.poms;

import com.andrewcummins.framework.context.ScenarioContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Base class for all Page Object Models and Widget POMs in the Playwright framework.
 *
 * <p>Every page and widget class extends this base to inherit a consistent set of
 * interaction methods built on Playwright's {@code Page} and {@code Locator} APIs.</p>
 *
 * <h2>Key difference from Selenium BasePage</h2>
 * <p>Playwright auto-waits for elements before every action — click, fill, selectOption
 * etc. There are no explicit waits, no {@code WebDriverWait}, no {@code ExpectedConditions}.
 * Playwright polls the DOM until the element is ready or the timeout is reached.
 * This makes tests significantly more stable and concise.</p>
 *
 * <h2>Locator strategy</h2>
 * <p>Playwright uses {@code Locator} objects rather than {@code WebElement} references.
 * Locators are lazy — they don't query the DOM until an action is performed on them.
 * This eliminates {@code StaleElementReferenceException} entirely.</p>
 *
 * <h2>Recommended locator priority</h2>
 * <ol>
 *   <li>ARIA role — {@code getByRole()}</li>
 *   <li>Label — {@code getByLabel()}</li>
 *   <li>Placeholder — {@code getByPlaceholder()}</li>
 *   <li>Test ID — {@code getByTestId()}</li>
 *   <li>CSS selector — {@code locator()}</li>
 *   <li>XPath — last resort</li>
 * </ol>
 */
public abstract class BasePage {

    /**
     * The {@code ScenarioContext} instance providing access to the Page and configuration.
     */
    protected final ScenarioContext context;

    /**
     * The Playwright {@code Page} instance for this scenario.
     */
    protected final Page page;

    /**
     * Constructs a new {@code BasePage} with the given {@code ScenarioContext}.
     *
     * <p>Unlike the Selenium framework, there is no PageFactory initialisation
     * needed here — Playwright Locators are resolved lazily at interaction time.</p>
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public BasePage(ScenarioContext context) {
        this.context = context;
        this.page = context.getPage();
    }

    // =========================================================================
    // NAVIGATION
    // =========================================================================

    /**
     * Navigates the browser to the given URL and waits for the page to load.
     *
     * @param url the full URL to navigate to
     */
    public void navigateTo(String url) {
        page.navigate(url);
    }

    /**
     * Returns the current page URL.
     *
     * @return the current URL string
     */
    public String getCurrentUrl() {
        return page.url();
    }

    /**
     * Returns the current page title.
     *
     * @return the current page title string
     */
    public String getPageTitle() {
        return page.title();
    }

    /**
     * Navigates back to the previous page in the browser history.
     */
    public void navigateBack() {
        page.goBack();
    }

    /**
     * Refreshes the current page.
     */
    public void refreshPage() {
        page.reload();
    }

    // =========================================================================
    // LOCATOR HELPERS
    // =========================================================================

    /**
     * Returns a {@code Locator} for the given CSS selector.
     *
     * <p>Locators are lazy — the DOM is not queried until an action is performed.</p>
     *
     * @param selector the CSS selector string
     * @return a {@code Locator} for the given selector
     */
    public Locator locate(String selector) {
        return page.locator(selector);
    }

    /**
     * Returns a {@code Locator} for an element with the given ARIA role.
     *
     * <p>ARIA role locators are the most resilient — they match elements by their
     * semantic meaning rather than their CSS class or ID, making tests less brittle
     * to UI changes.</p>
     *
     * @param role the ARIA role to match
     * @param name the accessible name of the element
     * @return a {@code Locator} for the element
     */
    public Locator locateByRole(AriaRole role, String name) {
        return page.getByRole(role, new Page.GetByRoleOptions().setName(name));
    }

    /**
     * Returns a {@code Locator} for an element with the given label text.
     *
     * @param label the label text associated with the element
     * @return a {@code Locator} for the labelled element
     */
    public Locator locateByLabel(String label) {
        return page.getByLabel(label);
    }

    /**
     * Returns a {@code Locator} for an element with the given placeholder text.
     *
     * @param placeholder the placeholder text of the input element
     * @return a {@code Locator} for the element
     */
    public Locator locateByPlaceholder(String placeholder) {
        return page.getByPlaceholder(placeholder);
    }

    /**
     * Returns a {@code Locator} for an element with the given {@code data-testid} attribute.
     *
     * @param testId the value of the {@code data-testid} attribute
     * @return a {@code Locator} for the element
     */
    public Locator locateByTestId(String testId) {
        return page.getByTestId(testId);
    }

    /**
     * Returns a {@code Locator} for an element containing the given text.
     *
     * @param text the text content to match
     * @return a {@code Locator} for the element
     */
    public Locator locateByText(String text) {
        return page.getByText(text);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Clicks the element identified by the given locator.
     *
     * <p>Playwright auto-waits for the element to be visible, stable,
     * and enabled before clicking.</p>
     *
     * @param locator the {@code Locator} identifying the element to click
     */
    public void click(Locator locator) {
        locator.click();
    }

    /**
     * Clears the input field and types the given text into it.
     *
     * <p>Uses Playwright's {@code fill()} which clears the field first,
     * then sets the value directly — faster and more reliable than
     * simulating keystrokes with {@code type()}.</p>
     *
     * @param locator the {@code Locator} identifying the input field
     * @param text    the text to enter
     */
    public void fill(Locator locator, String text) {
        locator.fill(text);
    }

    /**
     * Selects an option from a {@code <select>} dropdown by its visible text.
     *
     * @param locator     the {@code Locator} identifying the select element
     * @param visibleText the visible text of the option to select
     */
    public void selectOption(Locator locator, String visibleText) {
        locator.selectOption(visibleText);
    }

    /**
     * Retrieves the visible text content of the element identified by the locator.
     *
     * <p>Playwright auto-waits for the element to be visible before reading its text.</p>
     *
     * @param locator the {@code Locator} identifying the element
     * @return the visible text content as a string
     */
    public String getText(Locator locator) {
        return locator.innerText();
    }

    /**
     * Retrieves the value of the specified attribute from the element.
     *
     * @param locator       the {@code Locator} identifying the element
     * @param attributeName the name of the attribute to retrieve
     * @return the attribute value as a string, or {@code null} if not present
     */
    public String getAttribute(Locator locator, String attributeName) {
        return locator.getAttribute(attributeName);
    }

    /**
     * Retrieves the value of an input field.
     *
     * <p>Uses Playwright's {@code inputValue()} which reads the {@code value}
     * property of input, textarea, and select elements.</p>
     *
     * @param locator the {@code Locator} identifying the input element
     * @return the current input value as a string
     */
    public String getInputValue(Locator locator) {
        return locator.inputValue();
    }

    /**
     * Presses the Enter key on the element identified by the locator.
     *
     * @param locator the {@code Locator} identifying the element
     */
    public void pressEnter(Locator locator) {
        locator.press("Enter");
    }

    // =========================================================================
    // WAIT METHODS
    // =========================================================================

    /**
     * Waits until the element identified by the locator is visible.
     *
     * <p>In most cases this is unnecessary since Playwright auto-waits,
     * but useful when you need an explicit visibility assertion before proceeding.</p>
     *
     * @param locator the {@code Locator} to wait for
     */
    public void waitForVisible(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
    }

    /**
     * Waits until the element identified by the locator is hidden or removed.
     *
     * <p>Useful for waiting on loading spinners or overlays to disappear.</p>
     *
     * @param locator the {@code Locator} to wait for
     */
    public void waitForHidden(Locator locator) {
        locator.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN));
    }

    /**
     * Waits until the current URL contains the given text.
     *
     * @param urlFragment the text expected to appear in the current URL
     */
    public void waitForUrlToContain(String urlFragment) {
        page.waitForURL("**" + urlFragment + "**");
    }

    // =========================================================================
    // STATE CHECKS
    // =========================================================================

    /**
     * Returns whether the element identified by the locator is currently visible.
     *
     * @param locator the {@code Locator} to check
     * @return {@code true} if the element is visible, {@code false} otherwise
     */
    public boolean isVisible(Locator locator) {
        return locator.isVisible();
    }

    /**
     * Returns whether the element identified by the locator is enabled.
     *
     * @param locator the {@code Locator} to check
     * @return {@code true} if the element is enabled, {@code false} otherwise
     */
    public boolean isEnabled(Locator locator) {
        return locator.isEnabled();
    }

    /**
     * Returns the count of elements matching the given locator.
     *
     * <p>Useful for asserting the number of items in a list without
     * retrieving all elements.</p>
     *
     * @param locator the {@code Locator} to count
     * @return the number of matching elements
     */
    public int count(Locator locator) {
        return locator.count();
    }

    // =========================================================================
    // JAVASCRIPT
    // =========================================================================

    /**
     * Scrolls the element identified by the locator into the visible viewport.
     *
     * @param locator the {@code Locator} identifying the element to scroll to
     */
    public void scrollIntoView(Locator locator) {
        locator.scrollIntoViewIfNeeded();
    }

    /**
     * Executes JavaScript in the context of the current page.
     *
     * @param script the JavaScript expression to execute
     * @return the result of the script execution
     */
    public Object executeScript(String script) {
        return page.evaluate(script);
    }

    /**
     * Takes a screenshot of the current page and returns it as a byte array.
     *
     * <p>Used by the {@code Hooks} class to capture failure screenshots
     * and attach them to the Allure report.</p>
     *
     * @return the screenshot as a PNG byte array
     */
    public byte[] takeScreenshot() {
        return page.screenshot();
    }
}