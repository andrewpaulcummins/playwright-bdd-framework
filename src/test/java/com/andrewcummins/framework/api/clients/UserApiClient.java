package com.andrewcummins.framework.api.clients;

import com.andrewcummins.framework.context.ScenarioContext;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.restassured.AllureRestAssured;

/**
 * API client for the ReqRes {@code /users} endpoints.
 *
 * <p>Encapsulates all REST Assured request logic for user-related API operations.
 * Each method performs a single HTTP request and stores the response in
 * {@code ScenarioContext} for assertion in step definitions.</p>
 *
 * <p>The Allure filter is applied to every request, automatically attaching
 * request and response details to the Allure report.</p>
 */
public class UserApiClient {

    private final ScenarioContext context;
    private final String baseUrl;
    private final String apiKey;

    /**
     * Constructs a new {@code UserApiClient} with the given {@code ScenarioContext}.
     *
     * @param context the {@code ScenarioContext} for the current scenario
     */
    public UserApiClient(ScenarioContext context) {
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
    // GET REQUESTS
    // =========================================================================

    /**
     * Sends a GET request to {@code /users} and retrieves a paginated list of users.
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     *
     * @param page the page number to retrieve (1-based)
     */
    public void getUsers(int page) {
        Response response = baseRequest()
                .queryParam("page", page)
                .when()
                .get("/users")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("GET /users?page=" + page, response);
    }

    /**
     * Sends a GET request to {@code /users/{id}} and retrieves a single user.
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     *
     * @param userId the ID of the user to retrieve
     */
    public void getUserById(int userId) {
        Response response = baseRequest()
                .when()
                .get("/users/" + userId)
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("GET /users/" + userId, response);
    }

    /**
     * Sends a GET request for a user ID that does not exist,
     * expecting a 404 response.
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     */
    public void getUserNotFound() {
        Response response = baseRequest()
                .when()
                .get("/users/9999")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("GET /users/9999", response);
    }

    // =========================================================================
    // POST REQUESTS
    // =========================================================================

    /**
     * Sends a POST request to {@code /users} to create a new user.
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     *
     * @param name the name of the user to create
     * @param job  the job title of the user to create
     */
    public void createUser(String name, String job) {
        String requestBody = String.format("{\"name\": \"%s\", \"job\": \"%s\"}", name, job);

        Response response = baseRequest()
                .body(requestBody)
                .when()
                .post("/users")
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("POST /users", response);
    }

    // =========================================================================
    // PUT REQUESTS
    // =========================================================================

    /**
     * Sends a PUT request to {@code /users/{id}} to fully update a user.
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     *
     * @param userId the ID of the user to update
     * @param name   the updated name
     * @param job    the updated job title
     */
    public void updateUser(int userId, String name, String job) {
        String requestBody = String.format("{\"name\": \"%s\", \"job\": \"%s\"}", name, job);

        Response response = baseRequest()
                .body(requestBody)
                .when()
                .put("/users/" + userId)
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("PUT /users/" + userId, response);
    }

    // =========================================================================
    // DELETE REQUESTS
    // =========================================================================

    /**
     * Sends a DELETE request to {@code /users/{id}} to delete a user.
     *
     * <p>Stores the response in {@code ScenarioContext} for assertion.</p>
     *
     * @param userId the ID of the user to delete
     */
    public void deleteUser(int userId) {
        Response response = baseRequest()
                .when()
                .delete("/users/" + userId)
                .then()
                .extract()
                .response();

        context.setLastResponse(response);
        logResponse("DELETE /users/" + userId, response);
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
        System.out.println("[UserApiClient] " + endpoint +
                " → " + response.statusCode() +
                " (" + response.time() + "ms)");
    }
}