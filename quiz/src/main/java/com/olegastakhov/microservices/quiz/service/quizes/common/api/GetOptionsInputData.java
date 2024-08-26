package com.olegastakhov.microservices.quiz.service.quizes.common.api;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class GetOptionsInputData<T> {
    private int numberOfOptions;
    private  RandomQuizData<T> randomQuizData;
    private  Supplier<List<T>> questionItemsSupplier;
    private  Function<T, String> answerRetriever;

    public int getNumberOfOptions() {
        return numberOfOptions;
    }

    public GetOptionsInputData<T> setNumberOfOptions(int numberOfOptions) {
        this.numberOfOptions = numberOfOptions;
        return this;
    }

    public RandomQuizData<T> getRandomQuizData() {
        return randomQuizData;
    }

    public GetOptionsInputData<T> setRandomQuizData(RandomQuizData<T> randomQuizData) {
        this.randomQuizData = randomQuizData;
        return this;
    }

    public Supplier<List<T>> getQuestionItemsSupplier() {
        return questionItemsSupplier;
    }

    public GetOptionsInputData<T> setQuestionItemsSupplier(Supplier<List<T>> questionItemsSupplier) {
        this.questionItemsSupplier = questionItemsSupplier;
        return this;
    }

    public Function<T, String> getAnswerRetriever() {
        return answerRetriever;
    }

    public GetOptionsInputData<T> setAnswerRetriever(Function<T, String> answerRetriever) {
        this.answerRetriever = answerRetriever;
        return this;
    }
}