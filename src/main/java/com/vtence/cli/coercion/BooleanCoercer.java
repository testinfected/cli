package com.vtence.cli.coercion;

public class BooleanCoercer implements TypeCoercer<Boolean> {

    public Boolean convert(String value) throws Exception {
        return Boolean.valueOf(value);
    }
}
