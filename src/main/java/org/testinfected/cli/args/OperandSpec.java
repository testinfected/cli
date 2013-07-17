package org.testinfected.cli.args;

import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public class OperandSpec {
    private final Map<Class<?>, TypeCoercer<?>> coercers = new HashMap<Class<?>, TypeCoercer<?>>();

    private final String name;

    private TypeCoercer type = new StringCoercer();
    private String argumentPattern;
    private String description;

    public static OperandSpec operand(String name) {
        return new OperandSpec(name);
    }

    protected OperandSpec(String name) {
        this.name = name;
    }

    public OperandSpec as(String argument) {
        this.argumentPattern = argument;
        return this;
    }

    public OperandSpec help(String message) {
        this.description = message;
        return this;
    }

    public OperandSpec ofType(Class<?> type) {
        return ofType(coercerFor(type));
    }

    public OperandSpec ofType(TypeCoercer<?> type) {
        this.type = type;
        return this;
    }

    public OperandSpec using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.coercers.putAll(coercers);
        return this;
    }

    private TypeCoercer<?> coercerFor(Class<?> type) {
        if (!coercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return coercers.get(type);
    }

    public Operand make() {
        Operand operand = new Operand(name, type);
        operand.setDisplayName(argumentPattern);
        operand.setDescription(description);
        return operand;
    }
}
