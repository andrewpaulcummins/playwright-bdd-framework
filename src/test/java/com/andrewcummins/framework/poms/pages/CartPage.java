package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import com.microsoft.playwright.Locator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CartPage extends BasePage {

    /**
     * Initializes the CartPage with the given scenario context.
     *
     * @param context the {@link ScenarioContext} containing page and test state
     */
    public CartPage(ScenarioContext context) {
        super(context);
    }

    private Locator pageTitle()         { return locate(".title"); }
    private Locator cartItems()         { return locate(".cart_item"); }
    private Locator itemNames()         { return locate(".inventory_item_name"); }
    private Locator itemPrices()        { return locate(".inventory_item_price"); }
    private Locator itemQuantities()    { return locate(".cart_quantity"); }
    private Locator removeButtons()     { return locate(".cart_button"); }
    private Locator checkoutButton()    { return locate("[data-test='checkout']"); }
    private Locator continueShoppingButton() { return locate("[data-test='continue-shopping']"); }

    /**
     * Retrieves the header widget associated with this page.
     *
     * @return a {@link HeaderWidget} instance for interacting with the page header
     */
    public HeaderWidget getHeader() {
        return new HeaderWidget(context);
    }

    /**
     * Clicks the checkout button to proceed with the purchase.
     */
    public void clickCheckout() {
        click(checkoutButton());
    }

    /**
     * Clicks the continue shopping button to return to the product listing.
     */
    public void clickContinueShopping() {
        click(continueShoppingButton());
    }

    /**
     * Removes a product from the cart by its name.
     *
     * @param productName the name of the product to remove
     * @throws RuntimeException if the product is not found in the cart
     */
    public void removeItem(String productName) {
        int count = itemNames().count();
        for (int i = 0; i < count; i++) {
            if (itemNames().nth(i).innerText().equals(productName)) {
                removeButtons().nth(i).click();
                return;
            }
        }
        throw new RuntimeException("[CartPage] Product '" + productName + "' not found in cart.");
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
     * Checks if the page title is currently displayed on the cart page.
     *
     * @return {@code true} if the page title is visible, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isVisible(pageTitle());
    }

    /**
     * Gets the total number of items currently in the cart.
     *
     * @return the count of cart items
     */
    public int getCartItemCount() {
        return cartItems().count();
    }

    /**
     * Retrieves the names of all items currently in the cart.
     *
     * @return a {@link List} of product names in the cart
     */
    public List<String> getCartItemNames() {
        int count = itemNames().count();
        return IntStream.range(0, count)
                .mapToObj(i -> itemNames().nth(i).innerText())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the prices of all items currently in the cart.
     *
     * @return a {@link List} of item prices in the cart
     */
    public List<String> getCartItemPrices() {
        int count = itemPrices().count();
        return IntStream.range(0, count)
                .mapToObj(i -> itemPrices().nth(i).innerText())
                .collect(Collectors.toList());
    }

    /**
     * Verifies if a specific product is present in the cart.
     *
     * @param productName the name of the product to check
     * @return {@code true} if the product is in the cart, {@code false} otherwise
     */
    public boolean isProductInCart(String productName) {
        return getCartItemNames().contains(productName);
    }

    /**
     * Checks if the checkout button is visible and accessible on the cart page.
     *
     * @return {@code true} if the checkout button is displayed, {@code false} otherwise
     */
    public boolean isCheckoutButtonDisplayed() {
        return isVisible(checkoutButton());
    }

    /**
     * Checks if the continue shopping button is visible and accessible on the cart page.
     *
     * @return {@code true} if the continue shopping button is displayed, {@code false} otherwise
     */
    public boolean isContinueShoppingButtonDisplayed() {
        return isVisible(continueShoppingButton());
    }
}