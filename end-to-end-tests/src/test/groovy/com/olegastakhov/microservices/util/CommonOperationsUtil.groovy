package com.olegastakhov.microservices.util

class CommonOperationsUtil {
    static CommonOperationsUtil INSTANCE = new CommonOperationsUtil()

    Map cloneAndModifyParamsMap(final Map<String, Object> params = new HashMap<>(), final boolean copyHeaders = true) {
        final Map httpCallParams = new LinkedHashMap()
        httpCallParams.putAll(params)
        httpCallParams.remove("ns_httpClient_headers")
        if (copyHeaders) {
            doCopyHeaders(params, httpCallParams)
        }
        return httpCallParams
    }

    private void doCopyHeaders(Map<String, Object> params, LinkedHashMap httpCallParams) {
        final Map headersFrom = params.ns_httpClient_headers
        final Map headersTo = new HashMap()
        if (null != headersFrom) {
            if (headersFrom.containsKey("correlation_id")) {
                headersTo << [correlation_id: headersFrom.correlation_id]
                httpCallParams << [ns_httpClient_headers: headersTo]
            }
        }
    }
}
