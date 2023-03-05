import java.util.Scanner;

public class Stars {
    public static void main(String[] args) throws Exception {
        // предполгаем что вводятся корректные данные: m, n - натуральные числа
        try (Scanner scanner = new Scanner(System.in)) {
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            String template = scanner.next();
            printFigure(n, m, template);
        }
    }

    public static void printFigure(int n, int m, String template) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                System.out.print(template + " ");
            }
            System.out.println();
        }
    }
}

