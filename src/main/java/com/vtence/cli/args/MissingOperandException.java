package com.vtence.cli.args;

import com.vtence.cli.ParsingException;

public class MissingOperandException extends ParsingException {
    private Operand<?> operand;

    public MissingOperandException(Operand<?> operand) {
        this.operand = operand;
    }

    public String getMissingOperand() {
        return operand.getName();
    }

    public String getMessage() {
        return String.format("operand %s missing", operand.getName());
    }
}