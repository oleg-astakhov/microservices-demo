package com.olegastakhov.microservices.util

import com.google.gson.Gson
import com.olegastakhov.microservices.util.httpresponse.E2EHttpResponse
import org.apache.commons.lang3.StringUtils
import org.apache.http.client.methods.*
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext

class E2EHttpClient {
    private final CloseableHttpClient httpClient
    private final String defaultUri
    private E2EContentType contentType
    private Map<String, String> headers = new HashMap<>()

    E2EHttpClient(CloseableHttpClient httpClient, String defaultUri) {
        assert StringUtils.isNotBlank(defaultUri)
        this.httpClient = httpClient
        this.defaultUri = defaultUri
    }

    private void initializeHeaders(HttpRequestBase requestBase,
                                   Map params) {
        /**
         * The order of setting headers is important.
         * We first set headers from this.headers.
         * It's like default headers, for all requests,
         * if they are not set explicitly.
         *
         * This "explicitly" means from the
         * "params.ns_httpClient_headers". So, if this property
         * contains the same header which is set via this.headers
         * then they must override the default headers.
         */
        setHeaders(requestBase, this.headers)

        if (null != contentType) {
            /**
             * When I was migrating from SpringBoot 2.x to SpringBoot 3.x and then
             * updating all other libs, at some point I was removing the dependency on
             * "groovyx.net.http.HTTPBuilder" because it was old and abandoned
             * and blocked bumping up other libraries (e.g. groovy).
             *
             * Through hours of debugging I've found that, for example,
             * SpringBoot will not redirect after POST to /logout if no Accept
             * header is specified. The consequence was that a new cookie was not
             * returned/assigned upon logout to the calling HTTP client.
             */
            requestBase.setHeader("Content-Type", contentType.toString())
            requestBase.setHeader("Accept", contentType.getAcceptHeader())
        }
        setHeaders(requestBase, params.ns_httpClient_headers)
    }

    private void setHeaders(HttpRequestBase requestBase,
                            Map headers) {
        if (null != headers && headers.size() > 0) {
            headers.entrySet().stream().forEach { it ->
                requestBase.setHeader(it.getKey(), it.getValue())
            }
        }
    }

    <T> T get(Class<T> clazz, Map params, Closure responseHandlerClosure) {
        final String path = Helper.extractCurrentNonBlankStringProperty("ns_navi_path", params)
        HttpGet request = new HttpGet(isAbsolutePath(path) ? path : defaultUri + path)
        initializeHeaders(request, params)
        return execute(clazz, request, responseHandlerClosure)
    }

    private boolean isAbsolutePath(final String path) {
        final URI uri = new URI(path)
        if (uri.isAbsolute() && null != uri.getHost()) {
            return true
        }
        return false
    }

    <T> T postJson(Class<T> clazz, Map params, Closure responseHandlerClosure) {
        String path = Helper.extractCurrentNonBlankStringProperty("ns_navi_path", params)
        HttpPost request = new HttpPost(defaultUri + path)
        initializeHeaders(request, params)
        request.setHeader("Content-type", "application/json");
        final Map body = params.ns_jsonAjax_body as Map ?: new HashMap<>()
        final StringEntity entity = new StringEntity(new Gson().toJson(body))
        request.setEntity(entity)
        return execute(clazz, request, responseHandlerClosure)
    }

    <T> T execute(Class<T> clazz,
                  HttpUriRequest request,
                  Closure responseHandlerClosure) {
        final HttpContext ctx = new BasicHttpContext()
        T result = httpClient.execute(request, ctx)
                .withCloseable({ CloseableHttpResponse response ->
                    E2EHttpResponse e2eHttpResponse = new E2EHttpResponse(response)
                    e2eHttpResponse.setHttpContext(ctx)
                    return responseHandlerClosure.call(e2eHttpResponse)
                }) as T
        return result
    }

    void setContentType(E2EContentType contentType) {
        this.contentType = contentType
    }

    void addHeader(final String name, String value) {
        headers.put(name, value)
    }

    void shutdown() {
        httpClient.close()
    }
}
