package com.vtence.cli.args;

import com.vtence.cli.ParsingException;

public class UnrecognizedOptionException extends ParsingException
{
    private final String option;

    public UnrecognizedOptionException(String option) {
        this.option = option;
    }

    public String getOption() {
        return option;
    }

    public String getMessage() {
        return "unrecognized option: " + option;
    }
}