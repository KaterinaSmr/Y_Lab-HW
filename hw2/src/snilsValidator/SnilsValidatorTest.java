package snilsValidator;

public class SnilsValidatorTest {

    public static void main(String[] args) {
        SnilsValidator snilsValidator = new SnilsValidatorImpl();
        System.out.println(snilsValidator.validate("11223344595"));
        System.out.println(snilsValidator.validate("90114404441"));
        System.out.println(snilsValidator.validate("00000000000"));

        System.out.println(snilsValidator.validate("01468870570"));
        //не только цифры
        System.out.println(snilsValidator.validate("1234567a967"));
        //короче
        System.out.println(snilsValidator.validate("1234567446"));
        //длиннее
        System.out.println(snilsValidator.validate("1234567446890"));

    }
}
