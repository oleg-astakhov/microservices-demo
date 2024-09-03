package com.olegastakhov.microservices.util.httpresponse

import org.apache.http.Header
import org.apache.http.HttpHeaders
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.impl.client.RedirectLocations
import org.apache.http.protocol.HttpContext

class E2EHttpResponse implements StatusCodeAware {
    CloseableHttpResponse httpResponse
    HttpContext httpContext

    E2EHttpResponse(CloseableHttpResponse httpResponse) {
        this.httpResponse = httpResponse
    }

    @Override
    int getStatusCode() {
        return httpResponse.getStatusLine().getStatusCode()
    }

    String getContentType() {
        final Header[] headers = httpResponse.getHeaders(HttpHeaders.CONTENT_TYPE)
        assert 1 == headers.size()
        return headers[0].getElements()[0].name
    }

    Header[] getAllHeaders() {
        return httpResponse.getAllHeaders()
    }

    Header getFirstHeader(final String name) {
        return httpResponse.getFirstHeader(name)
    }

    RedirectLocations getRedirectLocations() {
        return httpContext.getAttribute("http.protocol.redirect-locations") as RedirectLocations
    }
}
