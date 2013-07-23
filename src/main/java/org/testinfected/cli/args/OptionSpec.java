package org.testinfected.cli.args;

import org.testinfected.cli.coercion.TypeCoercer;

import java.util.Map;

public interface OptionSpec {

    OptionSpec withShortForm(String shortForm);

    OptionSpec withLongForm(String longForm);

    OptionSpec describedAs(String description);

    OptionSpec takingArgument(String argument);

    OptionSpec defaultingTo(Object value);

    OptionSpec ofType(Class type);

    OptionSpec using(Map<Class<?>, TypeCoercer<?>> coercers);

    OptionSpec ofType(TypeCoercer type);

    OptionSpec whenPresent(Option.Action action);
}
