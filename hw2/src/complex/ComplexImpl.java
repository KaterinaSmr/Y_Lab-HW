package complex;

import java.util.Objects;

public final class ComplexImpl implements Complex {
    private final double real;
    private final double imaginary;

    public ComplexImpl(double real, double imaginary) {
        this.real = real;
        this.imaginary = imaginary;
    }

    public ComplexImpl(double real) {
        this(real, 0);
    }

    @Override
    public Complex add(Complex addition) {
        if (!(addition instanceof ComplexImpl)) {
            throw new IllegalArgumentException("Addition should be an instance of ComplexImpl class");
        }
        ComplexImpl c = (ComplexImpl) addition;
        return new ComplexImpl(this.real + c.real, this.imaginary + c.imaginary);
    }

    @Override
    public Complex substract(Complex subtrahend) {
        if (!(subtrahend instanceof ComplexImpl)) {
            throw new IllegalArgumentException("Subtrahend should be an instance of ComplexImpl class");
        }
        ComplexImpl c = (ComplexImpl) subtrahend;
        return new ComplexImpl(this.real - c.real, this.imaginary - c.imaginary);
    }

    @Override
    public Complex multiply(Complex factor) {
        if (!(factor instanceof ComplexImpl)) {
            throw new IllegalArgumentException("Factor should be an instance of ComplexImpl class");
        }
        ComplexImpl c = (ComplexImpl) factor;
        double resultReal = this.real * c.real - this.imaginary * c.imaginary;
        double resultImaginary = this.real * c.imaginary + this.imaginary * c.real;
        return new ComplexImpl(resultReal, resultImaginary);
    }

    @Override
    public double absolute() {
        return Math.hypot(this.real, this.imaginary);
    }

    //Отбрасывает мнимую или вещественную часть только когда соответствующая часть == 0, без учета погрешностей double
    //можно было бы ввести какую-то погрешность EPSILON и считать нулем все что меньше по модулю
    //    boolean purelyRealNumber = Math.abs(this.imaginary) <= EPSILON;
    //но тогда возникнет неконсистентность с equals - который при сравнении с 0 выдаст false
    //если переписывать equals - то возникнет неконсистентность с hash и некосистентность с equals каждого агрумента double
    @Override
    public String toString() {
        boolean purelyRealNumber = Math.abs(this.imaginary) == 0;
        boolean purelyImaginaryNumber = Math.abs(this.real) == 0;
        return purelyRealNumber ? String.valueOf(this.real) : purelyImaginaryNumber ? this.imaginary + "i" : this.real + " + " + this.imaginary + "i";
    }

    public double getReal() {
        return real;
    }

    public double getImaginary() {
        return imaginary;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComplexImpl complexImpl = (ComplexImpl) o;
        return Double.compare(complexImpl.real, real) == 0 && Double.compare(complexImpl.imaginary, imaginary) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imaginary);
    }
}
