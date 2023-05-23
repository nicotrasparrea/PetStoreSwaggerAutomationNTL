@userSuite
Feature: Validate users

#  Scenario Outline: Create a list of users with array
#    Given we send the post request that adds users given an array with username "<username>"
#    And we validate the response is 200 for user
#    Then we validate the body response contains the username "<username>"
#
#    Examples:
#      | username       |
#      | TestUsernameTL |
  @addUser
  Scenario Outline: Add a new user
    Given we send the post request that adds a user with username "<username>"
    And we validate the response is 200 for user
    Then we validate the body response contains the field "message"

    Examples:
      | username       |
      | TestUsernameTL |

  @updateUser
  Scenario Outline: Update an existing user
    Given we send the post request that adds a user with username "<username>"
    When we send the put request that updates users with new name "<updatedUsername>"
    And we validate the response is 200 for user
    Then we validate the body response contains the username "<updatedUsername>"

    Examples:
      | username       | updatedUsername |
      | TestUsernameTL | UpdatedTL       |

  @deleteUser
  Scenario Outline: Delete a user by username
    Given we send the post request that adds a user with username "<username>"
    When we send the delete request that deletes a  user by username "<username>"
    And we validate the response is 200 for user
    Then we validate the delete body response contains the username "<username>"

    Examples:
      | username       |
      | TestUsernameTL |

  @getUser
  Scenario Outline: Find an existing user by username
    Given we send the post request that adds a user with username "<username>"
    When we send the get request that returns the user filtered by username "<username>"
    And we validate the response is 200 for user
    Then we validate the body response contains the user with expected ID

    Examples:
      | username       |
      | TestUsernameTL |