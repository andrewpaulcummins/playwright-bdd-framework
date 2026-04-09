package com.andrewcummins.framework.stepdefs;

import com.andrewcummins.framework.api.clients.AuthApiClient;
import com.andrewcummins.framework.api.clients.UserApiClient;
import com.andrewcummins.framework.context.ScenarioContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Cucumber step definitions for API feature file steps.
 *
 * <p>Step definitions are kept thin — they delegate all request logic
 * to the appropriate API client and all assertion logic stays here.
 * No REST Assured code lives in this class.</p>
 *
 * <p>The last API response is stored in {@code ScenarioContext} between
 * steps, allowing When steps to fire requests and Then steps to assert
 * on the stored response without passing objects between methods.</p>
 */
public class ApiSteps {

    private final ScenarioContext context;
    private final UserApiClient userApiClient;
    private final AuthApiClient authApiClient;

    /**
     * Constructs a new {@code ApiSteps} with the injected {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} injected by PicoContainer
     */
    public ApiSteps(ScenarioContext context) {
        this.context = context;
        this.userApiClient = new UserApiClient(context);
        this.authApiClient = new AuthApiClient(context);
    }

    // =========================================================================
    // GIVEN STEPS
    // =========================================================================

    /**
     * Placeholder Given step for API scenarios.
     * Confirms the ReqRes API is the system under test.
     */
    @Given("the ReqRes API is available")
    public void theReqResApiIsAvailable() {
        System.out.println("[ApiSteps] Target API: " + context.getConfigReader().getApiBaseUrl());
    }

    // =========================================================================
    // WHEN STEPS — USER
    // =========================================================================

    /**
     * Sends a GET request to retrieve a paginated list of users.
     *
     * @param page the page number to retrieve
     */
    @When("the client requests page {int} of users")
    public void theClientRequestsPageOfUsers(int page) {
        userApiClient.getUsers(page);
    }

    /**
     * Sends a GET request to retrieve a single user by ID.
     *
     * @param userId the user ID to retrieve
     */
    @When("the client requests user with ID {int}")
    public void theClientRequestsUserWithId(int userId) {
        userApiClient.getUserById(userId);
    }

    /**
     * Sends a GET request for a user ID that does not exist.
     */
    @When("the client requests a user that does not exist")
    public void theClientRequestsAUserThatDoesNotExist() {
        userApiClient.getUserNotFound();
    }

    /**
     * Sends a POST request to create a new user.
     *
     * @param name the name of the user to create
     * @param job  the job title of the user to create
     */
    @When("the client creates a user with name {string} and job {string}")
    public void theClientCreatesAUserWithNameAndJob(String name, String job) {
        userApiClient.createUser(name, job);
    }

    /**
     * Sends a PUT request to update an existing user.
     *
     * @param userId the ID of the user to update
     * @param name   the updated name
     * @param job    the updated job title
     */
    @When("the client updates user {int} with name {string} and job {string}")
    public void theClientUpdatesUserWithNameAndJob(int userId, String name, String job) {
        userApiClient.updateUser(userId, name, job);
    }

    /**
     * Sends a DELETE request to delete a user.
     *
     * @param userId the ID of the user to delete
     */
    @When("the client deletes user with ID {int}")
    public void theClientDeletesUserWithId(int userId) {
        userApiClient.deleteUser(userId);
    }

    // =========================================================================
    // WHEN STEPS — AUTH
    // =========================================================================

    /**
     * Sends a POST login request with the given credentials.
     *
     * @param email    the email address to authenticate with
     * @param password the password to authenticate with
     */
    @When("the client logs in with email {string} and password {string}")
    public void theClientLogsInWithEmailAndPassword(String email, String password) {
        authApiClient.login(email, password);
    }

    /**
     * Sends a POST login request with a missing password.
     *
     * @param email the email address to authenticate with
     */
    @When("the client logs in with email {string} and no password")
    public void theClientLogsInWithEmailAndNoPassword(String email) {
        authApiClient.loginWithoutPassword(email);
    }

    /**
     * Sends a POST register request with the given credentials.
     *
     * @param email    the email address to register with
     * @param password the password to register with
     */
    @When("the client registers with email {string} and password {string}")
    public void theClientRegistersWithEmailAndPassword(String email, String password) {
        authApiClient.register(email, password);
    }

    // =========================================================================
    // THEN STEPS — STATUS CODE
    // =========================================================================

    /**
     * Asserts that the response status code matches the expected value.
     *
     * @param expectedStatusCode the expected HTTP status code
     */
    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        Response response = context.getLastResponse();
        assertEquals(
                expectedStatusCode,
                response.statusCode(),
                "Expected status code " + expectedStatusCode +
                        " but received " + response.statusCode() +
                        ". Response body: " + response.body().asString()
        );
    }

    // =========================================================================
    // THEN STEPS — RESPONSE BODY
    // =========================================================================

    /**
     * Asserts that the response body contains the expected text.
     *
     * @param expectedText the text expected to be present in the response body
     */
    @Then("the response body should contain {string}")
    public void theResponseBodyShouldContain(String expectedText) {
        String responseBody = context.getLastResponse().body().asString();
        assertTrue(
                responseBody.contains(expectedText),
                "Expected response body to contain '" + expectedText +
                        "' but body was: " + responseBody
        );
    }

    /**
     * Asserts that the response body field at the given JSON path equals the expected value.
     *
     * @param jsonPath      the JSON path expression (e.g. "data.first_name")
     * @param expectedValue the expected string value at the path
     */
    @Then("the response field {string} should be {string}")
    public void theResponseFieldShouldBe(String jsonPath, String expectedValue) {
        String actualValue = context.getLastResponse().jsonPath().getString(jsonPath);
        assertEquals(
                expectedValue,
                actualValue,
                "Expected field '" + jsonPath + "' to be '" + expectedValue +
                        "' but was '" + actualValue + "'"
        );
    }

    /**
     * Asserts that the response body field at the given JSON path is not null or empty.
     *
     * @param jsonPath the JSON path expression (e.g. "token")
     */
    @Then("the response field {string} should not be empty")
    public void theResponseFieldShouldNotBeEmpty(String jsonPath) {
        String actualValue = context.getLastResponse().jsonPath().getString(jsonPath);
        assertNotNull(
                actualValue,
                "Expected field '" + jsonPath + "' to be present but it was null."
        );
        assertFalse(
                actualValue.trim().isEmpty(),
                "Expected field '" + jsonPath + "' to not be empty but it was blank."
        );
    }

    /**
     * Asserts that the response body contains a non-empty list at the given JSON path.
     *
     * @param jsonPath the JSON path expression (e.g. "data")
     */
    @Then("the response should contain a list of {string}")
    public void theResponseShouldContainAListOf(String jsonPath) {
        java.util.List<?> list = context.getLastResponse().jsonPath().getList(jsonPath);
        assertNotNull(list, "Expected a list at '" + jsonPath + "' but it was null.");
        assertFalse(list.isEmpty(), "Expected a non-empty list at '" + jsonPath + "' but it was empty.");
    }

    /**
     * Asserts that the response time is within the given threshold.
     *
     * @param milliseconds the maximum acceptable response time in milliseconds
     */
    @Then("the response time should be less than {int} milliseconds")
    public void theResponseTimeShouldBeLessThan(int milliseconds) {
        long actualTime = context.getLastResponse().time();
        assertTrue(
                actualTime < milliseconds,
                "Expected response time to be less than " + milliseconds +
                        "ms but was " + actualTime + "ms."
        );
    }
}