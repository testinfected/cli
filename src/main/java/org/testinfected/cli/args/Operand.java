package org.testinfected.cli.args;

import java.util.Iterator;

public class Operand {
    private final String name;
    private String value;

    public Operand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void consume(Iterator<String> arguments) throws MissingOperandException {
        if (noMore(arguments)) throw new MissingOperandException(this);
        value = nextOf(arguments);
    }

    private String nextOf(Iterator<String> arguments) {
        return arguments.next();
    }

    private boolean noMore(Iterator<String> arguments) {
        return !arguments.hasNext();
    }
}
