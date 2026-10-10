import java.util.Locale;

public enum Tier {
    STANDARD(5.00, 0.000),
    SILVER(3.00, 0.005),
    GOLD(0.00, 0.010);

    private final double monthlyFee;
    private final double interestBonus;

    Tier(double monthlyFee, double interestBonus) {
        this.monthlyFee = monthlyFee;
        this.interestBonus = interestBonus;
    }

    public double getMonthlyFee() {
        return monthlyFee;
    }

    public double getInterestBonus() {
        return interestBonus;
    }

    public String describe() {
        return String.format(Locale.US, "%s (monthly fee %s, interest bonus +%.2f%%)",
                name(), Money.format(monthlyFee), interestBonus * 100);
    }
}