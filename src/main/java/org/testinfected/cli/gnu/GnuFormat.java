package org.testinfected.cli.gnu;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.ArgsSpecification;
import org.testinfected.cli.args.ArgsDescription;
import org.testinfected.cli.args.ArgsFormat;
import org.testinfected.cli.args.ArgsParser;
import org.testinfected.cli.args.Option;

import java.io.IOException;
import java.util.List;

public class GnuFormat implements ArgsFormat
{
    private final ArgsParser parser = new GnuParser();
    private final ArgsSpecification specification = new GnuSpecification();
    private final ArgsDescription description = new GnuDescription();

    public GnuFormat() {}

    public Option defineOption(String name, String... schema) {
        return specification.defineOption(name, schema);
    }

    public List<String> parse(Iterable<Option> options, String... args) throws ParsingException {
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
