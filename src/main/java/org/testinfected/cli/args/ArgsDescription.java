package org.testinfected.cli.args;

import org.testinfected.cli.option.Option;

import java.io.IOException;

public interface ArgsDescription
{
    void setBanner(String banner);

    void formatOption(Option option);

    void appendTo(Appendable output) throws IOException;
}
