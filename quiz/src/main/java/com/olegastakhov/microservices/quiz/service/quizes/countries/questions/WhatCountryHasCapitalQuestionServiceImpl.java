package com.olegastakhov.microservices.quiz.service.quizes.countries.questions;

import com.olegastakhov.microservices.quiz.infrastructure.localization.LocalizationServiceImpl;
import com.olegastakhov.microservices.quiz.service.quizes.common.QuestionsCommonServiceImpl;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.GetOptionsInputData;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuestionGenerator;
import com.olegastakhov.microservices.quiz.service.quizes.common.api.QuizData;
import com.olegastakhov.microservices.quiz.service.quizes.countries.dataprovider.CountriesServiceImpl;
import com.olegastakhov.microservices.quiz.service.quizes.countries.dataprovider.CountryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component
public class WhatCountryHasCapitalQuestionServiceImpl implements QuestionGenerator<CountryData> {
    private final String id = "what-country-has-capital";

    @Autowired
    private LocalizationServiceImpl localization;
    @Autowired
    private QuestionsCommonServiceImpl common;
    @Autowired
    private CountriesServiceImpl countries;

    @Override
    public String getId() {
        return id;
    }

    private Function<CountryData, String> getAnswerRetriever() {
        return CountryData::getCountryName;
    }

    @Override
    public String getCorrectAnswer(String questionItemId) {
        final CountryData countryData = countries.get(questionItemId);
        return getAnswerRetriever().apply(countryData);
    }

    public QuizData generate() {
        final QuizData result = new QuizData();
        result.setQuestionId(id);
        final CountryData randomCountry = common.getRandomItem(countries::list);
        result.setQuestionItemId(randomCountry.getId());
        result.setQuestion(getLocalizedQuestion(randomCountry));
        result.setCorrectAnswer(getAnswerRetriever().apply(randomCountry));
        final List<CountryData> options = common.getOptions(new GetOptionsInputData<CountryData>()
                        .setNumberOfOptions(4)
                        .setRandomQuestion(randomCountry)
                        .setQuestionItemsSupplier(countries::list)
                        .setAnswerRetriever(getAnswerRetriever()));
        result.setOptions(common.map(options, getAnswerRetriever()));
        return result;
    }

    public String getQuestion(final String questionItemId) {
        final CountryData countryData = countries.get(questionItemId);
        return getLocalizedQuestion(countryData);
    }

    private String getLocalizedQuestion(CountryData countryData) {
        return localization.getLocalizedMessage("quiz.whatCountryHasCapital", countryData.getCapitalCityName());
    }
}
