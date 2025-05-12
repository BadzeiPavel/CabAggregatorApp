Feature: Ride Service operations

  Scenario: Get ride by ID
    Given I have a ride with ID "123e4567-e89b-12d3-a456-426614174000"
    When I fetch the ride by ID
    Then I should receive the ride with ID "123e4567-e89b-12d3-a456-426614174000"

  Scenario: Update ride details
    Given I have a ride with ID "123e4567-e89b-12d3-a456-426614174000"
    And I have updated the ride data
    When I update the ride
    Then I should receive the updated ride with the new details

  Scenario: Change ride status to "ACCEPTED"
    Given I have a ride with ID "123e4567-e89b-12d3-a456-426614174000"
    When I change the ride status to "ACCEPTED"
    Then the ride status should be "ACCEPTED"
