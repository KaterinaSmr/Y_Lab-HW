package snilsValidator;

public class SnilsValidatorImpl implements SnilsValidator {
    @Override
    public boolean validate(String snils) {
        if (!snils.matches("\\d{11}")) {
            return false;
        }


        char[] chars = snils.toCharArray();
        int sum = 0;
        for (int i = 0; i < 9; i++) {
            int x = Character.digit(chars[i], 10);
            sum += x * (9 - i);
        }
        //условие для вычисления контрольной суммы по сути сводится к вычислению остатка от деления на 101
        int remaining = sum % 101;
        int controlSum = remaining == 100 ? 0 : remaining;
        return controlSum == Integer.parseInt(snils.substring(snils.length() - 2));
    }
}
