Feature: Driver Rating Service

  Scenario: Create a new driver rating
    Given a valid rating request for driver "driver123" with passenger "passenger456"
    When I send a request to create the rating
    Then the rating should be created successfully
    And the rating status should be "PENDING"

  Scenario: Retrieve an existing driver rating by ID
    Given a driver rating exists with ID "123" for retrieval
    When I request to retrieve the rating with ID "123"
    Then I should receive the correct rating details

  Scenario: Get paginated ratings for driver
    Given ratings exist for driver "driver123"
    When I request paginated ratings for driver "driver123"
    Then I should receive a paginated list of ratings for the driver
    And the response should include the total number of pages and elements

  Scenario: Update an existing driver rating
    Given a driver rating exists with ID "123" for update
    When I send a request to update the rating with ID "123" to a rating of 4
    Then the rating should be updated successfully
    And the rating should reflect the updated rating of 4

  Scenario: Patch an existing driver rating
    Given a driver rating exists with ID "123" for patching
    When I send a patch request to update the rating with ID "123" to a rating of 4
    Then the rating should be patched successfully
    And the rating should reflect the patched rating of 4

  Scenario: Soft delete an existing driver rating
    Given a driver rating exists with ID "123" for deletion
    When I send a request to soft delete the rating with ID "123"
    Then the rating should be marked as deleted
    And the rating status should be "DELETED" on delete

  Scenario: Calculate the average rating for a driver
    Given ratings exist for driver "driver123" for average calculation
    When I request the average rating for driver "driver123"
    Then I should receive the correct average rating for the driver
