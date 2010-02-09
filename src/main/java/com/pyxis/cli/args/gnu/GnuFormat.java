package com.pyxis.cli.args.gnu;

import com.pyxis.cli.ParsingException;
import com.pyxis.cli.args.ArgsBuilder;
import com.pyxis.cli.args.ArgsDescription;
import com.pyxis.cli.args.ArgsFormat;
import com.pyxis.cli.args.ArgsParser;
import com.pyxis.cli.option.Option;

import java.io.IOException;

public class GnuFormat implements ArgsFormat
{
    private final ArgsParser parser = new GnuParser();
    private final ArgsBuilder builder = new GnuBuilder();
    private final ArgsDescription description = new GnuDescription();

    public GnuFormat() {}

    public Option defineOption(String name, String... schema) {
        return builder.defineOption(name, schema);
    }

    public String[] parse(Iterable<Option> options, String... args) throws ParsingException {
        return parser.parse(options, args);
    }

    public void setBanner(String banner) {
        description.setBanner(banner);
    }

    public void formatOption(Option option) {
        description.formatOption(option);
    }

    public void appendTo(Appendable appendable) throws IOException {
        description.appendTo(appendable);
    }
}
