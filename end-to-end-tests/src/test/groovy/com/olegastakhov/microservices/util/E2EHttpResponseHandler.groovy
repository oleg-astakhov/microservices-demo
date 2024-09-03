package com.olegastakhov.microservices.util

import com.google.gson.Gson
import com.olegastakhov.microservices.util.httpresponse.E2EHttpResponse
import com.olegastakhov.microservices.util.httpresponse.E2EMapResponseBody
import org.apache.commons.io.IOUtils
import org.apache.http.HttpResponse

import java.nio.charset.StandardCharsets

class E2EHttpResponseHandler {
    static String getResponseBodyAsString(final HttpResponse httpResponse) {
        if (null == httpResponse.getEntity()) {
            return null;
        }
        return IOUtils.toString(httpResponse.getEntity().getContent(), StandardCharsets.UTF_8)
    }

    static Map getResponseBodyAsMap(final HttpResponse httpResponse) {
        if (null == httpResponse.getEntity()) {
            return Collections.EMPTY_MAP
        }
        try {
            return new Gson().fromJson(getResponseBodyAsString(httpResponse), Map)
        } catch(Exception e) {
            /*
                TODO not very elegant, but works for now. This is rarely needed in consumers.
                 For instance, when incoming request did not map to a DTO pojo because of BadRequest
                 which resulted in HTTP 400 BadRequest. And since processing didn't get to the point
                 to construct a valid JSON response, even stating that this was a BadRequest, then
                 a non JSON response was received, which wasn't properly parsed into JSON, failed
                 and got here. This is fine if we are happy with just asserting for HTTP response
                 codes in such cases.
            */
            System.err.println("ERROR: ${HttpHelper.class.getName()}: Non parsable JSON response: " + e.getMessage())
            return Collections.EMPTY_MAP
        }
    }

    static Closure getMapResponseClosure() {
        return { E2EHttpResponse response ->
            return new E2EMapResponseBody(httpResponse: response, responseBody: getResponseBodyAsMap(response.httpResponse))
        }
    }
}
