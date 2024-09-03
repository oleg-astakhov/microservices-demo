package com.olegastakhov.microservices.quiz.service.quizes.common

import com.olegastakhov.microservices.quiz.service.quizes.common.api.GetOptionsInputData
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream
import com.olegastakhov.microservices.quiz.Helper

class QuestionsCommonServiceImplSpec extends Specification {

    def "generateOptions - request max 2"() {
        given:
        QuestionsCommonServiceImpl beanUnderTesting = Spy(QuestionsCommonServiceImpl)
        1 * beanUnderTesting.getRandomNonRepeatingAnswer(_, _, _) >> new TestQuestion("a2")


        when:
        GetOptionsInputData question = new GetOptionsInputData<TestQuestion>()
                .setNumberOfOptions(2)
                .setAnswerRetriever(getAnswerRetriever())
                .setQuestionItemsSupplier(() -> ques("a1", "a2", "a3"))
                .setRandomQuestion(new TestQuestion("a1"))

        List<TestQuestion> result = beanUnderTesting.generateOptions(question)

        then:
        result.size() == 2
        with(result.stream().map { it.getAnswer() }.collect(Collectors.toSet())) {
            it.contains("a1") // correct answer
            it.contains("a2")
        }
    }

    def "generateOptions - request max 3"() {
        given:
        QuestionsCommonServiceImpl beanUnderTesting = Spy(QuestionsCommonServiceImpl)
        1 * beanUnderTesting.getRandomNonRepeatingAnswer(_, _, _) >> new TestQuestion("a1")
        1 * beanUnderTesting.getRandomNonRepeatingAnswer(_, _, _) >> new TestQuestion("a5")


        when:
        GetOptionsInputData question = new GetOptionsInputData<TestQuestion>()
                .setNumberOfOptions(3)
                .setAnswerRetriever(getAnswerRetriever())
                .setQuestionItemsSupplier(() -> ques("a1", "a2", "a3", "a4", "a5"))
                .setRandomQuestion(new TestQuestion("a2"))

        List<TestQuestion> result = beanUnderTesting.generateOptions(question)

        then:
        result.size() == 3
        with(result.stream().map { it.getAnswer() }.collect(Collectors.toSet())) {
            it.contains("a2") // correct answer
            it.contains("a1")
            it.contains("a5")
        }
    }


    def "getRandomNonRepeatingAnswer"() {
        given:
        QuestionsCommonServiceImpl beanUnderTesting = new QuestionsCommonServiceImpl()

        when:
        TestQuestion result = beanUnderTesting.getRandomNonRepeatingAnswer(excludeAnswers, questions, getAnswerRetriever())

        then:
        expectedAnswer.contains(result.getAnswer())

        where:
        questions              || expectedAnswer           || excludeAnswers
        ques("a1", "a2", "a3") || Helper.toSet("a3")       || Helper.toSet("a1", "a2")
        ques("a1", "a2", "a3") || Helper.toSet("a2")       || Helper.toSet("a1", "a3")
        ques("a1", "a2", "a3") || Helper.toSet("a1", "a3") || Helper.toSet("a2")
    }

    def "getRandomNonRepeatingAnswer - ran out of options - sanity test case"() {
        given:
        QuestionsCommonServiceImpl beanUnderTesting = new QuestionsCommonServiceImpl()

        when:
        beanUnderTesting.getRandomNonRepeatingAnswer(
                Helper.toSet("a1", "a2", "a3"),
                ques("a1", "a2", "a3"),
                getAnswerRetriever())

        then:
        IllegalStateException ex = thrown(IllegalStateException)
        ex.getMessage() == "Ran out of options" // should not happen
    }

    private List<TestQuestion> ques(String... answers) {
        return Stream.of(answers).map { new TestQuestion(it) }.toList()
    }

    private Closure<String> getAnswerRetriever() {
        return { TestQuestion data -> data.getAnswer() }
    }

    private class TestQuestion {
        private String answer

        TestQuestion(String answer) {
            this.answer = answer
        }

        String getAnswer() {
            return answer
        }
    }
}
