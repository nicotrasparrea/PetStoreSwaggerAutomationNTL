@petSuite
Feature: Validate pets

  @addPetOK
  Scenario Outline: Add a new pet to the store
    Given we send the post request that adds a pet with name "<petName>"
    And we validate the response is 200 for pet
    And we validate the body contains key name
    Then we validate the body response contains the pet name "<petName>"

    Examples:
      | petName   |
      | PostPetTL |

  @updatePetOK
  Scenario Outline: Update an existing pet
    Given we send the post request that adds a pet with name "<petName>"
    And we validate the response is 200 for pet
    When we send the put request that updates pets with new name "<updatedName>"
    And we validate the response is 200 for pet
    Then we validate the body response contains the pet name "<updatedName>"

    Examples:
      | petName   | updatedName  |
      | TestPetTL | UpdatedPetTL |

  @findPetStatus
  Scenario Outline: Find pets by valid given status
    Given we send the post request that adds a pet with name "<petName>" and status "<status>"
    When we send the get request that returns the pets by status "<status>"
    And we validate the response is 200 for pet
    And we validate the body response contains objects with status "<status>"
    Then we validate the body response contains the pet name "<petName>" in the list
    @simple
    Examples:
      | petName   | status    |
      | TestPetTL | available |

    @complete
    Examples:
      | petName   | status    |
      | TestPetTL | available |
      | TestPetTL | pending   |
      | TestPetTL | sold      |

  @findPetId
  Scenario Outline: Find existing pet by ID
    Given we send the post request that adds a pet with name "<petName>"
    When we send the get request that returns the pet filtered by ID
    And we validate the response is 200 for pet
    And we validate the body response contains the pet name "<petName>"
    Then we validate the body response contains the expected ID

    Examples:
      | petName    |
      | TestPetNTL |

  @deletePetOK @delete
  Scenario: Delete an existing pet
    Given we send the post request that adds a pet with name "anyNameTL"
    When we send the delete request that deletes a pet by an ID
    And we validate the response is 200 for pet
    Then we validate the body response contains the same ID

