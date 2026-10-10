import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BankApp app = new BankApp(new InputReader(new Scanner(System.in)));
        app.run();
    }
}