package com.vtence.cli.coercion;

public class IntegerCoercer implements TypeCoercer<Integer>
{
    public Integer convert(String value) {
        return Integer.valueOf(value);
    }
}

