import java.util.Arrays;
import java.util.Scanner;

public class Pell {
    public static void main(String[] args) throws Exception {
        //предполагаем что ввод корректный 0 < n < 30.
        final int MAX_N = 30;
        long[] pellSeq = new long[MAX_N + 1];
        Arrays.fill(pellSeq, -1);
        try (Scanner scanner = new Scanner(System.in)) {
            int n = scanner.nextInt();
            System.out.println(pellWithRecursion(n));
            System.out.println(pellWithCycle(n));
            System.out.println(pellWithMemo(n, pellSeq));
            System.out.println(pellWithDP(n));
        }
    }

    public static long pellWithCycle(int n) {
        if (n == 0 || n == 1) {
            return n;
        }
        long num1 = 0;
        long num2 = 1;
        long value = 0;

        for (int i = 2; i <= n; i++) {
            value = 2 * num2 + num1;
            num1 = num2;
            num2 = value;
        }
        return value;
    }

    public static long pellWithRecursion(int n) {
        if (n == 0 || n == 1) {
            return n;
        }
        return 2 * pellWithRecursion(n - 1) + pellWithRecursion(n - 2);
    }

    //улучшенная рекурсия по образцу преподавателя из чата
    public static long pellWithMemo(int n, long[] pellSeq) {
        if (pellSeq[n] != -1) {
            return pellSeq[n];
        }
        if (n == 0 || n == 1) {
            pellSeq[n] = n;
            return n;
        }
        long result = 2 * pellWithMemo(n - 1, pellSeq) + pellWithMemo(n - 2, pellSeq);
        pellSeq[n] = result;
        return result;
    }

    public static long pellWithDP(int n) {
        long[] pellSeq = new long[n + 1];
        pellSeq[0] = 0;
        pellSeq[1] = 1;
        for (int i = 2; i <= n; i++) {
            pellSeq[i] = 2 * pellSeq[i - 1] + pellSeq[i - 2];
        }
        return pellSeq[n];
    }
}

