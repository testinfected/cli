package com.vtence.cli.args;

public interface ArgumentSpec<T> {

    T get(Args args);
}
