package com.vtence.cli.args;

import com.vtence.cli.ParsingException;

public class MissingOperandException extends ParsingException {
    private final String operand;

    public MissingOperandException(String name) {
        this.operand = name;
    }

    public String getMissingOperand() {
        return operand;
    }

    public String getMessage() {
        return String.format("operand %s missing", operand);
    }
}
