package org.testinfected.cli.args;

import org.testinfected.cli.option.Option;

public interface ArgsBuilder
{
    Option defineOption(String name, String... schema);
}
