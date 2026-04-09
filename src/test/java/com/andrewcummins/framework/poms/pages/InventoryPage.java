package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import com.microsoft.playwright.Locator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Page Object Model for the SauceDemo inventory page.
 *
 * <p>Encapsulates all element locators and interactions for the inventory page.
 * The shared header component is exposed via {@link #getHeader()} which returns
 * a {@code HeaderWidget} instance.</p>
 */
public class InventoryPage extends BasePage {

    /**
     * Constructs a new {@code InventoryPage} with the given {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public InventoryPage(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // LOCATORS
    // =========================================================================

    private Locator pageTitle()      { return locate(".title"); }
    private Locator sortDropdown()   { return locate("[data-test='product-sort-container']"); }
    private Locator productNames()   { return locate(".inventory_item_name"); }
    private Locator productPrices()  { return locate(".inventory_item_price"); }
    private Locator inventoryItems() { return locate(".inventory_item"); }
    private Locator cartButtons()    { return locate(".btn_inventory"); }
    private Locator cartBadge()      { return locate(".shopping_cart_badge"); }

    // =========================================================================
    // WIDGET ACCESS
    // =========================================================================

    /**
     * Returns the {@code HeaderWidget} instance for this page.
     *
     * <p>All header interactions — burger menu, cart icon, logout — should
     * be performed through this widget.</p>
     *
     * @return a new {@code HeaderWidget} instance
     */
    public HeaderWidget getHeader() {
        return new HeaderWidget(context);
    }

    // =========================================================================
    // INTERACTION METHODS
    // =========================================================================

    /**
     * Adds the product with the given name to the shopping cart.
     *
     * <p>Locates the matching product card and clicks its Add to Cart button.
     * Fails fast if no product with the given name is found.</p>
     *
     * @param productName the exact name of the product to add
     * @throws RuntimeException if the product is not found
     */
    public void addProductToCart(String productName) {
        int count = productNames().count();
        for (int i = 0; i < count; i++) {
            if (productNames().nth(i).innerText().equals(productName)) {
                cartButtons().nth(i).click();
                return;
            }
        }
        throw new RuntimeException(
                "[InventoryPage] Product '" + productName + "' not found. " +
                        "Available products: " + getProductNames()
        );
    }

    /**
     * Clicks the product name link for the given product name.
     *
     * @param productName the exact name of the product to click
     * @throws RuntimeException if the product is not found
     */
    public void clickProductName(String productName) {
        int count = productNames().count();
        for (int i = 0; i < count; i++) {
            if (productNames().nth(i).innerText().equals(productName)) {
                productNames().nth(i).click();
                return;
            }
        }
        throw new RuntimeException(
                "[InventoryPage] Product '" + productName + "' not found. " +
                        "Available products: " + getProductNames()
        );
    }

    /**
     * Sorts the product list by the given visible sort option text.
     *
     * <p>Valid options: "Name (A to Z)", "Name (Z to A)",
     * "Price (low to high)", "Price (high to low)"</p>
     *
     * @param sortOption the visible text of the sort option to select
     */
    public void sortProductsBy(String sortOption) {
        selectOption(sortDropdown(), sortOption);
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the page title text displayed at the top of the inventory page.
     *
     * @return the page title text
     */
    public String getPageTitleText() {
        return getText(pageTitle());
    }

    /**
     * Returns whether the inventory page title is displayed.
     *
     * @return {@code true} if the page title is visible
     */
    public boolean isPageTitleDisplayed() {
        return isVisible(pageTitle());
    }

    /**
     * Returns a list of all product names currently displayed on the page.
     *
     * @return a {@code List} of product name strings in display order
     */
    public List<String> getProductNames() {
        int count = productNames().count();
        return IntStream.range(0, count)
                .mapToObj(i -> productNames().nth(i).innerText())
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of all product prices currently displayed on the page.
     *
     * @return a {@code List} of product price strings in display order
     */
    public List<String> getProductPrices() {
        int count = productPrices().count();
        return IntStream.range(0, count)
                .mapToObj(i -> productPrices().nth(i).innerText())
                .collect(Collectors.toList());
    }

    /**
     * Returns the total number of product items displayed on the page.
     *
     * @return the number of product items
     */
    public int getProductCount() {
        return inventoryItems().count();
    }

    /**
     * Returns the number displayed on the shopping cart badge.
     *
     * @return the cart item count as an integer
     */
    public int getCartBadgeCount() {
        return Integer.parseInt(getText(cartBadge()));
    }

    /**
     * Returns whether the shopping cart badge is currently displayed.
     *
     * @return {@code true} if the cart badge is visible
     */
    public boolean isCartBadgeDisplayed() {
        return isVisible(cartBadge());
    }
}