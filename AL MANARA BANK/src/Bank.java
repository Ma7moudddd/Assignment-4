public class Bank {
    private final Customer[] customers = new Customer[BankConfig.MAX_CUSTOMERS];
    private final Account[] accounts = new Account[BankConfig.MAX_ACCOUNTS];
    private int customerCount;
    private int accountCount;
    private int nextCustomerId = BankConfig.FIRST_CUSTOMER_ID;
    private int nextAccountSequence = BankConfig.FIRST_ACCOUNT_SEQUENCE;

    public Customer registerCustomer(String fullName, String nationalId, String phone, Tier tier) {
        if (customerCount >= customers.length) {
            throw new BankException("Customer storage is full. No more customers can be registered.");
        }
        String name = clean(fullName);
        if (name.isEmpty()) {
            throw new BankException("The customer name cannot be empty.");
        }
        String national = clean(nationalId);
        if (national.isEmpty()) {
            throw new BankException("The national ID cannot be empty.");
        }
        if (findCustomerByNationalId(national) != null) {
            throw new BankException("This national ID already belongs to another customer.");
        }
        String cleanPhone = clean(phone);
        if (!cleanPhone.isEmpty()) {
            validatePhone(cleanPhone);
        }
        Customer customer = new Customer(nextCustomerId, name, national,
                cleanPhone.isEmpty() ? null : cleanPhone, tier);
        nextCustomerId++;
        customers[customerCount++] = customer;
        return customer;
    }

    public Account openAccount(int customerId, AccountType type, double openingBalance, int durationMonths) {
        Customer owner = requireCustomer(customerId);
        if (accountCount >= accounts.length) {
            throw new BankException("Account storage is full. No more accounts can be opened.");
        }
        if (openingBalance < type.getMinimumOpeningBalance()) {
            throw new BankException("The opening balance for a " + type.getLabel() + " account must be at least "
                    + Money.format(type.getMinimumOpeningBalance()) + ".");
        }
        String accountNumber = String.format("%s-%04d", type.getPrefix(), nextAccountSequence);
        Account account = createAccount(type, accountNumber, owner, openingBalance, durationMonths);
        nextAccountSequence++;
        accounts[accountCount++] = account;
        owner.registerAccountOpened();
        return account;
    }

    public Account deposit(String accountNumber, double amount) {
        Account account = requireAccount(accountNumber);
        account.deposit(amount);
        return account;
    }

    public Account withdraw(String accountNumber, double amount) {
        Account account = requireAccount(accountNumber);
        account.withdraw(amount);
        return account;
    }

    public void transfer(String sourceNumber, String destinationNumber, double amount) {
        Account source = requireAccount(sourceNumber);
        Account destination = requireAccount(destinationNumber);
        if (source == destination) {
            throw new BankException("The source and destination accounts must be different.");
        }
        destination.requireCanReceive(amount);
        source.withdraw(amount);
        try {
            destination.deposit(amount);
        } catch (BankException e) {
            source.reverseWithdrawal(amount);
            throw new BankException("Transfer failed and the money was returned to the source account. "
                    + e.getMessage());
        }
    }

    public void closeAccount(String accountNumber) {
        Account account = requireAccount(accountNumber);
        account.close();
        Customer owner = findCustomer(account.getOwnerId());
        if (owner != null) {
            owner.registerAccountClosed();
        }
    }

    public void freezeAccount(String accountNumber) {
        requireAccount(accountNumber).freeze();
    }

    public void unfreezeAccount(String accountNumber) {
        requireAccount(accountNumber).unfreeze();
    }

    public void advanceOneMonth() {
        for (int i = 0; i < accountCount; i++) {
            accounts[i].onMonthEnd();
        }
    }

    public Customer findCustomer(int customerId) {
        for (int i = 0; i < customerCount; i++) {
            if (customers[i].getCustomerId() == customerId) {
                return customers[i];
            }
        }
        return null;
    }

    public Customer requireCustomer(int customerId) {
        Customer customer = findCustomer(customerId);
        if (customer == null) {
            throw new BankException("No customer found with ID " + customerId + ".");
        }
        return customer;
    }

    public Account findAccount(String accountNumber) {
        String wanted = clean(accountNumber);
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getAccountNumber().equalsIgnoreCase(wanted)) {
                return accounts[i];
            }
        }
        return null;
    }

    public Account requireAccount(String accountNumber) {
        Account account = findAccount(accountNumber);
        if (account == null) {
            throw new BankException("No account found with number " + clean(accountNumber) + ".");
        }
        return account;
    }

    public Account[] getAllAccounts() {
        Account[] result = new Account[accountCount];
        for (int i = 0; i < accountCount; i++) {
            result[i] = accounts[i];
        }
        return result;
    }

    public Account[] getAccountsByCustomer(int customerId) {
        int matches = 0;
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getOwnerId() == customerId) {
                matches++;
            }
        }
        Account[] result = new Account[matches];
        int index = 0;
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getOwnerId() == customerId) {
                result[index++] = accounts[i];
            }
        }
        return result;
    }

    public Account[] getAccountsByType(AccountType type) {
        int matches = 0;
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getType() == type) {
                matches++;
            }
        }
        Account[] result = new Account[matches];
        int index = 0;
        for (int i = 0; i < accountCount; i++) {
            if (accounts[i].getType() == type) {
                result[index++] = accounts[i];
            }
        }
        return result;
    }

    public static double sumBalances(Account[] list) {
        double total = 0;
        for (Account account : list) {
            total += account.getBalance();
        }
        return Money.round(total);
    }

    private Account createAccount(AccountType type, String accountNumber, Customer owner,
                                  double openingBalance, int durationMonths) {
        double bonus = owner.getTier().getInterestBonus();
        switch (type) {
            case SAVINGS:
                return new SavingsAccount(accountNumber, owner.getCustomerId(), openingBalance,
                        BankConfig.SAVINGS_BASE_RATE + bonus);
            case CURRENT:
                return new CurrentAccount(accountNumber, owner.getCustomerId(), openingBalance,
                        BankConfig.CURRENT_OVERDRAFT_LIMIT);
            case FIXED_DEPOSIT:
                if (durationMonths < BankConfig.MIN_FIXED_DEPOSIT_MONTHS
                        || durationMonths > BankConfig.MAX_FIXED_DEPOSIT_MONTHS) {
                    throw new BankException("The duration must be between " + BankConfig.MIN_FIXED_DEPOSIT_MONTHS
                            + " and " + BankConfig.MAX_FIXED_DEPOSIT_MONTHS + " months.");
                }
                return new FixedDepositAccount(accountNumber, owner.getCustomerId(), openingBalance,
                        BankConfig.FIXED_DEPOSIT_BASE_RATE + bonus, durationMonths);
            default:
                throw new BankException("Unsupported account type.");
        }
    }

    private Customer findCustomerByNationalId(String nationalId) {
        for (int i = 0; i < customerCount; i++) {
            if (customers[i].getNationalId().equalsIgnoreCase(nationalId)) {
                return customers[i];
            }
        }
        return null;
    }

    private void validatePhone(String phone) {
        for (int i = 0; i < phone.length(); i++) {
            char c = phone.charAt(i);
            if (c < '0' || c > '9') {
                throw new BankException("The phone number must contain digits only.");
            }
        }
        if (phone.length() < BankConfig.MIN_PHONE_DIGITS || phone.length() > BankConfig.MAX_PHONE_DIGITS) {
            throw new BankException("The phone number must be between " + BankConfig.MIN_PHONE_DIGITS
                    + " and " + BankConfig.MAX_PHONE_DIGITS + " digits.");
        }
    }

    private static String clean(String text) {
        return text == null ? "" : text.trim();
    }
}