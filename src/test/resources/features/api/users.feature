@api
Feature: ReqRes API — Users and Authentication

  Background:
    Given the ReqRes API is available

  # ===========================================================================
  # GET /users
  # ===========================================================================

  @TC010 @users @regression @sanity
  Scenario: Retrieve first page of users successfully
    When the client requests page 1 of users
    Then the response status code should be 200
    And the response should contain a list of "data"
    And the response field "page" should be "1"
    And the response time should be less than 5000 milliseconds

  @TC011 @users @regression
  Scenario: Retrieve second page of users successfully
    When the client requests page 2 of users
    Then the response status code should be 200
    And the response should contain a list of "data"
    And the response field "page" should be "2"

  # ===========================================================================
  # GET /users/{id}
  # ===========================================================================

  @TC012 @users @regression @sanity
  Scenario: Retrieve a single user by ID successfully
    When the client requests user with ID 2
    Then the response status code should be 200
    And the response field "data.id" should be "2"
    And the response field "data.email" should be "janet.weaver@reqres.in"
    And the response field "data.first_name" should be "Janet"
    And the response field "data.last_name" should be "Weaver"
    And the response time should be less than 5000 milliseconds

  @TC013 @users @regression
  Scenario: Request a user that does not exist returns 404
    When the client requests a user that does not exist
    Then the response status code should be 404

  # ===========================================================================
  # POST /users
  # ===========================================================================

  @TC014 @users @regression @sanity
  Scenario: Create a new user successfully
    When the client creates a user with name "Andrew" and job "SDET"
    Then the response status code should be 201
    And the response field "name" should be "Andrew"
    And the response field "job" should be "SDET"
    And the response field "id" should not be empty
    And the response field "createdAt" should not be empty
    And the response time should be less than 5000 milliseconds

  @TC015 @users @regression
  Scenario: Create a user with different attributes
    When the client creates a user with name "Jane" and job "Product Manager"
    Then the response status code should be 201
    And the response field "name" should be "Jane"
    And the response field "job" should be "Product Manager"
    And the response field "id" should not be empty

  # ===========================================================================
  # PUT /users/{id}
  # ===========================================================================

  @TC016 @users @regression
  Scenario: Update an existing user successfully
    When the client updates user 2 with name "Andrew Updated" and job "Senior SDET"
    Then the response status code should be 200
    And the response field "name" should be "Andrew Updated"
    And the response field "job" should be "Senior SDET"
    And the response field "updatedAt" should not be empty
    And the response time should be less than 5000 milliseconds

  # ===========================================================================
  # DELETE /users/{id}
  # ===========================================================================

  @TC017 @users @regression @sanity
  Scenario: Delete a user successfully
    When the client deletes user with ID 2
    Then the response status code should be 204
    And the response time should be less than 5000 milliseconds

  # ===========================================================================
  # POST /login
  # ===========================================================================

  @TC018 @auth @regression @sanity
  Scenario: Successful login returns a token
    When the client logs in with email "eve.holt@reqres.in" and password "cityslicka"
    Then the response status code should be 200
    And the response field "token" should not be empty
    And the response time should be less than 5000 milliseconds

  @TC019 @auth @regression
  Scenario: Login without password returns 400 with error message
    When the client logs in with email "eve.holt@reqres.in" and no password
    Then the response status code should be 400
    And the response field "error" should be "Missing password"

  # ===========================================================================
  # POST /register
  # ===========================================================================

  @TC020 @auth @regression
  Scenario: Successful registration returns an ID and token
    When the client registers with email "eve.holt@reqres.in" and password "pistol"
    Then the response status code should be 200
    And the response field "id" should not be empty
    And the response field "token" should not be empty
    And the response time should be less than 5000 milliseconds