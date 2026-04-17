# =============================================================================
# FEATURE: Checkout
# =============================================================================
# APPLICATION : SauceDemo (https://www.saucedemo.com)
# PAGES       : Checkout (/checkout-step-one.html)
#               Overview (/checkout-step-two.html)
#               Complete (/checkout-complete.html)
# AUTHOR      : Andrew Cummins
# =============================================================================

@checkout
Feature: Checkout functionality

  As a logged in user with items in my cart
  I want to complete the checkout process
  So that I can place an order

  Background:
    Given a "standard" user is on the "login" page
    When the user logs in
    And the user adds "Sauce Labs Backpack" to the cart
    And the user navigates to the cart
    And the user proceeds to checkout

  @TC034 @regression @sanity
  Scenario: Checkout page is displayed after proceeding from cart
    Then the checkout page should be displayed

  @TC035 @regression @sanity
  Scenario: User can complete a full checkout
    When the user enters checkout details "Andrew" "Cummins" "E91"
    And the user clicks continue on checkout
    Then the overview page should be displayed
    And the overview should contain "Sauce Labs Backpack"
    When the user clicks finish
    Then the order confirmation page should be displayed
    And the confirmation header should contain "Thank you for your order"

  @TC036 @regression
  Scenario: Checkout shows error when first name is missing
    When the user clicks continue on checkout
    Then the checkout error message should contain "First Name is required"

  @TC037 @regression
  Scenario: Checkout shows error when last name is missing
    When the user enters checkout details "Andrew" "" "E91"
    And the user clicks continue on checkout
    Then the checkout error message should contain "Last Name is required"

  @TC038 @regression
  Scenario: Checkout shows error when postal code is missing
    When the user enters checkout details "Andrew" "Cummins" ""
    And the user clicks continue on checkout
    Then the checkout error message should contain "Postal Code is required"

  @TC039 @regression
  Scenario: Overview page displays correct order summary
    When the user enters checkout details "Andrew" "Cummins" "E91"
    And the user clicks continue on checkout
    Then the overview page should be displayed
    And the overview should contain "Sauce Labs Backpack"

  @TC040 @regression
  Scenario: User can navigate back to products after completing order
    When the user enters checkout details "Andrew" "Cummins" "E91"
    And the user clicks continue on checkout
    And the user clicks finish
    Then the order confirmation page should be displayed
    When the user clicks back to products
    Then the inventory page should be displayed
