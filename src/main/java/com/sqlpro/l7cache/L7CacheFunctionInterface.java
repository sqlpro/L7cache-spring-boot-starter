package com.sqlpro.l7cache;

@FunctionalInterface
public interface L7CacheFunctionInterface<T> {
    T apply();
}
