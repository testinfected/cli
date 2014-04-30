package com.vtence.cli.args;

public interface Syntax
{
    Option<?> defineOption(String name, String... definition);
}
