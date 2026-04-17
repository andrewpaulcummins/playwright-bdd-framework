package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import com.microsoft.playwright.Locator;

public class CompletePage extends BasePage {

    /**
     * Initializes the CompletePage with the given scenario context.
     *
     * @param context the {@link ScenarioContext} containing page and test state
     */
    public CompletePage(ScenarioContext context) {
        super(context);
    }

    private Locator pageTitle()         { return locate(".title"); }
    private Locator confirmationHeader() { return locate(".complete-header"); }
    private Locator confirmationText()  { return locate(".complete-text"); }
    private Locator ponyExpressImage()  { return locate(".pony_express"); }
    private Locator backHomeButton()    { return locate("[data-test='back-to-products']"); }

    /**
     * Retrieves the header widget associated with this page.
     *
     * @return a {@link HeaderWidget} instance for interacting with the page header
     */
    public HeaderWidget getHeader() {
        return new HeaderWidget(context);
    }

    /**
     * Clicks the back to home button to return to the products page.
     */
    public void clickBackHome() {
        click(backHomeButton());
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
     * Checks if the page title is currently displayed on the complete page.
     *
     * @return {@code true} if the page title is visible, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isVisible(pageTitle());
    }

    /**
     * Retrieves the text content of the confirmation header.
     *
     * @return the confirmation header text
     */
    public String getConfirmationHeaderText() {
        return getText(confirmationHeader());
    }

    /**
     * Retrieves the text content of the confirmation message.
     *
     * @return the confirmation text
     */
    public String getConfirmationText() {
        return getText(confirmationText());
    }

    /**
     * Checks if the confirmation header is currently displayed on the page.
     *
     * @return {@code true} if the confirmation header is visible, {@code false} otherwise
     */
    public boolean isConfirmationHeaderDisplayed() {
        return isVisible(confirmationHeader());
    }

    /**
     * Checks if the Pony Express image is currently displayed on the complete page.
     *
     * @return {@code true} if the Pony Express image is visible, {@code false} otherwise
     */
    public boolean isPonyExpressImageDisplayed() {
        return isVisible(ponyExpressImage());
    }
}