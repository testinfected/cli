package com.vtence.cli.coercion;

public interface TypeCoercer<T>
{
    T convert(String value) throws Exception;
}

