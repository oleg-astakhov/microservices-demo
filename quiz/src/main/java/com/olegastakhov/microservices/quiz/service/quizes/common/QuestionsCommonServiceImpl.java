package com.olegastakhov.microservices.quiz.service.quizes.common;

import com.olegastakhov.microservices.quiz.service.quizes.common.api.GetOptionsInputData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


@Component
public class QuestionsCommonServiceImpl {

    public <T> T getRandomItem(final Supplier<List<T>> questionItemsSupplier) {
        final List<T> list = questionItemsSupplier.get();
        final int size = list.size();
        final int randomCountryIdx = new Random().nextInt(0, size);
        return list.get(randomCountryIdx);
    }

    public <T> List<T> getOptions(final GetOptionsInputData<T> input) {
        if (input.getNumberOfOptions() < 2) {
            throw new IllegalArgumentException("Number of options is less than 2");
        }
        if (input.getNumberOfOptions() > 26) {
            throw new IllegalArgumentException("Number of options is more than 26");
        }
        Assert.notNull(input.getRandomQuestion(), "randomQuizData is null when not expected");
        Assert.notNull(input.getQuestionItemsSupplier(), "questionItemsSupplier is null when not expected");
        Assert.notNull(input.getAnswerRetriever(), "answerRetriever is null when not expected");
        return generateOptions(input);
    }

    public <T> List<String> map(final List<T> options,
                                final Function<T, String> answerRetriever) {
        return options.stream()
                .map(answerRetriever)
                .toList();
    }

    protected  <T> List<T> generateOptions(final GetOptionsInputData<T> input) {
        final T correctAnswerData = input.getRandomQuestion();
        final Function<T, String> answerRetriever = input.getAnswerRetriever();

        final List<T> result = new ArrayList<>();
        final Set<String> excludeAnswer = new HashSet<>();

        result.add(correctAnswerData);

        excludeAnswer.add(answerRetriever.apply(correctAnswerData));

        do {
            final T randomQuestionData = getRandomNonRepeatingAnswer(excludeAnswer,
                    input.getQuestionItemsSupplier().get(),
                    answerRetriever);
            result.add(randomQuestionData);
            excludeAnswer.add(answerRetriever.apply(randomQuestionData));
        } while (result.size() < input.getNumberOfOptions()
                && excludeAnswer.size() < input.getQuestionItemsSupplier().get().size());

        Collections.shuffle(result);
        return result;
    }

    protected <T> T getRandomNonRepeatingAnswer(final Set<String> excludeAnswer,
                                                final List<T> questionItems,
                                                final Function<T, String> answerRetriever) {
        final Random random = new Random();

        final List<T> questionsMinusExcluded = new ArrayList<>(questionItems);

        questionsMinusExcluded.removeIf(t -> excludeAnswer.contains(answerRetriever.apply(t)));

        if (questionsMinusExcluded.isEmpty()) {
            throw new IllegalStateException("Ran out of options");
        }

        final int size = questionsMinusExcluded.size();

        T questionItemData;
        int idx;
        do {
            idx = random.nextInt(0, size);
            questionItemData = questionsMinusExcluded.get(idx);
        } while (excludeAnswer.contains(answerRetriever.apply(questionItemData)));


        return questionItemData;
    }
}
