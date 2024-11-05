package com.olegastakhov.microservices.quiz.command.e2egetlogline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.Serializable;

@Profile(value = "compose-e2e-test")
@Configuration
public class E2EInMemoryAppenderConfigurationServiceImpl {

    @Bean
    public E2EInMemoryAppender getInMemoryAppender() {
        return new E2EInMemoryAppender(getCustomPatternLayout());
    }

    private Layout<? extends Serializable> getCustomPatternLayout() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        org.apache.logging.log4j.core.config.Configuration config = context.getConfiguration();

        final Appender consoleAppender = config.getAppender("ConsoleAppender");
        if (null == consoleAppender) {
            return PatternLayout.createDefaultLayout();
        }

        final Layout<? extends Serializable> layout = consoleAppender.getLayout();
        if (null == layout) {
            return PatternLayout.createDefaultLayout();
        }
        return layout;
    }
}
