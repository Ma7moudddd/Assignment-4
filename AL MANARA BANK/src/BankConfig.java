public final class BankConfig {
    public static final int MAX_CUSTOMERS = 100;
    public static final int MAX_ACCOUNTS = 500;
    public static final int FIRST_CUSTOMER_ID = 1001;
    public static final int FIRST_ACCOUNT_SEQUENCE = 1;
    public static final double MIN_TRANSACTION_AMOUNT = 1.00;
    public static final double MAX_AMOUNT = 1_000_000_000.00;
    public static final double SAVINGS_BASE_RATE = 0.030;
    public static final double FIXED_DEPOSIT_BASE_RATE = 0.050;
    public static final double CURRENT_OVERDRAFT_LIMIT = 1000.00;
    public static final int MIN_FIXED_DEPOSIT_MONTHS = 1;
    public static final int MAX_FIXED_DEPOSIT_MONTHS = 120;
    public static final int MIN_PHONE_DIGITS = 7;
    public static final int MAX_PHONE_DIGITS = 15;

    private BankConfig() {
    }
}