package com.andrewcummins.framework.poms.widgets;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.microsoft.playwright.Locator;

/**
 * Widget POM for the SauceDemo shared header component.
 *
 * <p>The header appears on every post-login page. Encapsulating it here
 * ensures header interactions are defined once and reused across all pages
 * that contain it. Identical in responsibility to the Selenium framework's
 * HeaderWidget — rewritten using Playwright's Locator API.</p>
 */
public class HeaderWidget extends BasePage {

    /**
     * Constructs a new {@code HeaderWidget} with the given {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public HeaderWidget(ScenarioContext context) {
        super(context);
    }

    // =========================================================================
    // LOCATORS
    // =========================================================================

    private Locator burgerMenuButton()  { return locate("#react-burger-menu-btn"); }
    private Locator burgerMenuClose()   { return locate("#react-burger-cross-btn"); }
    private Locator cartIcon()          { return locate(".shopping_cart_link"); }
    private Locator cartBadge()         { return locate(".shopping_cart_badge"); }
    private Locator appLogo()           { return locate(".app_logo"); }
    private Locator allItemsLink()      { return locate("#inventory_sidebar_link"); }
    private Locator logoutLink()        { return locate("#logout_sidebar_link"); }
    private Locator resetAppStateLink() { return locate("#reset_sidebar_link"); }

    // =========================================================================
    // BURGER MENU INTERACTIONS
    // =========================================================================

    /**
     * Opens the burger menu sidebar.
     */
    public void openBurgerMenu() {
        click(burgerMenuButton());
    }

    /**
     * Closes the burger menu sidebar.
     */
    public void closeBurgerMenu() {
        click(burgerMenuClose());
    }

    /**
     * Clicks the All Items link in the burger menu sidebar.
     */
    public void clickAllItems() {
        click(allItemsLink());
    }

    /**
     * Clicks the Logout link in the burger menu sidebar.
     */
    public void clickLogout() {
        click(logoutLink());
    }

    /**
     * Clicks the Reset App State link in the burger menu sidebar.
     */
    public void clickResetAppState() {
        click(resetAppStateLink());
    }

    /**
     * Logs the user out by opening the burger menu and clicking logout.
     */
    public void logout() {
        openBurgerMenu();
        clickLogout();
    }

    // =========================================================================
    // CART INTERACTIONS
    // =========================================================================

    /**
     * Clicks the shopping cart icon, navigating to the cart page.
     */
    public void clickCartIcon() {
        click(cartIcon());
    }

    // =========================================================================
    // STATE RETRIEVAL METHODS
    // =========================================================================

    /**
     * Returns the number of items displayed on the cart badge.
     *
     * @return the cart item count as an integer
     */
    public int getCartItemCount() {
        return Integer.parseInt(getText(cartBadge()));
    }

    /**
     * Returns whether the cart badge is currently displayed.
     *
     * @return {@code true} if the cart badge is visible
     */
    public boolean isCartBadgeDisplayed() {
        return isVisible(cartBadge());
    }

    /**
     * Returns whether the application logo is displayed in the header.
     *
     * @return {@code true} if the app logo is visible
     */
    public boolean isAppLogoDisplayed() {
        return isVisible(appLogo());
    }

    /**
     * Returns whether the burger menu sidebar is currently open.
     *
     * @return {@code true} if the burger menu is open
     */
    public boolean isMenuOpen() {
        return isVisible(burgerMenuClose());
    }

    /**
     * Returns whether the burger menu button is displayed in the header.
     *
     * @return {@code true} if the burger menu button is visible
     */
    public boolean isBurgerMenuButtonDisplayed() {
        return isVisible(burgerMenuButton());
    }
}