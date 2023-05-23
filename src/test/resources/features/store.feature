@storeSuite
Feature: Validate store

  @addOrderOK
  Scenario Outline: Place an order for an existing pet
    Given we send the post request that adds an order with pet ID <petId>
    And we validate the response is 200 for store
    And we validate the body contains a ship date with same date as current
    Then we validate the body response contains the pet ID <petId>

    Examples:
      | petId               |
      | 9223372036854775807 |

  @findOrderId
  Scenario Outline: Find purchase order by ID
    Given we send the post request that adds an order with pet ID <petId>
    When we send the get request that returns an order by ID
    And we validate the response is 200 for store
    And we validate the body response contains the pet ID <petId>
    Then we validate the body response contains the order ID

    Examples:
      | petId               |
      | 9223372036854775807 |

  @deleteOrderIdOK
  Scenario Outline: Delete purchase order by ID
    Given we send the post request that adds an order with pet ID <petId>
    When we send the delete request that deletes an order by an ID
    And we validate the response is 200 for store
    Then we validate the body response contains the order ID

    Examples:
      | petId               |
      | 9223372036854775807 |

  @findInventoriesStatus
  Scenario Outline: Find pet inventories by status
    Given we send the get request that returns a list of status
    And we validate the response is 200 for store
    Then we validate the response body contains the status "<status>"

    Examples:
      | status    |
      | available |
