# =============================================================================
# FEATURE: Login Functionality
# =============================================================================
# Tests covering the SauceDemo login page.
#
# TAGGING CONVENTION:
#   @TC[ID]       - unique test case identifier, one per scenario
#   @regression   - included in the full regression suite
#   @sanity       - included in the quick smoke/sanity suite
#   @WIP          - work in progress, excluded from regression runs
#   @login        - functional tag grouping all login-related tests
#
# RUNNING SPECIFIC TESTS:
#   All login tests    : set tags = "@login" in RunCukesTest.java
#   Single test        : set tags = "@TC001" in RunCukesTest.java
#   Sanity suite only  : set tags = "@sanity" in RunCukesTest.java
#   Exclude WIP        : set tags = "not @WIP" in RunCukesTest.java
# =============================================================================

@login
Feature: Login functionality

  As a user of the SauceDemo application
  I want to be able to log in with valid credentials
  So that I can access the product inventory

  # ---------------------------------------------------------------------------
  # HAPPY PATH SCENARIOS
  # ---------------------------------------------------------------------------

  @TC001 @regression @sanity
  Scenario: Standard user can log in successfully
    Given a "standard" user is on the "login" page
    When the user logs in
    Then the user should be on the "inventory" page
    And the inventory page should be displayed

  @TC002 @regression
  Scenario: Standard user lands on inventory page after login
    Given a "standard" user is on the "login" page
    When the user logs in
    Then the inventory page should be displayed

  # ---------------------------------------------------------------------------
  # NEGATIVE / ERROR SCENARIOS
  # ---------------------------------------------------------------------------

  @TC003 @regression @sanity
  Scenario: Locked out user cannot log in
    Given a "locked" user is on the "login" page
    When the user logs in
    Then an error message should be displayed
    And the error message should contain "Epic sadface: Sorry, this user has been locked out."

  @TC004 @regression
  Scenario: User cannot log in with empty username
    Given a "standard" user is on the "login" page
    When the user clicks the login button
    Then an error message should be displayed
    And the error message should contain "Epic sadface: Username is required"

  @TC005 @regression
  Scenario: User cannot log in with empty password
    Given a "standard" user is on the "login" page
    When the user enters their username
    And the user clicks the login button
    Then an error message should be displayed
    And the error message should contain "Epic sadface: Password is required"

  @TC006 @regression
  Scenario: Problem user can log in successfully
    Given a "problem" user is on the "login" page
    When the user logs in
    Then the user should be on the "inventory" page
    And the inventory page should be displayed

  @TC007 @regression
  Scenario: Performance glitch user can log in successfully
    Given a "performance_glitch" user is on the "login" page
    When the user logs in
    Then the user should be on the "inventory" page
    And the inventory page should be displayed

  # ---------------------------------------------------------------------------
  # FIELD VALIDATION SCENARIOS
  # ---------------------------------------------------------------------------

  @TC008 @regression
  Scenario: Error message is displayed when both fields are empty
    Given a "standard" user is on the "login" page
    When the user clicks the login button
    Then an error message should be displayed

  @TC009 @regression
  Scenario: User can log in after correcting invalid credentials
    Given a "locked" user is on the "login" page
    When the user logs in
    Then an error message should be displayed
    Given a "standard" user is on the "login" page
    When the user logs in
    Then the user should be on the "inventory" page

  # ---------------------------------------------------------------------------
  # WORK IN PROGRESS
  # ---------------------------------------------------------------------------

  @TC010 @WIP
  Scenario: User session persists after page refresh
    Given a "standard" user is on the "login" page
    When the user logs in
    Then the user should be on the "inventory" page
