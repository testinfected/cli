package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.Iterator;

public class Operand {

    private final String name;
    private final TypeCoercer typeCoercer;

    private String displayName;
    private String description;
    private Object value;

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

    public void setDisplayName(String name) {
        this.displayName = name;
    }

    public String getDisplayName() {
        return displayName != null ? displayName : name.toUpperCase();
    }

    public Object getValue() {
        return value;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public void printTo(Help help) {
        help.printOperand(this);
    }

    public void consume(Iterator<String> arguments) throws ParsingException {
        if (noMore(arguments)) throw new MissingOperandException(this);
        value = convert(nextOf(arguments));
    }

    private String nextOf(Iterator<String> arguments) {
        return arguments.next();
    }

    private boolean noMore(Iterator<String> arguments) {
        return !arguments.hasNext();
    }

    private Object convert(String value) throws InvalidArgumentException {
        try {
            return typeCoercer.convert(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(name, value, e);
        }
    }
}
