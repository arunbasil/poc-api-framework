Feature: Validate Joint Customer Details for Sharing API

  Scenario Outline: Validate account and customer details for a joint customer
    Given a name sharing request is made for account "<accountNumber1>"
    Then the joint customer number corresponds to "<jointCustomerNumber>"
    And the account number corresponds to "<accountNumber>"
    And the formatted account number corresponds to "<formattedAccountNumber>"
    Then the following customer details are correct:
      | customerNumber | accountName  |
      | <customerNumber1> | <accountName1> |
      | <customerNumber2> | <accountName2> |
      | <customerNumber3> | <accountName3> |
      # Add more rows as needed...

    Examples:
      | accountNumber1                        | jointCustomerNumber | accountNumber         | formattedAccountNumber | customerNumber1 | accountName1  | customerNumber2 | accountName2  | customerNumber3 | accountName3  |
      | eyJjb2RlIjoiQyIsImVkIjoiMDAwMDIwNTEy  | 987654              | 020512000867933       | 02-0512-000867-933     | 111111          | John Smith    | 222222          | Jane Smith    | 333333          | James Smith   |
    # Add more examples if needed 020512000867933
