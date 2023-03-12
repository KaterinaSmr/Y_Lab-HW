package sequences;

public class SequencesImpl implements Sequences {
    @Override
    public void a(int n) {
        System.out.print("\nA. ");
        for (int i = 1; i <= n; i++) {
            System.out.print(i * 2 + " ");
        }
    }

    @Override
    public void b(int n) {
        System.out.print("\nB. ");
        for (int i = 0; i < n; i++) {
            System.out.print((i * 2 + 1) + " ");
        }
    }

    @Override
    public void c(int n) {
        System.out.print("\nC. ");
        for (int i = 1; i <= n; i++) {
            System.out.print(i * i + " ");
        }
    }

    @Override
    public void d(int n) {
        System.out.print("\nD. ");
        for (int i = 1; i <= n; i++) {
            System.out.print(i * i * i + " ");
        }
    }

    @Override
    public void e(int n) {
        System.out.print("\nE. ");
        for (int i = 1, j = 1; i <= n; i++, j *= -1) {
            System.out.print(j + " ");
//            если по какой то причине нельзя использовать вторую переменную j, есть еще такие варианты:
//            System.out.print((i % 2) * 2 - 1 + " ");
//            System.out.print((int) Math.pow(-1, i % 2 + 1) + " ");
        }
    }

    @Override
    public void f(int n) {
        System.out.print("\nF. ");
        for (int i = 1, j = 1; i <= n; i++, j *= -1) {
            System.out.print(i * j + " ");
        }
    }

    @Override
    public void g(int n) {
        System.out.print("\nG. ");
        for (int i = 1, j = 1; i <= n; i++, j *= -1) {
            System.out.print(i * i * j + " ");
        }
    }

    @Override
    public void h(int n) {
        System.out.print("\nH. ");
        for (int i = 1; i <= n; i++) {
            System.out.print((i + 1) / 2 * (i % 2) + " ");
        }
    }

    @Override
    public void i(int n) {
        System.out.print("\nI. ");
        long next = 1;
        for (int i = 1; i <= n; i++) {
            next *= i;
            System.out.print(next + " ");
        }
    }

    @Override
    public void j(int n) {
        System.out.print("\nJ. ");
        int num1 = 0;
        int num2 = 1;
        int val = 1;
        System.out.print(val + " ");
        for (int i = 1; i < n; i++) {
            val = num1 + num2;
            System.out.print(val + " ");
            num1 = num2;
            num2 = val;
        }
    }
}
