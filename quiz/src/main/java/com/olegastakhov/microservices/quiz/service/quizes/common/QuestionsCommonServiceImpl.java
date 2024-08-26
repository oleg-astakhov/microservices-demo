package com.olegastakhov.microservices.quiz.service.quizes.common;

import com.olegastakhov.microservices.quiz.service.quizes.common.api.GetOptionsInputData;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.RandomQuizData;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


@Component
public class QuestionsCommonServiceImpl {

    public <T> RandomQuizData<T> getRandomItem(final Supplier<List<T>> questionItemsSupplier) {
        final List<T> list = questionItemsSupplier.get();
        final int size = list.size();
        final int randomCountryIdx = new Random().nextInt(0, size);
        final T quizItemData = list.get(randomCountryIdx);
        return new RandomQuizData<>(quizItemData, randomCountryIdx);
    }

    public <T> List<T> getOptions(final GetOptionsInputData<T> input) {
        if (input.getNumberOfOptions() < 2) {
            throw new IllegalArgumentException("Number of options is less than 2");
        }
        if (input.getNumberOfOptions() > 26) {
            throw new IllegalArgumentException("Number of options is more than 26");
        }
        Assert.notNull(input.getRandomQuizData(), "randomQuizData is null when not expected");
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

    private <T> List<T> generateOptions(final GetOptionsInputData<T> input) {
        final RandomQuizData<T> correctAnswerData = input.getRandomQuizData();
        final Function<T, String> answerRetriever = input.getAnswerRetriever();

        final List<T> result = new ArrayList<>();
        final Set<String> excludeAnswer = new HashSet<>();
        final Set<Integer> excludeIndexes = new HashSet<>();

        result.add(correctAnswerData.questionData());

        excludeAnswer.add(answerRetriever.apply(correctAnswerData.questionData()));
        excludeIndexes.add(correctAnswerData.index());

        do {
            final RandomQuizData<T> randomQuizData = getRandomNonRepeatingAnswer(excludeAnswer,
                    excludeIndexes,
                    input.getQuestionItemsSupplier().get(),
                    answerRetriever);
            result.add(randomQuizData.questionData());
            excludeAnswer.add(answerRetriever.apply(randomQuizData.questionData()));
            excludeIndexes.add(randomQuizData.index());
        } while (result.size() < input.getNumberOfOptions()
                && excludeIndexes.size() < input.getQuestionItemsSupplier().get().size());

        Collections.shuffle(result);
        return result;
    }

    private <T> RandomQuizData<T> getRandomNonRepeatingAnswer(final Set<String> excludeAnswer,
                                                              final Set<Integer> excludeIndexes,
                                                              final List<T> questionItems,
                                                              final Function<T, String> answerRetriever) {
        final Random random = new Random();
        final int size = questionItems.size();
        T questionItemData;
        int idx;
        do {
            idx = random.nextInt(0, size);
            questionItemData = questionItems.get(idx);
        } while (excludeIndexes.contains(idx)
                || excludeAnswer.contains(answerRetriever.apply(questionItemData)));

        return new RandomQuizData<>(questionItemData, idx);
    }
}
