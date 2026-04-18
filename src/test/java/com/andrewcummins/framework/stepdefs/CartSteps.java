package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.pages.CartPage;
import com.andrewcummins.framework.poms.pages.InventoryPage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

public class CartSteps {

    private final ScenarioContext context;

    /**
     * Initializes the CartSteps with the given scenario context.
     *
     * @param context the {@link ScenarioContext} containing page and test state
     */
    public CartSteps(ScenarioContext context) {
        this.context = context;
    }

    /**
     * Step definition: Navigates to the cart from the inventory page.
     * This step clicks the cart icon in the header and updates the current page context to "cart".
     *
     * @see HeaderWidget#clickCartIcon()
     * @see ScenarioContext#setCurrentPageName(String)
     */
    @When("the user navigates to the cart")
    public void theUserNavigatesToTheCart() {
        InventoryPage inventoryPage = new InventoryPage(context);
        inventoryPage.getHeader().clickCartIcon();
        context.setCurrentPageName("cart");
    }

    /**
     * Step definition: Removes a specific product from the cart.
     *
     * @param productName the name of the product to remove from the cart
     * @see CartPage#removeItem(String)
     */
    @When("the user removes {string} from the cart")
    public void theUserRemovesFromTheCart(String productName) {
        CartPage cartPage = new CartPage(context);
        cartPage.removeItem(productName);
    }

    /**
     * Step definition: Initiates the checkout process from the cart.
     *
     * @see CartPage#clickCheckout()
     */
    @When("the user proceeds to checkout")
    public void theUserProceedsToCheckout() {
        CartPage cartPage = new CartPage(context);
        cartPage.clickCheckout();
    }

    /**
     * Step definition: Returns from cart to shopping by clicking the continue shopping button.
     *
     * @see CartPage#clickContinueShopping()
     */
    @When("the user continues shopping")
    public void theUserContinuesShopping() {
        CartPage cartPage = new CartPage(context);
        cartPage.clickContinueShopping();
    }

    /**
     * Step definition: Verifies that the cart page is displayed with the correct title.
     * Asserts that the page title is visible and matches "Your Cart".
     *
     * @throws AssertionError if the cart page title is not displayed or does not match expected value
     * @see CartPage#isPageTitleDisplayed()
     * @see CartPage#getPageTitleText()
     */
    @Then("the cart page should be displayed")
    public void theCartPageShouldBeDisplayed() {
        CartPage cartPage = new CartPage(context);
        assertTrue(cartPage.isPageTitleDisplayed(), "Cart page title is not displayed.");
        assertEquals("Your Cart", cartPage.getPageTitleText(),
                "Expected cart page title 'Your Cart' but was: " + cartPage.getPageTitleText());
    }

    /**
     * Step definition: Verifies that the cart contains the expected number of items.
     *
     * @param expectedCount the expected number of items in the cart
     * @throws AssertionError if the actual item count does not match the expected count
     * @see CartPage#getCartItemCount()
     */
    @Then("the cart should contain {int} items")
    public void theCartShouldContainItems(int expectedCount) {
        CartPage cartPage = new CartPage(context);
        assertEquals(expectedCount, cartPage.getCartItemCount(),
                "Expected " + expectedCount + " items in cart but found " + cartPage.getCartItemCount());
    }

    /**
     * Step definition: Verifies that a specific product is present in the cart.
     *
     * @param productName the name of the product to verify
     * @throws AssertionError if the product is not found in the cart
     * @see CartPage#isProductInCart(String)
     */
    @Then("the cart should contain {string}")
    public void theCartShouldContain(String productName) {
        CartPage cartPage = new CartPage(context);
        assertTrue(cartPage.isProductInCart(productName),
                "Expected '" + productName + "' to be in the cart but it was not found.");
    }

    /**
     * Step definition: Verifies that a specific product is NOT present in the cart.
     *
     * @param productName the name of the product to verify
     * @throws AssertionError if the product is found in the cart
     * @see CartPage#isProductInCart(String)
     */
    @Then("the cart should not contain {string}")
    public void theCartShouldNotContain(String productName) {
        CartPage cartPage = new CartPage(context);
        assertFalse(cartPage.isProductInCart(productName),
                "Expected '" + productName + "' to not be in the cart but it was found.");
    }

    /**
     * Step definition: Verifies that the checkout button is visible on the cart page.
     *
     * @throws AssertionError if the checkout button is not displayed
     * @see CartPage#isCheckoutButtonDisplayed()
     */
    @Then("the checkout button should be displayed")
    public void theCheckoutButtonShouldBeDisplayed() {
        CartPage cartPage = new CartPage(context);
        assertTrue(cartPage.isCheckoutButtonDisplayed(), "Checkout button is not displayed.");
    }
}