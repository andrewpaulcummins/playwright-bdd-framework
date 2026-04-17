package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.pages.CheckoutPage;
import com.andrewcummins.framework.poms.pages.CompletePage;
import com.andrewcummins.framework.poms.pages.OverviewPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class CheckoutSteps {

    private final ScenarioContext context;

    /**
     * Initializes the CheckoutSteps with the given scenario context.
     *
     * @param context the {@link ScenarioContext} containing page and test state
     */
    public CheckoutSteps(ScenarioContext context) {
        this.context = context;
    }

    /**
     * Step definition: Enters checkout details (first name, last name, and postal code) on the checkout page.
     *
     * @param firstName the first name to enter
     * @param lastName the last name to enter
     * @param postalCode the postal code to enter
     * @see CheckoutPage#fillCheckoutDetails(String, String, String)
     */
    @When("the user enters checkout details {string} {string} {string}")
    public void theUserEntersCheckoutDetails(String firstName, String lastName, String postalCode) {
        CheckoutPage checkoutPage = new CheckoutPage(context);
        checkoutPage.fillCheckoutDetails(firstName, lastName, postalCode);
    }

    /**
     * Step definition: Clicks the continue button on the checkout information page.
     * This advances to the checkout overview page.
     *
     * @see CheckoutPage#clickContinue()
     */
    @When("the user clicks continue on checkout")
    public void theUserClicksContinueOnCheckout() {
        CheckoutPage checkoutPage = new CheckoutPage(context);
        checkoutPage.clickContinue();
    }

    /**
     * Step definition: Clicks the finish button on the overview page to complete the order.
     *
     * @see OverviewPage#clickFinish()
     */
    @When("the user clicks finish")
    public void theUserClicksFinish() {
        OverviewPage overviewPage = new OverviewPage(context);
        overviewPage.clickFinish();
    }

    /**
     * Step definition: Clicks the back to products button on the completion page.
     *
     * @see CompletePage#clickBackHome()
     */
    @When("the user clicks back to products")
    public void theUserClicksBackToProducts() {
        CompletePage completePage = new CompletePage(context);
        completePage.clickBackHome();
    }

    /**
     * Step definition: Verifies that the checkout page is displayed with the correct title.
     * Asserts that the page title is visible and matches "Checkout: Your Information".
     *
     * @throws AssertionError if the checkout page title is not displayed or does not match expected value
     * @see CheckoutPage#isPageTitleDisplayed()
     * @see CheckoutPage#getPageTitleText()
     */
    @Then("the checkout page should be displayed")
    public void theCheckoutPageShouldBeDisplayed() {
        CheckoutPage checkoutPage = new CheckoutPage(context);
        assertTrue(checkoutPage.isPageTitleDisplayed(), "Checkout page title is not displayed.");
        assertEquals("Checkout: Your Information", checkoutPage.getPageTitleText(),
                "Unexpected checkout page title: " + checkoutPage.getPageTitleText());
    }

    /**
     * Step definition: Verifies that the overview page is displayed with the correct title.
     * Asserts that the page title is visible and matches "Checkout: Overview".
     *
     * @throws AssertionError if the overview page title is not displayed or does not match expected value
     * @see OverviewPage#isPageTitleDisplayed()
     * @see OverviewPage#getPageTitleText()
     */
    @Then("the overview page should be displayed")
    public void theOverviewPageShouldBeDisplayed() {
        OverviewPage overviewPage = new OverviewPage(context);
        assertTrue(overviewPage.isPageTitleDisplayed(), "Overview page title is not displayed.");
        assertEquals("Checkout: Overview", overviewPage.getPageTitleText(),
                "Unexpected overview page title: " + overviewPage.getPageTitleText());
    }

    /**
     * Step definition: Verifies that the order completion page is displayed with the correct title
     * and that the confirmation header is visible.
     * Asserts that the page title matches "Checkout: Complete!" and confirmation header is shown.
     *
     * @throws AssertionError if the completion page or confirmation header is not displayed correctly
     * @see CompletePage#isPageTitleDisplayed()
     * @see CompletePage#getPageTitleText()
     * @see CompletePage#isConfirmationHeaderDisplayed()
     */
    @Then("the order confirmation page should be displayed")
    public void theOrderConfirmationPageShouldBeDisplayed() {
        CompletePage completePage = new CompletePage(context);
        assertTrue(completePage.isPageTitleDisplayed(), "Complete page title is not displayed.");
        assertEquals("Checkout: Complete!", completePage.getPageTitleText(),
                "Unexpected complete page title: " + completePage.getPageTitleText());
        assertTrue(completePage.isConfirmationHeaderDisplayed(), "Confirmation header is not displayed.");
    }

    /**
     * Step definition: Verifies that the confirmation header contains the expected text.
     *
     * @param expectedText the text expected to be contained in the confirmation header
     * @throws AssertionError if the confirmation header does not contain the expected text
     * @see CompletePage#getConfirmationHeaderText()
     */
    @Then("the confirmation header should contain {string}")
    public void theConfirmationHeaderShouldContain(String expectedText) {
        CompletePage completePage = new CompletePage(context);
        assertTrue(completePage.getConfirmationHeaderText().contains(expectedText),
                "Expected confirmation header to contain '" + expectedText + "' but was: " + completePage.getConfirmationHeaderText());
    }

    /**
     * Step definition: Verifies that an error message is displayed on the checkout page
     * and that it contains the expected text.
     *
     * @param expectedText the text expected to be contained in the error message
     * @throws AssertionError if error message is not displayed or does not contain expected text
     * @see CheckoutPage#isErrorMessageDisplayed()
     * @see CheckoutPage#getErrorMessageText()
     */
    @Then("the checkout error message should contain {string}")
    public void theCheckoutErrorMessageShouldContain(String expectedText) {
        CheckoutPage checkoutPage = new CheckoutPage(context);
        assertTrue(checkoutPage.isErrorMessageDisplayed(), "Checkout error message is not displayed.");
        assertTrue(checkoutPage.getErrorMessageText().contains(expectedText),
                "Expected error to contain '" + expectedText + "' but was: " + checkoutPage.getErrorMessageText());
    }

    /**
     * Step definition: Verifies that a specific product name appears in the order overview.
     *
     * @param productName the name of the product to verify in the overview
     * @throws AssertionError if the product is not found in the order overview
     * @see OverviewPage#getOrderItemNames()
     */
    @Then("the overview should contain {string}")
    public void theOverviewShouldContain(String productName) {
        OverviewPage overviewPage = new OverviewPage(context);
        assertTrue(overviewPage.getOrderItemNames().contains(productName),
                "Expected '" + productName + "' in overview but items were: " + overviewPage.getOrderItemNames());
    }
}