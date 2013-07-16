package org.testinfected.cli.args;

public interface ArgsSpecification
{
    Option defineOption(String name, String... schema);
}
