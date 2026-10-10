import java.util.Locale;

public class SavingsAccount extends Account {
    private final double annualInterestRate;
    private int monthlyWithdrawalCount;

    public SavingsAccount(String accountNumber, int ownerId, double openingBalance, double annualInterestRate) {
        super(accountNumber, ownerId, openingBalance);
        this.annualInterestRate = annualInterestRate;
    }

    @Override
    protected void checkWithdrawalRule(double amount) {
        if (Money.round(getBalance() - amount) < 0) {
            throw new BankException("Insufficient funds. A savings account cannot go below zero. Available balance: "
                    + Money.format(getBalance()) + ".");
        }
    }

    @Override
    protected void afterWithdrawal() {
        monthlyWithdrawalCount++;
    }

    @Override
    protected void afterWithdrawalReversed() {
        if (monthlyWithdrawalCount > 0) {
            monthlyWithdrawalCount--;
        }
    }

    @Override
    public void onMonthEnd() {
        monthlyWithdrawalCount = 0;
    }

    @Override
    public AccountType getType() {
        return AccountType.SAVINGS;
    }

    @Override
    public String[] describeSpecifics() {
        return new String[] {
                String.format(Locale.US, "Annual interest rate : %.2f%%", annualInterestRate * 100),
                "Withdrawals this month: " + monthlyWithdrawalCount
        };
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }

    public int getMonthlyWithdrawalCount() {
        return monthlyWithdrawalCount;
    }
}