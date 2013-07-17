package org.testinfected.cli.args;

public class OperandBuilder {
    private final String name;

    private String argumentPattern;

    public static OperandBuilder operandNamed(String name) {
        return new OperandBuilder(name);
    }

    protected OperandBuilder(String name) {
        this.name = name;
    }

    public OperandBuilder as(String argument) {
        this.argumentPattern = argument;
        return this;
    }

    public Operand make() {
        Operand operand = new Operand(name);
        operand.setDisplayName(argumentPattern);
        return operand;
    }
}
