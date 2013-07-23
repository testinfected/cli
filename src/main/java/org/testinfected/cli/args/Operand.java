package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public class Operand<T> implements OperandSpec<T> {

    private final Map<Class<?>, TypeCoercer<?>> coercers = new HashMap<Class<?>, TypeCoercer<?>>();

    private final String name;
    private TypeCoercer<? extends T> typeCoercer;

    private String displayName;
    private String description;

    public static Operand<String> named(String name) {
        return new Operand<String>(name, new StringCoercer());
    }

    protected Operand(String name, TypeCoercer<? extends T> type) {
        this.name = name;
        this.typeCoercer = type;
    }

    public String getName() {
        return name;
    }

    public Operand<T> as(String argument) {
        this.displayName = argument;
        return this;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name.toUpperCase();
    }

    public Operand<T> describedAs(String message) {
        this.description = message;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public <S> Operand<S> ofType(Class<? extends S> type) {
        return ofType(coercerFor(type));
    }

    @SuppressWarnings("unchecked")
    public <S> Operand<S> ofType(TypeCoercer<? extends S> type) {
        this.typeCoercer = (TypeCoercer<? extends T>) type;
        return (Operand<S>) this;
    }

    public Operand<T> using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.coercers.putAll(coercers);
        return this;
    }

    @SuppressWarnings("unchecked")
    private <S> TypeCoercer<? extends S> coercerFor(Class<? extends S> type) {
        if (!coercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return (TypeCoercer<? extends S>) coercers.get(type);
    }

    public T get(Args args) {
        return args.get(name);
    }

    public void printTo(Help help) {
        help.print(this);
    }

    public void consume(Args detected, Input args) throws ParsingException {
        if (args.empty()) throw new MissingOperandException(this);
        detected.put(name, value(args));
    }

    private T value(Input args) throws InvalidArgumentException {
        return convert(args.next());
    }

    private T convert(String value) throws InvalidArgumentException {
        try {
            return typeCoercer.convert(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(name, value, e);
        }
    }
}
