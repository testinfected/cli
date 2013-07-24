package org.testinfected.cli.args;

public interface ArgumentSpec<T> {

    T get(Args args);
}
