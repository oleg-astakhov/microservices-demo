package com.olegastakhov.microservices.quiz.service.quizes.countries.dataprovider;

public class CountryData {
    private String id;
    private String continentName;
    private String countryCode;
    private String countryName;
    private String currencyCode;
    private String capitalCityName;

    public String getId() {
        return id;
    }

    public CountryData setId(String id) {
        this.id = id;
        return this;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public CountryData setCountryCode(String countryCode) {
        this.countryCode = countryCode;
        return this;
    }

    public String getCountryName() {
        return countryName;
    }

    public CountryData setCountryName(String countryName) {
        this.countryName = countryName;
        return this;
    }

    public String getContinentName() {
        return continentName;
    }

    public CountryData setContinentName(String continentName) {
        this.continentName = continentName;
        return this;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public CountryData setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

    public String getCapitalCityName() {
        return capitalCityName;
    }

    public CountryData setCapitalCityName(String capitalCityName) {
        this.capitalCityName = capitalCityName;
        return this;
    }

    @Override
    public String toString() {
        return String.format("%s, %s, %s, %s, %s", continentName, countryCode, countryName, capitalCityName, currencyCode);
    }
}
