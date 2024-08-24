package builder;

import model.POCAccountVerificationRequest;

public class POCAccountVerificationRequestBuilder {
    public static POCAccountVerificationRequest buildPOCAccountVerificationRequest(String accountID) {
        return POCAccountVerificationRequest.of(accountID);
    }
}
