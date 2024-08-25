Feature: Account Information Verification

  Scenario: Verify account information for multiple customers
    Given data is loaded from excel "src/test/resources/account_info.xlsx" and sheet "Sheet1"
    Then perform account verification
