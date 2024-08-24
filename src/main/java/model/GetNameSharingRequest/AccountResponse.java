package model.GetNameSharingRequest;


import java.util.List;

public record AccountResponse(String customerNumber, Account account, List<AccountName> accountNames) {}

