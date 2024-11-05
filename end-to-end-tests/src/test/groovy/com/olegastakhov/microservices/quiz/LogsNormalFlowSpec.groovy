package com.olegastakhov.microservices.quiz

import com.olegastakhov.microservices.AbstractSpecification

import java.util.regex.Pattern

class LogsNormalFlowSpec extends AbstractSpecification {
    def "get logs - ensure traceId and spanId are initialized"() {
        when: "call a special e2e endpoint to get a sample of logs from InMemory appender"
        Map logsData = QuizHelper.getLogs(params)

        then:
        null != logsData.logs
        logsData.size() > 0

        when: "find a log line printed for e2e testing purposes"
        List<String> reversedLogs = new ArrayList<>(logsData.logs)
        Collections.reverse(reversedLogs)
        String searchedSubstring = "e2e testing the presence of traceId and spanId"

        String matchedLogLine = reversedLogs.stream()
                .filter(it ->
                    it.contains(searchedSubstring)
                )
                .findAny()
                .orElseThrow(() -> new RuntimeException(String.format("No substring [%s] has been found in logs", searchedSubstring)))

        then: "find tracedId and spanId"
        // example log substring: [app=quiz,traceId=6728a61e73bd123794e6cb669b9ea76a,spanId=94e6cb669b9ea76a]
        Pattern.compile("traceId=[0-9a-z]+,spanId=[0-9a-z]+").matcher(matchedLogLine).find()
    }
}
