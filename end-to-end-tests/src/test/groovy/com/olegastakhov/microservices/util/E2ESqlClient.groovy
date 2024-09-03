package com.olegastakhov.microservices.util

import groovy.sql.GroovyRowResult
import groovy.sql.Sql

class E2ESqlClient {
    private Sql datasource

    E2ESqlClient(Sql datasource) {
        this.datasource = datasource
    }

    Sql getSql() {
        return datasource
    }

    GroovyRowResult getRow(final String sqlSelect,
                           final Map params = new HashMap<>()) {
        Optional<GroovyRowResult> row = findRow(sqlSelect, params)
        assert row.isPresent()
        return row.get()
    }

    Optional<GroovyRowResult> findRow(final String sqlSelect,
                                      final Map params = new HashMap<>()) {
        List<GroovyRowResult> rows = datasource.rows("${sqlSelect}".toString(), params)

        if (rows.isEmpty()) {
            return Optional.empty()
        }
        if (rows.size() > 1) {
            throw new RuntimeException("Multiple results are not expected")
        }
        return Optional.of(rows[0])
    }
}
