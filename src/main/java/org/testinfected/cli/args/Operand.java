package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public class Operand implements OperandSpec {

    private final Map<Class<?>, TypeCoercer<?>> coercers = new HashMap<Class<?>, TypeCoercer<?>>();

    private final String name;
    private TypeCoercer<?> typeCoercer;

    private String displayName;
    private String description;

    public static Operand named(String name) {
        return new Operand(name);
    }

    public Operand(String name) {
        this(name, new StringCoercer());
    }

    public Operand(String name, TypeCoercer type) {
        this.name = name;
        this.typeCoercer = type;
    }

    public String getName() {
        return name;
    }

    public Operand as(String argument) {
        this.displayName = argument;
        return this;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name.toUpperCase();
    }

    public Operand describedAs(String message) {
        this.description = message;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public Operand ofType(Class<?> type) {
        return ofType(coercerFor(type));
    }

    public Operand ofType(TypeCoercer<?> type) {
        this.typeCoercer = type;
        return this;
    }

    public Operand using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.coercers.putAll(coercers);
        return this;
    }

    private TypeCoercer<?> coercerFor(Class<?> type) {
        if (!coercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return coercers.get(type);
    }

    public String getValue(Args detected) {
        return detected.get(name);
    }

    public void printTo(Help help) {
        help.print(this);
    }

    public void consume(Args detected, Input args) throws ParsingException {
        if (args.empty()) throw new MissingOperandException(this);
        detected.put(name, value(args));
    }

    private Object value(Input args) throws InvalidArgumentException {
        return convert(args.next());
    }

    private Object convert(String value) throws InvalidArgumentException {
        try {
            return typeCoercer.convert(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(name, value, e);
        }
    }
}
