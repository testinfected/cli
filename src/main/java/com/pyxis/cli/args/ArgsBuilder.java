package com.pyxis.cli.args;

import com.pyxis.cli.option.Option;

public interface ArgsBuilder
{
    Option defineOption(String name, String... schema);
}
