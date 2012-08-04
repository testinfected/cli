package org.testinfected.cli.coercion;

import java.io.File;

public class FileCoercer implements TypeCoercer<File>
{
    public File convert(String value) {
        return new File(value);
    }
}
