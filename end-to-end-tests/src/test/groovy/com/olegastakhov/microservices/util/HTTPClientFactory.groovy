package com.olegastakhov.microservices.util

import com.olegastakhov.microservices.Environment
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.LaxRedirectStrategy

class HTTPClientFactory {
    private static final int CONNECT_TIMEOUT = 10 * 1000;
    private static final int READ_TIMEOUT = 50 * 1000;
    private static final int CONNECTION_REQUEST_TIMEOUT = 10 * 1000;

    private static HTTPClientFactory INSTANCE

    static synchronized HTTPClientFactory getInstance() {
        if (null == INSTANCE) {
            println("Creating ${HTTPClientFactory.class.getSimpleName()} instance...")
            INSTANCE = new HTTPClientFactory()
        }
        return INSTANCE
    }

    private HTTPClientFactory() {
    }

    E2EHttpClient createPublicRestClient() {
        // not authenticated + follow redirects + enabled cookie management
        return createAjaxHttpClient()
    }

    E2EHttpClient createAjaxHttpClient(final Map<String, Object> params = new HashMap<>()) {
        E2EHttpClient httpClient = createFollowRedirectsWithCookieManagementHttpClient(params)
        initJsonHeaders(httpClient)
        params << [ns_ajaxJsonHttpClient_instance: httpClient]
        return httpClient
    }


    E2EHttpClient createFollowRedirectsWithCookieManagementHttpClient(final Map<String, Object> params = new HashMap<>()) {
        CloseableHttpClient closeableHttpClient = createFollowRedirectsWithCookieManagementCloseableHttpClient(params)
        return new E2EHttpClient(closeableHttpClient, Environment.DEFAULT_APP_URI)
    }

    private void initJsonHeaders(final E2EHttpClient httpClient) {
        httpClient.addHeader("X-Requested-With", "XMLHttpRequest")
        httpClient.setContentType(E2EContentType.JSON)
    }


    CloseableHttpClient createFollowRedirectsWithCookieManagementCloseableHttpClient(final Map<String, Object> params = new HashMap<>()) {
        createHttpClientWithParams(
                params << [
                        ns_httpClient_configuration_disableCookieManagement  : false,
                        ns_httpClient_configuration_enableLaxRedirectStrategy: true
                ]
        )
    }


    CloseableHttpClient createHttpClientWithParams(final Map<String, Object> params = new HashMap<>()) {
        return getApacheHttpClientBuilder(params).build()
    }

    private Boolean getBooleanValue(final Map<String, Object> params,
                                    final String key,
                                    final Boolean defaultValue) {
        return params.containsKey(key) ? params[key] as Boolean : defaultValue
    }

    private HttpClientBuilder customize(final HttpClientBuilder httpClientBuilder,
                                        final Map<String, Object> params = new HashMap<>()) {
        boolean disableCookieManagement = getBooleanValue(params, "ns_httpClient_configuration_disableCookieManagement", false)
        boolean enableLaxRedirectStrategy = getBooleanValue(params, "ns_httpClient_configuration_enableLaxRedirectStrategy", false)
        boolean disableRedirectHandling = getBooleanValue(params, "ns_httpClient_configuration_disableRedirectHandling", false)

        if (enableLaxRedirectStrategy && disableRedirectHandling) {
            throw new IllegalStateException("Mutually exclusive configuration parameters have been enabled: enableLaxRedirectStrategy and disableRedirectHandling")
        }

        httpClientBuilder.setDefaultRequestConfig(getApacheDefaultRequestConfigBuilder(params).build())
                .setRetryHandler(new DefaultHttpRequestRetryHandler(3, true))

        if (disableCookieManagement) {
            httpClientBuilder.disableCookieManagement()
        }

        if (enableLaxRedirectStrategy) {
            httpClientBuilder.setRedirectStrategy(new LaxRedirectStrategy())
            // LaxRedirectStrategy automatically follows redirects
        }

        if (disableRedirectHandling) {
            httpClientBuilder.disableRedirectHandling()
        }

        /**
         * Explicitly remove/consume this configuration, so that it was not picked up by
         * another piece of code which wants to create an HttpClient.
        */
        params.remove("ns_httpClient_configuration_disableCookieManagement")
        params.remove("ns_httpClient_configuration_enableLaxRedirectStrategy")
        params.remove("ns_httpClient_configuration_disableContentCompression")
        params.remove("ns_httpClient_configuration_disableRedirectHandling")

        return httpClientBuilder
    }

    private HttpClientBuilder getApacheHttpClientBuilder(final Map<String, Object> params = new HashMap<>()) {
        return customize(HttpClientBuilder.create(), params)
    }

    private RequestConfig.Builder getApacheDefaultRequestConfigBuilder(final Map<String, Object> params = new HashMap<>()) {
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom()
                .setConnectTimeout(CONNECT_TIMEOUT)
                .setConnectionRequestTimeout(CONNECTION_REQUEST_TIMEOUT)
                .setSocketTimeout(CONNECT_TIMEOUT)

        boolean disableCookieManagement = params.ns_httpClient_configuration_disableCookieManagement ?: false
        boolean disableContentCompression = params.ns_httpClient_configuration_disableContentCompression ?: false


        if (disableContentCompression) {
            requestConfigBuilder.setContentCompressionEnabled(false)
        }

        if (disableCookieManagement) {
            requestConfigBuilder.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
        } else {
            /**
             * Without this you'll get:
             * `MalformedCookieException` with a message:
             * `Invalid 'expires' attribute: Sat, 24 Feb 2024 17:34:28 GMT`
             *
             * This is because the format of the `Expires` property which
             * fails silently during parsing, and cookieStore is not populated.
             */
            requestConfigBuilder.setCookieSpec(CookieSpecs.STANDARD)
        }
        return requestConfigBuilder
    }
}
