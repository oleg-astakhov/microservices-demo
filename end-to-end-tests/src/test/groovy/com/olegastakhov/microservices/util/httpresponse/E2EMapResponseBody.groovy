package com.olegastakhov.microservices.util.httpresponse

class E2EMapResponseBody implements StatusCodeAware {
    E2EHttpResponse httpResponse
    Map responseBody

    @Override
    int getStatusCode() {
        return httpResponse.getStatusCode()
    }
}
