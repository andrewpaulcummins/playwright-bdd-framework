package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.context.ScenarioContext;
import com.andrewcummins.framework.factory.UserFactory;
import com.andrewcummins.framework.models.User;
import com.andrewcummins.framework.navigation.PageNavigator;
import com.andrewcummins.framework.poms.pages.InventoryPage;
import com.andrewcummins.framework.poms.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber step definitions for login-related feature file steps.
 *
 * <p>Identical in structure to the Selenium framework's LoginSteps.
 * The only difference is the assertion library — JUnit 5 {@code Assertions}
 * replaces TestNG {@code Assert}.</p>
 *
 * <h2>JUnit 5 vs TestNG assertions</h2>
 * <ul>
 *   <li>TestNG: {@code Assert.assertTrue(condition, message)}</li>
 *   <li>JUnit 5: {@code assertTrue(condition, message)} — message is a
 *       {@code Supplier<String>} or {@code String}, parameter order is the same</li>
 * </ul>
 */
public class LoginSteps {

    private final ScenarioContext context;
    private final PageNavigator pageNavigator;
    private final UserFactory userFactory;

    /**
     * Constructs a new {@code LoginSteps} with the injected {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} injected by PicoContainer
     */
    public LoginSteps(ScenarioContext context) {
        this.context = context;
        this.pageNavigator = new PageNavigator(context);
        this.userFactory = new UserFactory(context.getJsonDataReader());
    }

    // =========================================================================
    // GIVEN STEPS
    // =========================================================================

    /**
     * Navigates to the specified page as the specified user type.
     *
     * <p>Loads and decrypts the user from test data, stores them in context,
     * and navigates the browser to the correct URL via {@code PageNavigator}.</p>
     *
     * @param userType the user type string (e.g. "standard", "locked")
     * @param pageName the page name string (e.g. "login", "inventory")
     */
    @Given("a {string} user is on the {string} page")
    public void aUserIsOnThePage(String userType, String pageName) {
        User user = userFactory.getUser(userType);
        context.setCurrentUser(user);
        pageNavigator.navigateTo(pageName);
    }

    // =========================================================================
    // WHEN STEPS
    // =========================================================================

    /**
     * Performs a complete login using the current user's credentials.
     *
     * @see LoginPage#login(String, String)
     */
    @When("the user logs in")
    public void theUserLogsIn() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        User currentUser = context.getCurrentUser();
        loginPage.login(currentUser.getUsername(), currentUser.getPassword());
    }

    /**
     * Enters only the username of the current user without submitting.
     */
    @When("the user enters their username")
    public void theUserEntersTheirUsername() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        loginPage.enterUsername(context.getCurrentUser().getUsername());
    }

    /**
     * Enters only the password of the current user without submitting.
     */
    @When("the user enters their password")
    public void theUserEntersTheirPassword() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        loginPage.enterPassword(context.getCurrentUser().getPassword());
    }

    /**
     * Clicks the login button without entering any credentials.
     */
    @When("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        loginPage.clickLoginButton();
    }

    // =========================================================================
    // THEN STEPS
    // =========================================================================

    /**
     * Asserts that the user has been navigated to the specified page.
     *
     * @param pageName the expected page name
     */
    @Then("the user should be on the {string} page")
    public void theUserShouldBeOnThePage(String pageName) {
        String currentUrl = context.getPage().url();
        String expectedUrlFragment = pageName.toLowerCase().equals("login")
                ? context.getConfigReader().getUiBaseUrl()
                : pageName.toLowerCase();

        assertTrue(
                currentUrl.contains(expectedUrlFragment),
                "Expected to be on the '" + pageName + "' page but current URL was: " + currentUrl
        );

        context.setCurrentPageName(pageName.toLowerCase());
    }

    /**
     * Asserts that an error message is displayed on the login page.
     */
    @Then("an error message should be displayed")
    public void anErrorMessageShouldBeDisplayed() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        assertTrue(
                loginPage.isErrorMessageDisplayed(),
                "Expected an error message to be displayed but none was found."
        );
    }

    /**
     * Asserts that the error message contains the expected text.
     *
     * @param expectedMessage the expected error message text
     */
    @Then("the error message should contain {string}")
    public void theErrorMessageShouldContain(String expectedMessage) {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        String actualMessage = loginPage.getErrorMessageText();
        assertTrue(
                actualMessage.contains(expectedMessage),
                "Expected error message to contain '" + expectedMessage +
                        "' but actual message was: '" + actualMessage + "'"
        );
    }

    /**
     * Asserts that the inventory page is displayed after a successful login.
     */
    @Then("the inventory page should be displayed")
    public void theInventoryPageShouldBeDisplayed() {
        InventoryPage inventoryPage = new InventoryPage(context);
        assertTrue(
                inventoryPage.isPageTitleDisplayed(),
                "Expected the inventory page to be displayed but the page title was not found."
        );
        assertEquals(
                "Products",
                inventoryPage.getPageTitleText(),
                "Expected page title to be 'Products' but was: " + inventoryPage.getPageTitleText()
        );
    }

    /**
     * Asserts that no error message is displayed on the login page.
     */
    @Then("no error message should be displayed")
    public void noErrorMessageShouldBeDisplayed() {
        LoginPage loginPage = (LoginPage) pageNavigator.getCurrentPage();
        assertFalse(
                loginPage.isErrorMessageDisplayed(),
                "Expected no error message to be displayed but one was found."
        );
    }
}