package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import com.microsoft.playwright.Locator;

public class CheckoutPage extends BasePage {

    /**
     * Initializes the CheckoutPage with the given scenario context.
     *
     * @param context the {@link ScenarioContext} containing page and test state
     */
    public CheckoutPage(ScenarioContext context) {
        super(context);
    }

    private Locator pageTitle()         { return locate(".title"); }
    private Locator firstNameField()    { return locate("[data-test='firstName']"); }
    private Locator lastNameField()     { return locate("[data-test='lastName']"); }
    private Locator postalCodeField()   { return locate("[data-test='postalCode']"); }
    private Locator continueButton()    { return locate("[data-test='continue']"); }
    private Locator cancelButton()      { return locate("[data-test='cancel']"); }
    private Locator errorMessage()      { return locate("[data-test='error']"); }

    /**
     * Retrieves the header widget associated with this page.
     *
     * @return a {@link HeaderWidget} instance for interacting with the page header
     */
    public HeaderWidget getHeader() {
        return new HeaderWidget(context);
    }

    /**
     * Enters the first name into the checkout form.
     *
     * @param firstName the first name to enter
     */
    public void enterFirstName(String firstName) {
        fill(firstNameField(), firstName);
    }

    /**
     * Enters the last name into the checkout form.
     *
     * @param lastName the last name to enter
     */
    public void enterLastName(String lastName) {
        fill(lastNameField(), lastName);
    }

    /**
     * Enters the postal code into the checkout form.
     *
     * @param postalCode the postal code to enter
     */
    public void enterPostalCode(String postalCode) {
        fill(postalCodeField(), postalCode);
    }

    /**
     * Fills in all checkout form details at once.
     *
     * @param firstName the first name to enter
     * @param lastName the last name to enter
     * @param postalCode the postal code to enter
     */
    public void fillCheckoutDetails(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
    }

    /**
     * Clicks the continue button to proceed with checkout.
     */
    public void clickContinue() {
        click(continueButton());
    }

    /**
     * Clicks the cancel button to cancel the checkout process.
     */
    public void clickCancel() {
        click(cancelButton());
    }

    /**
     * Retrieves the text content of the page title.
     *
     * @return the page title text
     */
    public String getPageTitleText() {
        return getText(pageTitle());
    }

    /**
     * Checks if the page title is currently displayed on the checkout page.
     *
     * @return {@code true} if the page title is visible, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isVisible(pageTitle());
    }

    /**
     * Checks if an error message is currently displayed on the checkout page.
     *
     * @return {@code true} if the error message is visible, {@code false} otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isVisible(errorMessage());
    }

    /**
     * Retrieves the text content of the error message.
     *
     * @return the error message text
     */
    public String getErrorMessageText() {
        return getText(errorMessage());
    }
}