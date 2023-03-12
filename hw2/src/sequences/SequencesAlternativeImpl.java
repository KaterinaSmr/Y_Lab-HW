package sequences;

import java.util.function.LongSupplier;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

public class SequencesAlternativeImpl implements Sequences {
    @Override
    public void a(int n) {
        System.out.print("\nA. ");
        IntStream.iterate(2, i -> i + 2)
                .limit(n)
                .forEach(this::myPrint);
    }

    @Override
    public void b(int n) {
        System.out.print("\nB. ");
        IntStream.iterate(1, i -> i + 2)
                .limit(n)
                .forEach(this::myPrint);
    }

    @Override
    public void c(int n) {
        System.out.print("\nC. ");
        IntStream.rangeClosed(1, n)
                .map(i -> i * i)
                .forEach(this::myPrint);
    }

    @Override
    public void d(int n) {
        System.out.print("\nD. ");
        IntStream.rangeClosed(1, n)
                .map(i -> i * i * i)
                .forEach(this::myPrint);
    }

    @Override
    public void e(int n) {
        System.out.print("\nE. ");
        IntStream.iterate(1, i -> i * (-1))
                .limit(n)
                .forEach(this::myPrint);
    }

    @Override
    public void f(int n) {
        System.out.print("\nF. ");
        IntStream.iterate(1, i -> -(Math.abs(i) + 1) * i / Math.abs(i))
                .limit(n)
                .forEach(this::myPrint);
    }

    @Override
    public void g(int n) {
        System.out.print("\nG. ");
        IntStream.rangeClosed(1, n)
                .map(i -> i * i * (int) Math.pow(-1, i % 2 + 1))
                .forEach(this::myPrint);
    }

    @Override
    public void h(int n) {
        System.out.print("\nH. ");
        IntStream.rangeClosed(1, n)
                .map(i -> (i + 1) / 2 * (i % 2))
                .forEach(this::myPrint);

    }

    @Override
    public void i(int n) {
        System.out.print("\nI. ");
        LongSupplier longSupplier = new LongSupplier() {
            long i = 0L;
            long value = 1L;

            @Override
            public long getAsLong() {
                return value *= ++i;
            }
        };
        LongStream.generate(longSupplier)
                .limit(n)
                .forEach(this::myPrint);
    }

    @Override
    public void j(int n) {
        System.out.print("\nJ. ");
        //каждый элемент стрима содержит предыдущее и текущее число Фибоначчи
        Stream.iterate(new int[]{0, 1}, e -> new int[]{e[1], e[0] + e[1]})
                .limit(n)
                .map(e -> e[1])
                .forEach(this::myPrint);
    }
}
