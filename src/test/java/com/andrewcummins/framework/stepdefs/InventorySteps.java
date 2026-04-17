package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.navigation.PageNavigator;
import com.andrewcummins.framework.poms.pages.InventoryPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventorySteps {

    private final ScenarioContext context;
    private final PageNavigator pageNavigator;

    /**
     * Initializes the InventorySteps with the given scenario context.
     * Also initializes the PageNavigator for page navigation.
     *
     * @param context the {@link ScenarioContext} containing page and test state
     */
    public InventorySteps(ScenarioContext context) {
        this.context = context;
        this.pageNavigator = new PageNavigator(context);
    }

    /**
     * Step definition: Sorts products on the inventory page by the specified sort option.
     *
     * @param sortOption the sort option to apply (e.g., "Name (A to Z)", "Price (low to high)")
     * @see InventoryPage#sortProductsBy(String)
     */
    @When("the user sorts products by {string}")
    public void theUserSortsProductsBy(String sortOption) {
        InventoryPage inventoryPage = new InventoryPage(context);
        inventoryPage.sortProductsBy(sortOption);
    }

    /**
     * Step definition: Adds a specific product to the shopping cart.
     *
     * @param productName the name of the product to add to the cart
     * @see InventoryPage#addProductToCart(String)
     */
    @When("the user adds {string} to the cart")
    public void theUserAddsProductToCart(String productName) {
        InventoryPage inventoryPage = new InventoryPage(context);
        inventoryPage.addProductToCart(productName);
    }

    /**
     * Step definition: Clicks on a product by its name to view product details.
     *
     * @param productName the name of the product to click on
     * @see InventoryPage#clickProductName(String)
     */
    @When("the user clicks on product {string}")
    public void theUserClicksOnProduct(String productName) {
        InventoryPage inventoryPage = new InventoryPage(context);
        inventoryPage.clickProductName(productName);
    }

    /**
     * Step definition: Verifies that the inventory page displays the expected number of products.
     *
     * @param expectedCount the expected number of products to display
     * @throws AssertionError if the product count does not match the expected count
     * @see InventoryPage#getProductCount()
     */
    @Then("the inventory page should display {int} products")
    public void theInventoryPageShouldDisplayProducts(int expectedCount) {
        InventoryPage inventoryPage = new InventoryPage(context);
        assertEquals(
                expectedCount,
                inventoryPage.getProductCount(),
                "Expected " + expectedCount + " products but found " + inventoryPage.getProductCount()
        );
    }

    /**
     * Step definition: Verifies that products are sorted alphabetically in ascending order (A to Z).
     *
     * @throws AssertionError if products are not sorted alphabetically ascending
     * @see InventoryPage#getProductNames()
     */
    @Then("the products should be sorted alphabetically ascending")
    public void theProductsShouldBeSortedAlphabeticallyAscending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<String> names = inventoryPage.getProductNames();
        List<String> sorted = names.stream().sorted().toList();
        assertEquals(sorted, names, "Products are not sorted alphabetically ascending.");
    }

    /**
     * Step definition: Verifies that products are sorted alphabetically in descending order (Z to A).
     *
     * @throws AssertionError if products are not sorted alphabetically descending
     * @see InventoryPage#getProductNames()
     */
    @Then("the products should be sorted alphabetically descending")
    public void theProductsShouldBeSortedAlphabeticallyDescending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<String> names = inventoryPage.getProductNames();
        List<String> sorted = names.stream().sorted(java.util.Comparator.reverseOrder()).toList();
        assertEquals(sorted, names, "Products are not sorted alphabetically descending.");
    }

    /**
     * Step definition: Verifies that products are sorted by price in ascending order (low to high).
     *
     * @throws AssertionError if products are not sorted by price ascending
     * @see InventoryPage#getProductPrices()
     */
    @Then("the products should be sorted by price ascending")
    public void theProductsShouldBeSortedByPriceAscending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<Double> prices = inventoryPage.getProductPrices().stream()
                .map(p -> Double.parseDouble(p.replace("$", "")))
                .toList();
        List<Double> sorted = prices.stream().sorted().toList();
        assertEquals(sorted, prices, "Products are not sorted by price ascending.");
    }

    /**
     * Step definition: Verifies that products are sorted by price in descending order (high to low).
     *
     * @throws AssertionError if products are not sorted by price descending
     * @see InventoryPage#getProductPrices()
     */
    @Then("the products should be sorted by price descending")
    public void theProductsShouldBeSortedByPriceDescending() {
        InventoryPage inventoryPage = new InventoryPage(context);
        List<Double> prices = inventoryPage.getProductPrices().stream()
                .map(p -> Double.parseDouble(p.replace("$", "")))
                .toList();
        List<Double> sorted = prices.stream().sorted(java.util.Comparator.reverseOrder()).toList();
        assertEquals(sorted, prices, "Products are not sorted by price descending.");
    }

    /**
     * Step definition: Verifies that the cart badge displays the expected item count.
     * Asserts that the badge is visible and shows the correct count.
     *
     * @param expectedCount the expected number displayed on the cart badge
     * @throws AssertionError if cart badge is not displayed or count does not match
     * @see InventoryPage#isCartBadgeDisplayed()
     * @see InventoryPage#getCartBadgeCount()
     */
    @Then("the cart badge should show {int}")
    public void theCartBadgeShouldShow(int expectedCount) {
        InventoryPage inventoryPage = new InventoryPage(context);
        assertTrue(inventoryPage.isCartBadgeDisplayed(), "Cart badge is not displayed.");
        assertEquals(
                expectedCount,
                inventoryPage.getCartBadgeCount(),
                "Expected cart badge count " + expectedCount + " but found " + inventoryPage.getCartBadgeCount()
        );
    }

    /**
     * Step definition: Verifies that the cart badge is not displayed on the page.
     *
     * @throws AssertionError if cart badge is displayed but should not be
     * @see InventoryPage#isCartBadgeDisplayed()
     */
    @Then("the cart badge should not be displayed")
    public void theCartBadgeShouldNotBeDisplayed() {
        InventoryPage inventoryPage = new InventoryPage(context);
        assertFalse(inventoryPage.isCartBadgeDisplayed(), "Cart badge is displayed but should not be.");
    }
}