import java.util.Locale;

public class FixedDepositAccount extends Account {
    private final double annualInterestRate;
    private final int durationMonths;
    private int elapsedMonths;

    public FixedDepositAccount(String accountNumber, int ownerId, double openingBalance,
                               double annualInterestRate, int durationMonths) {
        super(accountNumber, ownerId, openingBalance);
        this.annualInterestRate = annualInterestRate;
        this.durationMonths = durationMonths;
        this.elapsedMonths = 0;
    }

    public boolean isMatured() {
        return elapsedMonths >= durationMonths;
    }

    public int getRemainingMonths() {
        return Math.max(0, durationMonths - elapsedMonths);
    }

    @Override
    protected void checkWithdrawalRule(double amount) {
        if (!isMatured()) {
            throw new BankException("This fixed deposit has not matured yet. Months remaining: "
                    + getRemainingMonths() + ".");
        }
        if (Money.round(getBalance() - amount) < 0) {
            throw new BankException("Insufficient funds. Available balance: " + Money.format(getBalance()) + ".");
        }
    }

    @Override
    protected void checkClosureRule() {
        if (!isMatured()) {
            throw new BankException("This fixed deposit is still locked. Months remaining: "
                    + getRemainingMonths() + ".");
        }
    }

    @Override
    public void onMonthEnd() {
        if (getStatus() != AccountStatus.CLOSED && elapsedMonths < durationMonths) {
            elapsedMonths++;
        }
    }

    @Override
    public AccountType getType() {
        return AccountType.FIXED_DEPOSIT;
    }

    @Override
    public String[] describeSpecifics() {
        return new String[] {
                String.format(Locale.US, "Annual interest rate : %.2f%%", annualInterestRate * 100),
                "Duration             : " + durationMonths + " months",
                "Months elapsed       : " + elapsedMonths,
                "Months remaining     : " + getRemainingMonths(),
                "Matured              : " + (isMatured() ? "Yes" : "No")
        };
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public int getElapsedMonths() {
        return elapsedMonths;
    }

    public double getAnnualInterestRate() {
        return annualInterestRate;
    }
}