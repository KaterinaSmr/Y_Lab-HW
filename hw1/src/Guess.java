import java.util.Random;
import java.util.Scanner;

public class Guess {
    public static void main(String[] args) throws Exception {
        int number = new Random().nextInt(100); // здесь загадывается число от 1 до 99
        int maxAttempts = 10; // здесь задается количество попыток
        System.out.println("Я загадал число. У тебя " + maxAttempts + " попыток угадать.");
        guess(number, maxAttempts);
    }

    public static void guess(int number, int maxAttempts){
        try (Scanner scanner = new Scanner(System.in)) {
            for (int i = maxAttempts-1; i >= 0 ; i--) {
                int n = scanner.nextInt();
                if (n == number){
                    System.out.println("Ты угадал с " + (maxAttempts - i) + " попытки!");
                    return;
                } else {
                    System.out.println("Мое число "
                            + (number < n ? "меньше" : "больше")
                            + "! Осталось " + i + " попыток!");
                }
            }
            System.out.println("Ты не угадал");
        }
    }
}

