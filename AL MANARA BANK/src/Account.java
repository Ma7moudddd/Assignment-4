public abstract class Account {
    private final String accountNumber;
    private final int ownerId;
    private double balance;
    private AccountStatus status;
    private int transactionCount;

    protected Account(String accountNumber, int ownerId, double openingBalance) {
        this.accountNumber = accountNumber;
        this.ownerId = ownerId;
        this.balance = Money.round(openingBalance);
        this.status = AccountStatus.ACTIVE;
        this.transactionCount = 0;
    }

    public final void requireCanReceive(double amount) {
        requireTransactionsAllowed();
        requireValidAmount(amount);
    }

    public final void deposit(double amount) {
        requireCanReceive(amount);
        balance = Money.round(balance + amount);
        transactionCount++;
    }

    public final void withdraw(double amount) {
        requireTransactionsAllowed();
        requireValidAmount(amount);
        checkWithdrawalRule(amount);
        balance = Money.round(balance - amount);
        transactionCount++;
        afterWithdrawal();
    }

    public final void reverseWithdrawal(double amount) {
        balance = Money.round(balance + amount);
        transactionCount--;
        afterWithdrawalReversed();
    }

    public final void close() {
        if (status == AccountStatus.CLOSED) {
            throw new BankException("Account " + accountNumber + " is already closed.");
        }
        if (!Money.isZero(balance)) {
            throw new BankException("Account " + accountNumber + " cannot be closed because its balance is "
                    + Money.format(balance) + ". The balance must be exactly $0.00.");
        }
        checkClosureRule();
        status = AccountStatus.CLOSED;
    }

    public final void freeze() {
        if (status == AccountStatus.CLOSED) {
            throw new BankException("Account " + accountNumber + " is closed and cannot be frozen.");
        }
        if (status == AccountStatus.FROZEN) {
            throw new BankException("Account " + accountNumber + " is already frozen.");
        }
        status = AccountStatus.FROZEN;
    }

    public final void unfreeze() {
        if (status != AccountStatus.FROZEN) {
            throw new BankException("Account " + accountNumber + " is not frozen.");
        }
        status = AccountStatus.ACTIVE;
    }

    public void onMonthEnd() {
    }

    protected abstract void checkWithdrawalRule(double amount);

    protected void checkClosureRule() {
    }

    protected void afterWithdrawal() {
    }

    protected void afterWithdrawalReversed() {
    }

    public abstract AccountType getType();

    public abstract String[] describeSpecifics();

    private void requireTransactionsAllowed() {
        if (status == AccountStatus.FROZEN) {
            throw new BankException("Account " + accountNumber + " is frozen. Transactions are not allowed.");
        }
        if (status == AccountStatus.CLOSED) {
            throw new BankException("Account " + accountNumber + " is closed. Transactions are not allowed.");
        }
    }

    private void requireValidAmount(double amount) {
        if (amount <= 0) {
            throw new BankException("The amount must be a positive number.");
        }
        if (amount < BankConfig.MIN_TRANSACTION_AMOUNT) {
            throw new BankException("The minimum transaction amount is "
                    + Money.format(BankConfig.MIN_TRANSACTION_AMOUNT) + ".");
        }
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public double getBalance() {
        return balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public int getTransactionCount() {
        return transactionCount;
    }
}