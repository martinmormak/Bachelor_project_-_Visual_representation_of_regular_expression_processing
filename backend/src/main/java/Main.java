import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        ConsoleUI consoleUI = new ConsoleUI();
        consoleUI.show(input);
    }
}