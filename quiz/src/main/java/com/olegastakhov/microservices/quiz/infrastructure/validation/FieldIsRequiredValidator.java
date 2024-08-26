package com.olegastakhov.microservices.quiz.infrastructure.validation;

import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

public class FieldIsRequiredValidator implements ConstraintValidator<FieldIsRequired, String> {
    private String i18nFieldKey;
    private String i18nMessageKey;
    private final LocalizationServiceImpl localizationService;

    public FieldIsRequiredValidator(LocalizationServiceImpl localizationService) {
        this.localizationService = localizationService;
    }

    @Override
    public void initialize(FieldIsRequired constraintAnnotation) {
        this.i18nFieldKey = constraintAnnotation.i18nFieldKey();
        this.i18nMessageKey = constraintAnnotation.i18nMessageKey();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = StringUtils.isNotBlank(value);
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            final String arg = localizationService.getLocalizedMessage(i18nFieldKey);
            final String messageText = localizationService.getLocalizedMessage(
                    i18nMessageKey, arg);

            context.buildConstraintViolationWithTemplate(messageText).addConstraintViolation();
        }
        return isValid;
    }
}