package com.olegastakhov.microservices.util


class ObjectWrapper<T> {
    private T value;

    ObjectWrapper(T value) {
        this.value = value;
    }

    T getValue() {
        return value;
    }

    void setValue(T value) {
        this.value = value;
    }
}
