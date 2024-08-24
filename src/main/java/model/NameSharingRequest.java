package model;

public record NameSharingRequest(String accountNumber) {

    public static NameSharingRequest of(String accountNumber) {
        return new NameSharingRequest(accountNumber);
    }
}
