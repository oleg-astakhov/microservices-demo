package com.olegastakhov.microservices.quiz.command.e2egetlogline;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is only available during end-to-ends tests.
 * It lets us test the presence of traceId and spanId
 * in the logs.
 *
 * See "LogsNormalFlowSpec" in end-to-end tests.
 */

public class E2EInMemoryAppender extends AbstractAppender {
    private static final int MAX_LOG_SIZE = 250;
    private final List<String> logEntries = new ArrayList<>();

    public E2EInMemoryAppender(Layout<? extends Serializable> layout) {
        super("InMemoryAppender", null, layout, true);

        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        Configuration config = context.getConfiguration();

        this.start();
        config.getRootLogger().addAppender(this, null, null);
        context.updateLoggers();
    }

    @Override
    public void append(LogEvent event) {
        if (null != event.getThrown()) {
            return; // skip exceptions
        }
        String formattedMessage = getLayout().toSerializable(event).toString();
        synchronized (logEntries) {
            logEntries.add(formattedMessage);

            // Limit log entries size to prevent OutOfMemory
            if (logEntries.size() > MAX_LOG_SIZE) {
                logEntries.clear();
            }
        }
    }


    public List<String> getLogEntries() {
        synchronized (logEntries) {
            return new ArrayList<>(logEntries);
        }
    }
}