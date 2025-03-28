Feature: Payment Service
  As a user
  I want to manage payments
  So that I can create, retrieve, delete, and process payments

  Scenario: Create a payment successfully
    Given a valid payment request
    When I send a request to create a payment
    Then the payment should be created successfully
    And the payment status should be PENDING

  Scenario: Retrieve an existing payment
    Given a payment exists with ride ID "ride123"
    When I request payment details for ride ID "ride123"
    Then I should receive the correct payment details

  Scenario: Delete a payment
    Given a payment exists with ride ID "ride123"
    When I request to delete the payment with ride ID "ride123"
    Then the payment should be deleted successfully
