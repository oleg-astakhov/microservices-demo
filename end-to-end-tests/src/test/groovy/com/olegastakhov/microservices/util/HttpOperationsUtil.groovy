package com.olegastakhov.microservices.util

import com.olegastakhov.microservices.util.httpresponse.E2EMapResponseBody

class HttpOperationsUtil {
    static E2EMapResponseBody httpGETAsJsonAjax(final Map<String, Object> params = new HashMap<>()) {
        final Map httpCallParams = CommonOperationsUtil.INSTANCE.cloneAndModifyParamsMap(params)
        httpCallParams << [ns_ajaxJsonHttpClient_instance: Helper.putDefaultOrGetActual(params, "ns_public_ajaxJsonHttpClient_instance", { HTTPClientFactory.getInstance().createPublicRestClient() })]
        return HttpHelper.httpGETAsJsonAjax(httpCallParams)
    }
}