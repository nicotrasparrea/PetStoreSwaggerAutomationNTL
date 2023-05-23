@userSuite
Feature: Validate users

  Scenario Outline: Create a list of users with array
    Given we send the post request that adds users given an array with username "<username>"
    And we validate the response is 200 for user
    Then we validate the body response contains the username "<username>"

    Examples:
      | username       |
      | TestUsernameTL |

  Scenario Outline: Create a list of users with list
    Given we send the post request that adds users given a list with username "<username>"
    And we validate the response is 200 for user
    Then we validate the body response contains the username "<username>"

    Examples:
      | username       |
      | TestUsernameTL |

  Scenario Outline: Add a new user
    Given we send the post request that adds a user with name "<username>"
    And we validate the response is 200 for user
    And we validate the body contains key name
    Then we validate the body response contains the pet name "<username>"

    Examples:
      | username       |
      | TestUsernameTL |

  Scenario Outline: Update an existing user
    Given we send the post request that adds a user with name "<username>"
    When we send the put request that updates users with new name "<updatedUsermame>"
    And we validate the response is 200 for user
    Then we validate the body response contains the username "<updatedUsername>"

    Examples:
      | username       | updatedUsername |
      | TestUsernameTL | UpdatedTL       |

  Scenario Outline: Delete a user by username
    Given we send the post request that adds a user with username "<username>"
    When we send the delete request that deletes a  user by username "<username>"
    And we validate the response is 200 for user
    Then we validate the body response contains the same username "<username>"

    Examples:
      | username       |
      | TestUsernameTL |

  Scenario Outline: Find an existing user by username
    Given we send the get request that returns the user filtered by username "<username>"
    And we validate the response is 200 for user
    Then we validate the body response contains the user with ID "<userId>"

    Examples:
      | username            | userId              |
      | FixedTestUsernameTL | 9223372036854754188 |

  Scenario Outline: Login an existing user
    Given we send the get request that logins a user with username "<username>" and password "<password>"
    And we validate the response is 200 for user
    Then we validate the body response contains a message with text "logged in user session:"

    Examples:
      | username            | password |
      | FixedTestUsernameTL | abc123   |

  Scenario Outline: Logout current session
    Given we send the get request that logins a user with username "<username>" and password "<password>"
    When we send the get request that logouts a user
    And we validate the response is 200 for user
    Then we validate the body response contains a message with text "ok"

    Examples:
      | username            | password |
      | FixedTestUsernameTL | abc123   |