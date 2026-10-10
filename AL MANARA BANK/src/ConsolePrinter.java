import java.util.Locale;

public class ConsolePrinter {
    private static final String TABLE_FORMAT = "%-10s %-20s %-14s %18s %-8s %6s%n";
    private final Bank bank;

    public ConsolePrinter(Bank bank) {
        this.bank = bank;
    }

    public void printMenu() {
        System.out.println("========== AL MANARA BANK ==========");
        System.out.println(" 1  Register New Customer");
        System.out.println(" 2  Open New Account");
        System.out.println(" 3  Deposit Money");
        System.out.println(" 4  Withdraw Money");
        System.out.println(" 5  Transfer Between Accounts");
        System.out.println(" 6  Display Customer Accounts");
        System.out.println(" 7  Display All Branch Accounts");
        System.out.println(" 8  Search Account by Number");
        System.out.println(" 9  Search Accounts by Type");
        System.out.println("10  Close an Account");
        System.out.println("11  Freeze an Account");
        System.out.println("12  Unfreeze an Account");
        System.out.println("13  Simulate End of Month");
        System.out.println(" 0  Exit");
        System.out.println("====================================");
    }

    public void printTierOptions() {
        Tier[] tiers = Tier.values();
        for (int i = 0; i < tiers.length; i++) {
            System.out.println((i + 1) + ". " + tiers[i].describe());
        }
    }

    public void printTypeOptions() {
        AccountType[] types = AccountType.values();
        for (int i = 0; i < types.length; i++) {
            System.out.println((i + 1) + ". " + types[i].getLabel()
                    + " (minimum opening balance " + Money.format(types[i].getMinimumOpeningBalance()) + ")");
        }
    }

    public void printCustomer(Customer customer) {
        System.out.println("Customer ID      : " + customer.getCustomerId());
        System.out.println("Full name        : " + customer.getFullName());
        System.out.println("National ID      : " + customer.getNationalId());
        System.out.println("Phone            : " + customer.getPhoneDisplay());
        System.out.println("Tier             : " + customer.getTier().describe());
        System.out.println("Accounts opened  : " + customer.getTotalAccounts());
        System.out.println("Open accounts    : " + customer.getOpenAccounts());
    }

    public void printAccountTable(Account[] accounts) {
        System.out.printf(Locale.US, TABLE_FORMAT, "Account No", "Owner", "Type", "Balance", "Status", "Txns");
        System.out.println(line(81));
        for (Account account : accounts) {
            Customer owner = bank.findCustomer(account.getOwnerId());
            String ownerName = owner == null ? "Unknown" : owner.getFullName();
            System.out.printf(Locale.US, TABLE_FORMAT,
                    account.getAccountNumber(),
                    shorten(ownerName, 20),
                    account.getType().getLabel(),
                    Money.format(account.getBalance()),
                    account.getStatus(),
                    account.getTransactionCount());
        }
        System.out.println(line(81));
    }

    public void printAccountDetails(Account account) {
        Customer owner = bank.findCustomer(account.getOwnerId());
        String ownerName = owner == null ? "Unknown" : owner.getFullName();
        System.out.println("Account number       : " + account.getAccountNumber());
        System.out.println("Account type         : " + account.getType().getLabel());
        System.out.println("Owner                : " + ownerName + " (ID " + account.getOwnerId() + ")");
        System.out.println("Balance              : " + Money.format(account.getBalance()));
        System.out.println("Status               : " + account.getStatus());
        System.out.println("Successful txns      : " + account.getTransactionCount());
        for (String detail : account.describeSpecifics()) {
            System.out.println(detail);
        }
    }

    private static String shorten(String text, int max) {
        if (text.length() <= max) {
            return text;
        }
        return text.substring(0, max - 3) + "...";
    }

    private static String line(int length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append('-');
        }
        return builder.toString();
    }
}