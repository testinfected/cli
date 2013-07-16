package org.testinfected.cli;

public class OperandBuilder {
    private final String name;

    public static OperandBuilder operandNamed(String name) {
        return new OperandBuilder(name);
    }

    protected OperandBuilder(String name) {
        this.name = name;
    }

    public Operand make() {
        return new Operand(name);
    }
}
