package com.vtence.cli.coercion;

public class StringCoercer implements TypeCoercer<String>
{
    public String convert(String value) {
        return value;
    }
}
