package complex;

public class ComplexTest {
    public static void main(String[] args) {
        Complex complexImpl = new ComplexImpl(4, 1);
        Complex complexImpl1 = new ComplexImpl(-3, 4);
        Complex real = new ComplexImpl(1);
        Complex imaginary = new ComplexImpl(0, 1);
        Complex zero = new ComplexImpl(0, 0);

        System.out.println(complexImpl);
        System.out.println(complexImpl1);
        System.out.println(real);
        System.out.println(imaginary);
        System.out.println(zero);

        System.out.println("Absolute of " + complexImpl1 + " = " + complexImpl1.absolute());
        System.out.println(complexImpl + " add " + complexImpl1 + " = " + complexImpl.add(complexImpl1));
        System.out.println(complexImpl + " substract " + complexImpl1 + " = " + complexImpl.substract(complexImpl1));
        System.out.println(complexImpl + " multiply " + complexImpl1 + " = " + complexImpl.multiply(complexImpl1));
        System.out.println(complexImpl + " multiply " + zero + " = " + complexImpl.multiply(zero));
    }
}
