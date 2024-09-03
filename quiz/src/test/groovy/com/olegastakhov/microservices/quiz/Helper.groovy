package com.olegastakhov.microservices.quiz

import java.util.stream.Collectors
import java.util.stream.Stream

class Helper {
    static Set<String> toSet(String... item) {
        return Stream.of(item).collect(Collectors.toSet())
    }
}
