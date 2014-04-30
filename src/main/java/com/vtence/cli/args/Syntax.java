package com.vtence.cli.args;

public interface Syntax
{
    Option<String> defineOption(String name, String... definition);
}
