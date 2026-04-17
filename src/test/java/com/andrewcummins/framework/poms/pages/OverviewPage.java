package com.andrewcummins.framework.poms.pages;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.poms.BasePage;
import com.andrewcummins.framework.poms.widgets.HeaderWidget;
import com.microsoft.playwright.Locator;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class OverviewPage extends BasePage {

    /**
     * Initializes the OverviewPage with the given scenario context.
     *
     * @param context the {@link ScenarioContext} containing page and test state
     */
    public OverviewPage(ScenarioContext context) {
        super(context);
    }

    private Locator pageTitle()         { return locate(".title"); }
    private Locator itemNames()         { return locate(".inventory_item_name"); }
    private Locator itemPrices()        { return locate(".inventory_item_price"); }
    private Locator subtotalLabel()     { return locate(".summary_subtotal_label"); }
    private Locator taxLabel()          { return locate(".summary_tax_label"); }
    private Locator totalLabel()        { return locate(".summary_total_label"); }
    private Locator finishButton()      { return locate("[data-test='finish']"); }
    private Locator cancelButton()      { return locate("[data-test='cancel']"); }

    /**
     * Retrieves the header widget associated with this page.
     *
     * @return a {@link HeaderWidget} instance for interacting with the page header
     */
    public HeaderWidget getHeader() {
        return new HeaderWidget(context);
    }

    /**
     * Clicks the finish button to complete the order.
     */
    public void clickFinish() {
        click(finishButton());
    }

    /**
     * Clicks the cancel button to cancel the order.
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
     * Checks if the page title is currently displayed on the overview page.
     *
     * @return {@code true} if the page title is visible, {@code false} otherwise
     */
    public boolean isPageTitleDisplayed() {
        return isVisible(pageTitle());
    }

    /**
     * Retrieves the names of all items in the order.
     *
     * @return a {@link List} of item names displayed on the overview page
     */
    public List<String> getOrderItemNames() {
        int count = itemNames().count();
        return IntStream.range(0, count)
                .mapToObj(i -> itemNames().nth(i).innerText())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the subtotal amount displayed on the order overview.
     *
     * @return the subtotal text including label and amount
     */
    public String getSubtotal() {
        return getText(subtotalLabel());
    }

    /**
     * Retrieves the tax amount displayed on the order overview.
     *
     * @return the tax text including label and amount
     */
    public String getTax() {
        return getText(taxLabel());
    }

    /**
     * Retrieves the total amount displayed on the order overview.
     *
     * @return the total text including label and amount
     */
    public String getTotal() {
        return getText(totalLabel());
    }

    /**
     * Checks if the finish button is visible and accessible on the overview page.
     *
     * @return {@code true} if the finish button is displayed, {@code false} otherwise
     */
    public boolean isFinishButtonDisplayed() {
        return isVisible(finishButton());
    }
}