package com.epam.edp.demo.validators;

public class PasswordValidator {
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int MAX_PASSWORD_LENGTH = 20;

    public static boolean validatePassword(String password) {
        if (password == null) {
            return false;
        }

        return isLengthValid(password) &&
                containsUppercaseLetter(password) &&
                containsLowercaseLetter(password) &&
                containsDigit(password) &&
                containsSpecialCharacter(password);
    }

    private static boolean isLengthValid(String password) {
        return password.length() >= MIN_PASSWORD_LENGTH && password.length() <= MAX_PASSWORD_LENGTH;
    }

    private static boolean containsUppercaseLetter(String password) {
        return password.matches(".*[A-Z].*");
    }

    private static boolean containsLowercaseLetter(String password) {
        return password.matches(".*[a-z].*");
    }

    private static boolean containsDigit(String password) {
        return password.matches(".*\\d.*");
    }

    private static boolean containsSpecialCharacter(String password) {
        return password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
    }
}