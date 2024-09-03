package com.olegastakhov.microservices.util

enum E2EContentType {
    JSON("application/json", "application/javascript", "text/javascript")

    private final String[] ctStrings;

    @Override
    String toString() { return ctStrings[0] }

    /**
     * Builds a string to be used as an HTTP <code>Accept</code> header
     * value, i.e. "application/xml, text/xml"
     * @return
     */
    @SuppressWarnings("unchecked")
    String getAcceptHeader() {
        Iterator<String> iter = Arrays.asList(ctStrings).iterator()
        StringBuilder sb = new StringBuilder();
        while (iter.hasNext()) {
            sb.append(iter.next());
            if (iter.hasNext()) sb.append(", ");
        }
        return sb.toString();
    }

    private E2EContentType(String... contentTypes) {
        this.ctStrings = contentTypes;
    }
}
