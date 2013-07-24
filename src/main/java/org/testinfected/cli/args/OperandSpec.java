package org.testinfected.cli.args;

import org.testinfected.cli.coercion.TypeCoercer;

public interface OperandSpec<T> extends ArgumentSpec<T> {

    OperandSpec<T> as(String argument);

    OperandSpec<T> describedAs(String message);

    <S> OperandSpec<S> ofType(Class<? extends S> type);

    <S> OperandSpec<S> ofType(TypeCoercer<? extends S> type);
}
