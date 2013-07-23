package org.testinfected.cli.args;

import org.testinfected.cli.coercion.TypeCoercer;

public interface OptionSpec<T> {

    OptionSpec<T> withShortForm(String shortForm);

    OptionSpec<T> withLongForm(String longForm);

    OptionSpec<T> describedAs(String description);

    OptionSpec<String> takingArgument(String argument);

    <V extends T> OptionSpec<T> defaultingTo(V value);

    <S> OptionSpec<S> ofType(Class<? extends S> type);

    <S> OptionSpec<S> ofType(TypeCoercer<? extends S> type);

    OptionSpec<T> whenPresent(Option.Action<T> action);

    T get(Args args);
}
