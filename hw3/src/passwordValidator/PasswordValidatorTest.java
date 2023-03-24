package passwordValidator;

public class PasswordValidatorTest {
    public static void main(String[] args) {
        //true
        System.out.println(PasswordValidator.validate("name", "pass", "pass"));
        System.out.println(PasswordValidator.validate("", "pass", "pass"));
        System.out.println(PasswordValidator.validate("name", "", ""));

        //некорректные символы в логине
        System.out.println(PasswordValidator.validate("n@me", "pass", "pass"));
        System.out.println(PasswordValidator.validate("name", "pass/", "pass/"));

        //пароль и подтверждение не совпадают
        System.out.println(PasswordValidator.validate("name", "pass", "abcd"));

        //слишком длинные строки
        System.out.println(PasswordValidator.validate("12345678901234567890", "pass", "pass"));
        System.out.println(PasswordValidator.validate("name", "12345678901234567890", "12345678901234567890"));

        //не очень поняла что должна возвращать валидация при получении null в качестве одного из параметров
        //по заданию сказано исключения обрабатывать, поэтому возвращаем false и выводим сообщение об ошибке
        System.out.println(PasswordValidator.validate(null, "pass", "pass"));
    }
}
