package enums;

public enum ReasonCode {
    ACCT_NAME_NO_MATCH("ANNM"),
    ACCT_NAME_CLOSE_MATCH("MBAM");

    private final String label;

    ReasonCode(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
