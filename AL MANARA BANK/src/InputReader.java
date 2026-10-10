import java.util.Scanner;

public class InputReader {
    private static final String AMOUNT_PATTERN = "-?(\\d+(\\.\\d*)?|\\.\\d+)";
    private final Scanner scanner;

    public InputReader(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            String text = readLine(prompt);
            try {
                return Integer.parseInt(text);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }

    public int readChoice(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
        }
    }

    public double readAmount(String prompt) {
        while (true) {
            String text = readLine(prompt);
            if (text.matches(AMOUNT_PATTERN)) {
                try {
                    double value = Double.parseDouble(text);
                    if (Math.abs(value) <= BankConfig.MAX_AMOUNT) {
                        return Money.round(value);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a number such as 250.75.");
                    continue;
                }
                System.out.println("That amount is too large. The maximum is " + Money.format(BankConfig.MAX_AMOUNT) + ".");
            } else {
                System.out.println("Invalid input. Please enter a number such as 250.75.");
            }
        }
    }
}