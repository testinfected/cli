package com.vtence.cli.coercion;

public class ClassCoercer implements TypeCoercer<Class>
{
    private final ClassLoader classLoader;

    public ClassCoercer() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ClassCoercer(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    public Class convert(String value) throws Exception {
        return classLoader.loadClass(value);
    }
}
