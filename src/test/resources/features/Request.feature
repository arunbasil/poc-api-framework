Feature: API Request Validation

  Scenario Outline: Verify account ID returns correct response
    When a matching request is sent with "<accountId>"
    Then the response status code should be <statusCode>
    And the response body should contain "<name>"

    Examples:
      | accountId | statusCode | name        |
      | 001       | 200        | Arun Basil |
      | 002       | 200        | Ann Basil   |
      | 003       | 400        | Bad Request for Account ID 003 |
      | 004       | 500        | Internal Server Error for Account ID 004 |


#Feature: API Request Validation
#
#  Scenario: Verify account ID 001 returns 200 with Arun Basil
#    When a matching request is sent with "001"
#    Then the response status code should be 200
#    And the response body should contain "Arun Basil"
#
#  Scenario: Verify account ID 002 returns 200 with Ann Basil
#    When a matching request is sent with "002"
#    Then the response status code should be 200
#    And the response body should contain "Ann Basil"
#
#  Scenario: Verify account ID 003 returns 400
#    When a matching request is sent with "003"
#    Then the response status code should be 400
#    And the response body should contain "Bad Request for Account ID 003"
#
#  Scenario: Verify account ID 004 returns 500
#    When a matching request is sent with "004"
#    Then the response status code should be 500
#    And the response body should contain "Internal Server Error for Account ID 004"
##
