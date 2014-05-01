package com.vtence.cli.args;

import com.vtence.cli.ParsingException;

public class InvalidArgumentException extends ParsingException
{
    private final String name;
    private final Object value;

    public InvalidArgumentException(String argument, Object value, Exception cause) {
        super(cause);
        this.name = argument;
        this.value = value;
    }

    public String getUnsatisfiedArgument() {
        return name;
    }

    public Object getOffendingValue() {
        return value;
    }

    public String getMessage() {
        return String.format("invalid %s `%s'", name, value);
    }
}
