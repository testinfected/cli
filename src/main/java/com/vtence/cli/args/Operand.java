package com.vtence.cli.args;

import com.vtence.cli.ParsingException;
import com.vtence.cli.coercion.StringCoercer;
import com.vtence.cli.coercion.TypeCoercer;

import java.util.Map;

public class Operand<T> extends Argument<T> implements OperandSpec<T> {

    private String displayName;
    private String description;

    public static Operand<String> named(String name) {
        return new Operand<String>(name, new StringCoercer());
    }

    protected Operand(String name, TypeCoercer<? extends T> type) {
        super(name, type);
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

    public T get(Args args) {
        return args.get(name);
    }

    public void printTo(Help help) {
        help.print(this);
    }

    public void handle(Args detected, Input args) throws ParsingException {
        if (args.empty()) throw new MissingOperandException(this);
        detected.put(this, value(args));
    }

    public boolean matches(String identifier) {
        return name.equals(identifier);
    }

    private T value(Input args) throws InvalidArgumentException {
        return convert(args.next());
    }
}
