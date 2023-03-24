package passwordValidator;

public class PasswordValidator {
    private static final int MAX_LENGTH = 19;

    public static boolean validate(String login, String password, String confirmPassword) {
        try {
            return validateLogin(login) && validatePassword(password) && validatePasswordConfirmation(password, confirmPassword);
        } catch (WrongLoginException | WrongPasswordException e) {
            System.out.println(e.getMessage());
            return false;
        } catch (NullPointerException e) {
            System.out.println("Значения логина, пароль и подтверждения пароля не должны быть null");
            return false;
        }
    }

    private static boolean validatePassword(String password) throws WrongPasswordException {
        if (!password.matches("\\w*")) {
            throw new WrongPasswordException("Пароль содержит недопустимые символы");
        }
        if (password.length() > MAX_LENGTH) {
            throw new WrongPasswordException("Пароль слишком длинный");
        }
        return password.matches("\\w{0," + MAX_LENGTH + "}");
    }

    private static boolean validateLogin(String login) throws WrongLoginException {
        if (!login.matches("\\w*")) {
            throw new WrongLoginException("Логин содержит недопустимые символы");
        }
        if (login.length() > MAX_LENGTH) {
            throw new WrongLoginException("Логин слишком длинный");
        }
        return login.matches("\\w{0," + MAX_LENGTH + "}");
    }

    private static boolean validatePasswordConfirmation(String password, String passwordConfirmation) throws WrongPasswordException {
        if (!passwordConfirmation.equals(password)) {
            throw new WrongPasswordException("Пароль и подтверждение не совпадают");
        }
        return true;
    }
}
