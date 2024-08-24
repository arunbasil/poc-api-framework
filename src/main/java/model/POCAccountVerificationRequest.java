
package model;

public record POCAccountVerificationRequest(String accountId) {

    public static POCAccountVerificationRequest of(String accountId) {
        return new POCAccountVerificationRequest(accountId);
    }
}
