package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

public class Operand {

    private final String name;
    private final TypeCoercer typeCoercer;

    private String displayName;
    private String description;

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

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public String getValue(Args detected) {
        return detected.get(name);
    }

    public void printTo(Help help) {
        help.printOperand(this);
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
