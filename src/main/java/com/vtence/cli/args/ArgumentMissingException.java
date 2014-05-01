package com.vtence.cli.args;

import com.vtence.cli.ParsingException;

public class ArgumentMissingException extends ParsingException
{
    private final String option;
    private final String arg;

    public ArgumentMissingException(String option, String arg) {
        this.option = option;
        this.arg = arg;
    }

    public String getUnsatisfiedOption() {
        return option;
    }

    public String getMessage() {
        return String.format("option %s expects argument %s", option, arg);
    }
}
