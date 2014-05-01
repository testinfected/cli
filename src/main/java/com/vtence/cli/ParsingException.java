package com.vtence.cli;

public abstract class ParsingException extends Exception
{
    protected ParsingException() {
    }

    protected ParsingException(Throwable cause) {
        super(cause);
    }
}
