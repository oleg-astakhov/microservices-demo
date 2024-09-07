package com.olegastakhov.microservices.quiz.service.quizes.countries.dataprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

/**
 * Hypothetical data provider
 */

@Component
public class CountriesServiceImpl {
    private static final Logger log = LoggerFactory.getLogger(CountriesServiceImpl.class);

    private final List<CountryData> countryData;
    private final Map<String, CountryData> idToCountry = new HashMap<>();

    public CountriesServiceImpl() {
        this.countryData = init();
    }

    public List<CountryData> list() {
        return countryData;
    }

    private List<CountryData> init() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("countries.csv")
                        .getInputStream()))) {
            return parseCountryData(reader.lines());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CountryData get(final String questionItemId) {
        final CountryData countryData = idToCountry.get(questionItemId);
        if (null == countryData) {
            throw new IllegalArgumentException(String.format("No country data is found for item id [%s]", questionItemId));
        }
        return countryData;
    }

    private List<CountryData> parseCountryData(final Stream<String> lines) {
        final Set<String> countryCodes = new HashSet<>();
        return lines.map(it -> it.split(","))
                .filter(it -> {
                    final int expectedTokens = 6;
                    if (it.length != expectedTokens) {
                        log.warn("Wrong number of tokens. Expecting: {}. Got: {}. This item will be skipped. Source: {}", expectedTokens, it.length, Arrays.asList(it));
                        return false;
                    }
                    return true;
                })
                .map(parts -> {
                    final CountryData countryData = map(parts);
                    if (countryCodes.contains(countryData.getCountryCode())) {
                        throw new IllegalArgumentException(String.format("Country code %s already added", countryData.getCountryCode()));
                    }
                    idToCountry.put(countryData.getId(), countryData);
                    countryCodes.add(countryData.getCountryCode());
                    return countryData;
                })
                .toList();
    }

    private CountryData map(String[] parts) {
        final String id = parts[0].trim().toUpperCase();
        final String continent = parts[1].trim();
        final String countryCode = parts[2].trim().toUpperCase();
        final String countryName = parts[3].trim();
        final String capitalCity = parts[4].trim();
        final String currencyCode = parts[5].trim().toUpperCase();
        return new CountryData()
                .setId(id)
                .setContinentName(continent)
                .setCountryCode(countryCode)
                .setCountryName(countryName)
                .setCapitalCityName(capitalCity)
                .setCurrencyCode(currencyCode);
    }
}
