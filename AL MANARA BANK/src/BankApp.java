import java.util.NoSuchElementException;

public class BankApp {
    private final Bank bank = new Bank();
    private final InputReader input;
    private final ConsolePrinter printer;

    public BankApp(InputReader input) {
        this.input = input;
        this.printer = new ConsolePrinter(bank);
    }

    public void run() {
        boolean running = true;
        try {
            while (running) {
                printer.printMenu();
                int choice = input.readInt("Enter your choice: ");
                try {
                    running = execute(choice);
                } catch (BankException e) {
                    System.out.println("Operation rejected: " + e.getMessage());
                }
                System.out.println();
            }
        } catch (NoSuchElementException e) {
            System.out.println();
        }
        System.out.println("Thank you for using Al Manara Bank. Goodbye!");
    }

    private boolean execute(int choice) {
        switch (choice) {
            case 1:
                registerCustomer();
                break;
            case 2:
                openAccount();
                break;
            case 3:
                deposit();
                break;
            case 4:
                withdraw();
                break;
            case 5:
                transfer();
                break;
            case 6:
                displayCustomerAccounts();
                break;
            case 7:
                displayAllAccounts();
                break;
            case 8:
                searchByNumber();
                break;
            case 9:
                searchByType();
                break;
            case 10:
                closeAccount();
                break;
            case 11:
                freezeAccount();
                break;
            case 12:
                unfreezeAccount();
                break;
            case 13:
                bank.advanceOneMonth();
                System.out.println("One month has passed. Fixed deposits advanced and monthly counters were reset.");
                break;
            case 0:
                return false;
            default:
                System.out.println("Invalid menu option. Please choose a number from the menu.");
        }
        return true;
    }

    private void registerCustomer() {
        String name = input.readLine("Full name: ");
        String nationalId = input.readLine("National ID: ");
        String phone = input.readLine("Phone number (optional, press Enter to skip): ");
        Tier tier = chooseTier();
        Customer customer = bank.registerCustomer(name, nationalId, phone, tier);
        System.out.println("Customer registered successfully. Customer ID: " + customer.getCustomerId() + ".");
        printer.printCustomer(customer);
    }

    private void openAccount() {
        int customerId = input.readInt("Customer ID: ");
        Customer owner = bank.requireCustomer(customerId);
        System.out.println("Opening an account for " + owner.getFullName() + " (" + owner.getTier() + ").");
        AccountType type = chooseAccountType();
        double openingBalance = input.readAmount("Opening balance: ");
        int months = 0;
        if (type == AccountType.FIXED_DEPOSIT) {
            months = input.readInt("Duration in months (" + BankConfig.MIN_FIXED_DEPOSIT_MONTHS + "-"
                    + BankConfig.MAX_FIXED_DEPOSIT_MONTHS + "): ");
        }
        Account account = bank.openAccount(customerId, type, openingBalance, months);
        System.out.println("Account opened successfully. Account number: " + account.getAccountNumber() + ".");
        printer.printAccountDetails(account);
    }

    private void deposit() {
        String number = input.readLine("Account number: ");
        double amount = input.readAmount("Deposit amount: ");
        Account account = bank.deposit(number, amount);
        System.out.println("Deposit successful. New balance: " + Money.format(account.getBalance()) + ".");
    }

    private void withdraw() {
        String number = input.readLine("Account number: ");
        double amount = input.readAmount("Withdrawal amount: ");
        Account account = bank.withdraw(number, amount);
        System.out.println("Withdrawal successful. New balance: " + Money.format(account.getBalance()) + ".");
    }

    private void transfer() {
        String sourceNumber = input.readLine("Source account number: ");
        String destinationNumber = input.readLine("Destination account number: ");
        double amount = input.readAmount("Transfer amount: ");
        bank.transfer(sourceNumber, destinationNumber, amount);
        Account source = bank.requireAccount(sourceNumber);
        Account destination = bank.requireAccount(destinationNumber);
        System.out.println("Transfer successful.");
        System.out.println("Source " + source.getAccountNumber() + " balance: " + Money.format(source.getBalance()));
        System.out.println("Destination " + destination.getAccountNumber() + " balance: "
                + Money.format(destination.getBalance()));
    }

    private void displayCustomerAccounts() {
        int customerId = input.readInt("Customer ID: ");
        Customer customer = bank.requireCustomer(customerId);
        printer.printCustomer(customer);
        Account[] accounts = bank.getAccountsByCustomer(customerId);
        if (accounts.length == 0) {
            System.out.println("This customer does not have any accounts yet.");
            return;
        }
        printer.printAccountTable(accounts);
        System.out.println("Combined balance: " + Money.format(Bank.sumBalances(accounts)));
    }

    private void displayAllAccounts() {
        Account[] accounts = bank.getAllAccounts();
        if (accounts.length == 0) {
            System.out.println("There are no accounts in the branch yet.");
            return;
        }
        printer.printAccountTable(accounts);
        System.out.println("Total accounts: " + accounts.length);
    }

    private void searchByNumber() {
        String number = input.readLine("Account number: ");
        Account account = bank.findAccount(number);
        if (account == null) {
            System.out.println("No account found with number " + number + ".");
            return;
        }
        printer.printAccountDetails(account);
    }

    private void searchByType() {
        AccountType type = chooseAccountType();
        Account[] accounts = bank.getAccountsByType(type);
        if (accounts.length == 0) {
            System.out.println("There are no " + type.getLabel() + " accounts.");
            return;
        }
        printer.printAccountTable(accounts);
        System.out.println("Matching accounts: " + accounts.length);
        System.out.println("Combined balance : " + Money.format(Bank.sumBalances(accounts)));
    }

    private void closeAccount() {
        String number = input.readLine("Account number: ");
        bank.closeAccount(number);
        System.out.println("Account " + bank.requireAccount(number).getAccountNumber() + " has been closed.");
    }

    private void freezeAccount() {
        String number = input.readLine("Account number: ");
        bank.freezeAccount(number);
        System.out.println("Account " + bank.requireAccount(number).getAccountNumber() + " is now frozen.");
    }

    private void unfreezeAccount() {
        String number = input.readLine("Account number: ");
        bank.unfreezeAccount(number);
        System.out.println("Account " + bank.requireAccount(number).getAccountNumber() + " is active again.");
    }

    private Tier chooseTier() {
        System.out.println("Select customer tier:");
        printer.printTierOptions();
        Tier[] tiers = Tier.values();
        int choice = input.readChoice("Tier: ", 1, tiers.length);
        return tiers[choice - 1];
    }

    private AccountType chooseAccountType() {
        System.out.println("Select account type:");
        printer.printTypeOptions();
        AccountType[] types = AccountType.values();
        int choice = input.readChoice("Account type: ", 1, types.length);
        return types[choice - 1];
    }
}