# =============================================================================
# FEATURE: Cart
# =============================================================================
# APPLICATION : SauceDemo (https://www.saucedemo.com)
# PAGE        : Cart Page (/cart.html)
# AUTHOR      : Andrew Cummins
# =============================================================================

@cart
Feature: Cart functionality

  As a logged in user
  I want to manage items in my shopping cart
  So that I can review and proceed to purchase

  Background:
    Given a "standard" user is on the "login" page
    When the user logs in

  @TC028 @regression @sanity
  Scenario: User can navigate to the cart
    When the user navigates to the cart
    Then the cart page should be displayed

  @TC029 @regression @sanity
  Scenario: Added product appears in cart
    When the user adds "Sauce Labs Backpack" to the cart
    And the user navigates to the cart
    Then the cart page should be displayed
    And the cart should contain 1 item(s)
    And the cart should contain "Sauce Labs Backpack"

  @TC030 @regression
  Scenario: Multiple products appear in cart
    When the user adds "Sauce Labs Backpack" to the cart
    And the user adds "Sauce Labs Bike Light" to the cart
    And the user navigates to the cart
    Then the cart should contain 2 item(s)
    And the cart should contain "Sauce Labs Backpack"
    And the cart should contain "Sauce Labs Bike Light"

  @TC031 @regression
  Scenario: User can remove a product from the cart
    When the user adds "Sauce Labs Backpack" to the cart
    And the user navigates to the cart
    And the user removes "Sauce Labs Backpack" from the cart
    Then the cart should contain 0 item(s)
    And the cart should not contain "Sauce Labs Backpack"

  @TC032 @regression
  Scenario: Cart displays checkout button
    When the user adds "Sauce Labs Backpack" to the cart
    And the user navigates to the cart
    Then the checkout button should be displayed

  @TC033 @regression
  Scenario: User can continue shopping from cart
    When the user adds "Sauce Labs Backpack" to the cart
    And the user navigates to the cart
    And the user continues shopping
    Then the inventory page should be displayed
