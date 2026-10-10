import java.util.Locale;

public final class Money {
    private Money() {
    }

    public static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public static boolean isZero(double value) {
        return Math.abs(value) < 0.005;
    }

    public static String format(double value) {
        String digits = String.format(Locale.US, "%,.2f", Math.abs(value));
        return (value < -0.005 ? "-$" : "$") + digits;
    }
}