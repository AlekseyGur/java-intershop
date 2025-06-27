package ru.alexgur.intershop.system.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.UUID;

public class ValidUUIDValidator implements ConstraintValidator<ValidUUID, Object> {

    @Override
    public void initialize(ValidUUID constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof UUID || tryParseUUID(value.toString())) {
            return true;
        }
        return false;
    }

    private static boolean tryParseUUID(String uuidStr) {
        try {
            UUID.fromString(uuidStr);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}