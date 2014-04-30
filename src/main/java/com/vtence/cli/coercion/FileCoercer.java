package com.vtence.cli.coercion;

import java.io.File;

public class FileCoercer implements TypeCoercer<File>
{
    public File convert(String value) {
        return new File(value);
    }
}
