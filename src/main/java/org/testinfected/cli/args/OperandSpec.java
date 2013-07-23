package org.testinfected.cli.args;

import org.testinfected.cli.coercion.TypeCoercer;

import java.util.Map;

public interface OperandSpec {

    OperandSpec as(String argument);

    OperandSpec describedAs(String message);

    OperandSpec ofType(Class<?> type);

    OperandSpec ofType(TypeCoercer<?> type);

    OperandSpec using(Map<Class<?>, TypeCoercer<?>> coercers);
}
