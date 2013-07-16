package org.testinfected.cli.args;

import org.testinfected.cli.args.Option;

public interface ArgsBuilder
{
    Option defineOption(String name, String... schema);
}
