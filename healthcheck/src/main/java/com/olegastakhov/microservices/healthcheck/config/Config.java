package com.olegastakhov.microservices.healthcheck.config;

public class Config {
    private String healthCheckUrl;
    private String mode;
    private int statusCode;

    public String getHealthCheckUrl() {
        return healthCheckUrl;
    }

    public Config setHealthCheckUrl(String healthCheckUrl) {
        this.healthCheckUrl = healthCheckUrl;
        return this;
    }

    public String getMode() {
        return mode;
    }

    public Config setMode(String mode) {
        this.mode = mode;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Config setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
