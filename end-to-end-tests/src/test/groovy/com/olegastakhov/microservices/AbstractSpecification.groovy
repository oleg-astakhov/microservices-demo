package com.olegastakhov.microservices

import com.olegastakhov.microservices.util.Helper
import spock.lang.Specification

abstract class AbstractSpecification extends Specification {
    final Map<String, Object> params = new HashMap<>() // not shared between tests

    def cleanup() {
        Helper.shutdownAllHttpClients(params)
    }
}
