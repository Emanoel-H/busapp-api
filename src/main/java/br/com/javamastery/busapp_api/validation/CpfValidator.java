package br.com.javamastery.busapp_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        String digits = cpf.replaceAll("\\D", "");

        if (digits.length() != 11)
            return false;

        if (digits.chars().distinct().count() == 1)
            return false;

        return validatesFirstDigit(digits) && validatesSecondDigit(digits);
    }

    private boolean validatesFirstDigit(String digits) {
        int sum = 0;
        for (int i = 0; i < 9; i++)
            sum += Character.getNumericValue(digits.charAt(i)) * (10 - i);

        int remainder = (sum * 10) % 11;
        int firstDigit = remainder == 10 ? 0 : remainder;

        return firstDigit == Character.getNumericValue(digits.charAt(9));
    }

    private boolean validatesSecondDigit(String digits) {
        int sum = 0;
        for (int i = 0; i < 10; i++)
            sum += Character.getNumericValue(digits.charAt(i)) * (11 - i);

        int remainder = (sum * 10) % 11;
        int secondDigit = remainder == 10 ? 0 : remainder;

        return secondDigit == Character.getNumericValue(digits.charAt(10));
    }
}
