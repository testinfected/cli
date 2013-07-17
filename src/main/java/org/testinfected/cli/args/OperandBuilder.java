package org.testinfected.cli.args;

import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public class OperandBuilder {
    private final String name;

    private final Map<Class, TypeCoercer<?>> typeCoercers = new HashMap<Class, TypeCoercer<?>>();

    private String argumentPattern;
    private String description;
    private TypeCoercer coercer = new StringCoercer();

    public static OperandBuilder operand(String name) {
        return new OperandBuilder(name);
    }

    protected OperandBuilder(String name) {
        this.name = name;
    }

    public OperandBuilder as(String argument) {
        this.argumentPattern = argument;
        return this;
    }

    public OperandBuilder help(String message) {
        this.description = message;
        return this;
    }

    public OperandBuilder ofType(Class<?> type) {
        return coerceWith(coercerFor(type));
    }

    public OperandBuilder coerceWith(TypeCoercer typeCoercer) {
        this.coercer = typeCoercer;
        return this;
    }

    public OperandBuilder using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.typeCoercers.putAll(coercers);
        return this;
    }

    private TypeCoercer coercerFor(Class type) {
        if (!typeCoercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return typeCoercers.get(type);
    }

    public Operand make() {
        Operand operand = new Operand(name, coercer);
        operand.setDisplayName(argumentPattern);
        operand.setDescription(description);
        return operand;
    }
}
