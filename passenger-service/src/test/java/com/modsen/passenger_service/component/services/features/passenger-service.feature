Feature: Passenger Service

  Scenario: Create a new passenger
    Given a passenger DTO is provided with all the required fields
    When the passenger is created
    Then the passenger should be saved in the repository
    And the created passenger's ID should not be null

  Scenario: Retrieve a passenger by ID
    Given a passenger exists with ID "123e4567-e89b-12d3-a456-426614174000"
    When the passenger is requested by ID "123e4567-e89b-12d3-a456-426614174000"
    Then the passenger details are returned
