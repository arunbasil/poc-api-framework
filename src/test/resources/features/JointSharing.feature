Feature: Account Information Verification

  Scenario Outline: Verify account information for multiple customers
    Given a name sharing request is made for account "<accountNumber1>"
    Then the joint customer number corresponds to "<jointCustomerNumber>"
    And the account number corresponds to "<accountNumber>"
    And the formatted account number corresponds to "<formattedAccountNumber>"
    Then the following customer details are correct:
      | customerNumber  | accountName   |
      | <customerNumber1> | <accountName1> |
      | <customerNumber2> | <accountName2> |
      | <customerNumber3> | <accountName3> |
      | <customerNumber4> | <accountName4> |

    Examples:
      | accountNumber1                        | jointCustomerNumber | accountNumber         | formattedAccountNumber | customerNumber1 | accountName1  | customerNumber2 | accountName2  | customerNumber3 | accountName3  | customerNumber4 | accountName4  |
      | eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTEy  | 987654              | 020512000867933       | 02-0512-000867-933     | 111111          | John Smith    | 222222          | Jane Smith    | 333333          | James Smith   | 444444          | Joe Smith     |
      | eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTEz  | 987653              | 020512000867931       | 02-0512-000867-931     | 111112          | Arun Basil    | 222221          | Ann Basil     |                 |               |                 |               |
      | eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTE0  | 987652              | 020512000867932       | 02-0512-000867-932     | 111113          | Alice Smith   | 222223          | Bob Smith     | 333334          | Carol Smith   |                 |               |

