@inventory
Feature: Inventory page functionality

  As a logged in user
  I want to browse and interact with the product inventory
  So that I can find and add products to my cart

  Background:
    Given a "standard" user is on the "login" page
    When the user logs in

  @TC021 @regression @sanity
  Scenario: Inventory page displays correct number of products
    Then the inventory page should display 6 products

  @TC022 @regression
  Scenario: Products can be sorted alphabetically ascending
    When the user sorts products by "Name (A to Z)"
    Then the products should be sorted alphabetically ascending

  @TC023 @regression
  Scenario: Products can be sorted alphabetically descending
    When the user sorts products by "Name (Z to A)"
    Then the products should be sorted alphabetically descending

  @TC024 @regression
  Scenario: Products can be sorted by price low to high
    When the user sorts products by "Price (low to high)"
    Then the products should be sorted by price ascending

  @TC025 @regression
  Scenario: Products can be sorted by price high to low
    When the user sorts products by "Price (high to low)"
    Then the products should be sorted by price descending

  @TC026 @regression @sanity
  Scenario: Adding a product updates the cart badge
    Then the cart badge should not be displayed
    When the user adds "Sauce Labs Backpack" to the cart
    Then the cart badge should show 1

  @TC027 @regression
  Scenario: Adding multiple products updates the cart badge count
    When the user adds "Sauce Labs Backpack" to the cart
    And the user adds "Sauce Labs Bike Light" to the cart
    Then the cart badge should show 2
