package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.microsoft.playwright.Locator;

/**
 * Page Object Model for the SauceDemo login page.
 *
 * <p>Encapsulates all element locators and interactions for the login page.
 * Notice the absence of {@code @FindBy} annotations — Playwright uses
 * {@code Locator} objects defined as methods rather than fields, eliminating
 * {@code StaleElementReferenceException} entirely.</p>
 */
public class LoginPage extends BasePage {

    /**
     * Constructs a new {@code LoginPage} with the given {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public LoginPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // LOCATORS — defined as private methods rather than @FindBy fields
    // Playwright re-evaluates locators on every interaction, eliminating
    // StaleElementReferenceException
    // =========================================================================

    private Locator usernameField() { return locate("[data-test='username']"); }
    private Locator passwordField() { return locate("[data-test='password']"); }
    private Locator loginButton()   { return locate("[data-test='login-button']"); }
    private Locator errorMessage()  { return locate("[data-test='error']"); }
    private Locator errorDismiss()  { return locate(".error-button"); }
    private Locator loginLogo()     { return locate(".login_logo"); }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Enters the given username into the username input field.
     *
     * @param username the username string to enter
     */
    public void enterUsername(String username) {
        fill(usernameField(), username);
    }

    /**
     * Enters the given password into the password input field.
     *
     * @param password the password string to enter
     */
    public void enterPassword(String password) {
        fill(passwordField(), password);
    }

    /**
     * Clicks the login submit button.
     */
    public void clickLoginButton() {
        click(loginButton());
    }

    /**
     * Performs a complete login by entering credentials and clicking the login button.
     *
     * @param username the username to enter
     * @param password the password to enter
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Dismisses the error message by clicking the X button.
     */
    public void dismissErrorMessage() {
        click(errorDismiss());
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the current text of the error message container.
     *
     * @return the error message text as a string
     */
    public String getErrorMessageText() {
        return getText(errorMessage());
    }

    /**
     * Returns whether the error message is currently displayed.
     *
     * @return {@code true} if the error message is visible
     */
    public boolean isErrorMessageDisplayed() {
        return isVisible(errorMessage());
    }

    /**
     * Returns whether the login logo is currently displayed.
     *
     * @return {@code true} if the login logo is visible
     */
    public boolean isLoginLogoDisplayed() {
        return isVisible(loginLogo());
    }

    /**
     * Returns whether the login button is currently displayed.
     *
     * @return {@code true} if the login button is visible
     */
    public boolean isLoginButtonDisplayed() {
        return isVisible(loginButton());
    }

    /**
     * Returns the current value of the username input field.
     *
     * @return the current username field value
     */
    public String getUsernameFieldValue() {
        return getInputValue(usernameField());
    }
}