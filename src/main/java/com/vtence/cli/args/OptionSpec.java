package com.vtence.cli.args;

import com.vtence.cli.coercion.TypeCoercer;

public interface OptionSpec<T> extends ArgumentSpec<T> {

    OptionSpec<T> alias(String form);

    OptionSpec<T> describedAs(String description);

    OptionSpec<String> takingArgument(String argument);

    <S> OptionSpec<S> ofType(Class<? extends S> type);

    <S> OptionSpec<S> ofType(TypeCoercer<? extends S> type);

    <V extends T> OptionSpec<T> defaultingTo(V value);

    OptionSpec<T> whenPresent(Option.Action<T> action);
}
