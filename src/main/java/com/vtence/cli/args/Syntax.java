package com.vtence.cli.args;

public interface Syntax
{
    Option<String> defineOption(String... definition);
}
