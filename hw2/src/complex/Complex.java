package complex;

public interface Complex {
    Complex add(Complex addition);

    Complex substract(Complex subtrahend);

    Complex multiply(Complex factor);

    double absolute();
}
