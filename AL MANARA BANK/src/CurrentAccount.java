public class CurrentAccount extends Account {
    private final double overdraftLimit;

    public CurrentAccount(String accountNumber, int ownerId, double openingBalance, double overdraftLimit) {
        super(accountNumber, ownerId, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    protected void checkWithdrawalRule(double amount) {
        if (Money.round(getBalance() - amount) < -overdraftLimit) {
            throw new BankException("Overdraft limit exceeded. The most you can withdraw now is "
                    + Money.format(getBalance() + overdraftLimit) + ".");
        }
    }

    public boolean isUsingOverdraft() {
        return getBalance() < 0;
    }

    public double getOverdraftUsed() {
        return isUsingOverdraft() ? -getBalance() : 0;
    }

    @Override
    public AccountType getType() {
        return AccountType.CURRENT;
    }

    @Override
    public String[] describeSpecifics() {
        return new String[] {
                "Overdraft limit      : " + Money.format(overdraftLimit),
                "Using overdraft      : " + (isUsingOverdraft() ? "Yes" : "No"),
                "Overdraft used       : " + Money.format(getOverdraftUsed())
        };
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }
}