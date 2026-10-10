public enum AccountType {
    SAVINGS("Savings", "SAV", 100.00),
    CURRENT("Current", "CUR", 50.00),
    FIXED_DEPOSIT("Fixed Deposit", "FDA", 1000.00);

    private final String label;
    private final String prefix;
    private final double minimumOpeningBalance;

    AccountType(String label, String prefix, double minimumOpeningBalance) {
        this.label = label;
        this.prefix = prefix;
        this.minimumOpeningBalance = minimumOpeningBalance;
    }

    public String getLabel() {
        return label;
    }

    public String getPrefix() {
        return prefix;
    }

    public double getMinimumOpeningBalance() {
        return minimumOpeningBalance;
    }
}