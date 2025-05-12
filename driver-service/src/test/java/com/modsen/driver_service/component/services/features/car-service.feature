Feature: Car Service

  Scenario: Create a new car
    Given a car with number "ABC123", brand "Toyota", model "Camry", color "Blue", category "ECONOMY" and 4 seats
    When the car is saved
    Then the car should be successfully created

  Scenario: Get a car by ID
    Given a car exists with ID "d290f1ee-6c54-4b01-90e6-d701748f0851"
    When the car is retrieved by ID
    Then the correct car details should be returned

  Scenario: Update a car
    Given an existing car with ID "d290f1ee-6c54-4b01-90e6-d701748f0851"
    When the car's color is updated to "Red"
    Then the car should reflect the updated color


  Scenario: Delete a car
    Given an existing car with ID "d290f1ee-6c54-4b01-90e6-d701748f0851"
    When the car is deleted
    Then the car should be marked as deleted
