package com.andrewcummins.framework.api.clients;

import com.andrewcummins.framework.context.ScenarioContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

/**
 * API client for the ReqRes {@code /login} and {@code /register} endpoints.
 *
 * <p>Encapsulates all REST Assured request logic for authentication-related
 * API operations. Each method performs a single HTTP request and stores the
 * response in {@code ScenarioContext} for assertion in step definitions.</p>
 */
public class AuthApiClient {

    private final ScenarioContext context;
    private final String baseUrl;
    private final String apiKey;

    /**
     * Constructs a new {@code AuthApiClient} with the given {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public AuthApiClient(ScenarioContext context) {
        this.context = context;
        this.baseUrl = context.getConfigReader().getApiBaseUrl();
        this.apiKey = context.getConfigReader().getApiKey();
    }

    // =========================================================================
    // REQUEST SPECIFICATION
    // =========================================================================

    /**
     * Returns a base {@code RequestSpecification} with common configuration
     * applied — base URI, content type, and Allure filter.
     *
     * @return a configured {@code RequestSpecification}
     */
    private RequestSpecification baseRequest() {
        return RestAssured.given()
                .filter(new AllureRestAssured())
                .baseUri(baseUrl)
                .header("x-api-key", apiKey)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    // =========================================================================
    // LOGIN
    // =========================================================================

    /**
     * Sends a POST request to {@code /login} with valid credentials.
     *
     * <p>ReqRes only accepts a predefined set of email addresses.
     * Use {@code eve.holt@reqres.in} with password {@code cityslicka}
     * for a successful login response.</p>
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     *
     * @param email    the email address to authenticate with
     * @param password the password to authenticate with
     */
    public void login(String email, String password) {
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        Response response = baseRequest()
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("POST /login", response);
    }

    /**
     * Sends a POST request to {@code /login} with a missing password,
     * expecting a 400 response with an error message.
     *
     * @param email the email address to authenticate with
     */
    public void loginWithoutPassword(String email) {
        String requestBody = String.format("{\"email\": \"%s\"}", email);

        Response response = baseRequest()
                .body(requestBody)
                .when()
                .post("/login")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("POST /login (no password)", response);
    }

    // =========================================================================
    // REGISTER
    // =========================================================================

    /**
     * Sends a POST request to {@code /register} with valid credentials.
     *
     * <p>ReqRes only accepts a predefined set of email addresses.
     * Use {@code eve.holt@reqres.in} with password {@code pistol}
     * for a successful registration response.</p>
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     *
     * @param email    the email address to register with
     * @param password the password to register with
     */
    public void register(String email, String password) {
        String requestBody = String.format(
                "{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        Response response = baseRequest()
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("POST /register", response);
    }

    /**
     * Sends a POST request to {@code /register} with a missing password,
     * expecting a 400 response with an error message.
     *
     * @param email the email address to register with
     */
    public void registerWithoutPassword(String email) {
        String requestBody = String.format("{\"email\": \"%s\"}", email);

        Response response = baseRequest()
                .body(requestBody)
                .when()
                .post("/register")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("POST /register (no password)", response);
    }

    // =========================================================================
    // PRIVATE HELPERS
    // =========================================================================

    /**
     * Logs the HTTP method, endpoint, and response status to the console.
     *
     * @param endpoint the endpoint called
     * @param response the response received
     */
    private void logResponse(String endpoint, Response response) {
        System.out.println("[AuthApiClient] " + endpoint +
                " → " + response.statusCode() +
                " (" + response.time() + "ms)");
    }
}