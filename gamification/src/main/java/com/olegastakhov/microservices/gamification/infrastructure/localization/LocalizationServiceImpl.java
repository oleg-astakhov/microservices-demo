package com.olegastakhov.microservices.gamification.infrastructure.localization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.Optional;

@Component
public class LocalizationServiceImpl {
    @Autowired
    MessageSource messageSource;

    public String getLocalizedMessage(final String i18nCode,
                                      final String... args) {
        final Locale locale = getCurrentUserLocale();
        return getLocalizedMessage(i18nCode, locale, args);
    }

    public String getLocalizedMessage(final String i18nCode,
                                      final Locale locale,
                                      final String... args) {
        final Optional<String> message = findLocalizedMessage(i18nCode, locale, Optional.empty(), args);
        if (message.isEmpty()) {
            throw new NoSuchMessageException(i18nCode, locale);
        }
        return message.get();
    }

    public Optional<String> findLocalizedMessage(final String i18nCode,
                                                 final Locale locale,
                                                 final Optional<String> defaultMessage,
                                                 final String... args) {
        Assert.hasLength(i18nCode, "i18nCode is blank when not expected");
        return Optional.ofNullable(messageSource.getMessage(i18nCode, args, defaultMessage.orElse(null), locale));
    }

    public Locale getCurrentUserLocale() {
        return LocaleContextHolder.getLocale();
    }
}
