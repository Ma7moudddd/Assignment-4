public class Customer {
    private final int customerId;
    private final String fullName;
    private final String nationalId;
    private final String phone;
    private final Tier tier;
    private int totalAccounts;
    private int openAccounts;

    public Customer(int customerId, String fullName, String nationalId, String phone, Tier tier) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.nationalId = nationalId;
        this.phone = phone;
        this.tier = tier;
    }

    public void registerAccountOpened() {
        totalAccounts++;
        openAccounts++;
    }

    public void registerAccountClosed() {
        if (openAccounts > 0) {
            openAccounts--;
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNationalId() {
        return nationalId;
    }

    public String getPhoneDisplay() {
        return phone == null ? "Not provided" : phone;
    }

    public Tier getTier() {
        return tier;
    }

    public int getTotalAccounts() {
        return totalAccounts;
    }

    public int getOpenAccounts() {
        return openAccounts;
    }
}