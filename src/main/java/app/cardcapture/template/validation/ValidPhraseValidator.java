package app.cardcapture.template.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;

public class ValidPhraseValidator implements ConstraintValidator<ValidPhrase, List<String>> {

    @Override
    public void initialize(ValidPhrase constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> value, ConstraintValidatorContext context) {
        if (isEmpty(value) || isBlank(value)) {
            return false;
        }
        return true;
    }

    private static boolean isBlank(List<String> value) {
        for (String phrase : value) {
            if (phrase != null && phrase.length() >= 1) {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(List<String> value) {
        if (value == null || value.isEmpty()) {
            return true;
        }
        return false;
    }
}