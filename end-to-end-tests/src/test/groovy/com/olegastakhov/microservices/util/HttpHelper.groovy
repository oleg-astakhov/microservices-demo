package com.olegastakhov.microservices.util


import com.olegastakhov.microservices.util.httpresponse.E2EMapResponseBody

class HttpHelper {
    static E2EMapResponseBody postAsJsonAjax(final Map<String, Object> params = new HashMap<>()) {
        E2EHttpClient httpClient = Helper.extractCurrentNonNullProperty(E2EHttpClient, "ns_ajaxJsonHttpClient_instance", params)
        final Map httpClientParams = new LinkedHashMap()
        if (params.containsKey("ns_httpClient_headers")) {
            httpClientParams << [ns_httpClient_headers: params.ns_httpClient_headers]
        }
        httpClientParams << [ns_jsonAjax_body: params.ns_jsonAjax_body]
        httpClientParams << [ns_navi_path: Helper.extractCurrentNonBlankStringProperty("ns_navi_path", params)]
        E2EMapResponseBody response = httpClient.postJson(E2EMapResponseBody.class, httpClientParams, E2EHttpResponseHandler.getMapResponseClosure())
        Helper.assertForStatus(200, response, params)
        Helper.putAsIndividualParams(response, params)
        return response
    }

    static E2EMapResponseBody httpGETAsJsonAjax(final Map<String, Object> params = new HashMap<>()) {
        E2EHttpClient httpClient = Helper.extractCurrentNonNullProperty(E2EHttpClient, "ns_ajaxJsonHttpClient_instance", params)
        E2EMapResponseBody response = httpClient.get(E2EMapResponseBody.class, params, E2EHttpResponseHandler.getMapResponseClosure()) as E2EMapResponseBody
        Helper.assertForStatus(200, response, params)
        Helper.putAsIndividualParams(response, params)
        return response
    }
}
