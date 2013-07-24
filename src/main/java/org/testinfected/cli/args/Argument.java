package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public abstract class Argument<T> implements ArgumentSpec<T> {

    protected final Map<Class, TypeCoercer<?>> coercers = new HashMap<Class, TypeCoercer<?>>();

    protected final String name;
    protected TypeCoercer<? extends T> typeCoercer;

    public Argument(String name, TypeCoercer<? extends T> type) {
        this.name = name;
        this.typeCoercer = type;
    }

    public String getName() {
        return name;
    }

    public abstract void handle(Args detected, Input args) throws ParsingException;

    public abstract void printTo(Help help);

    @SuppressWarnings("unchecked")
    protected <S> TypeCoercer<? extends S> coercerFor(Class<? extends S> type) {
        if (!coercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return (TypeCoercer<? extends S>) coercers.get(type);
    }

    protected T convert(String value) throws InvalidArgumentException {
        try {
            return typeCoercer.convert(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(name, value, e);
        }
    }
}
