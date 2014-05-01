package com.vtence.cli.coercion;

import java.util.Locale;

public class LocaleCoercer implements TypeCoercer<Locale>
{
    public Locale convert(String value) {
        return new Locale(value);
    }
}
