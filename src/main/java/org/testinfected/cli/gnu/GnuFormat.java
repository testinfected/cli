package org.testinfected.cli.gnu;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Parser;
import org.testinfected.cli.args.Syntax;
import org.testinfected.cli.args.Help;
import org.testinfected.cli.args.Format;
import org.testinfected.cli.args.Option;

import java.io.IOException;
import java.util.List;

public class GnuFormat implements Format
{
    private final Parser parser = new GnuParser();
    private final Syntax syntax = new GnuSyntax();
    private final Help help = new GnuHelp();

    public GnuFormat() {}

    public Option defineOption(String name, String... schema) {
        return syntax.defineOption(name, schema);
    }

    public List<String> parse(Iterable<Option> options, String... args) throws ParsingException {
        return parser.parse(options, args);
    }

    public void displayProgram(String name) {
        help.displayProgram(name);
    }

    public void displayVersion(String number) {
        help.displayVersion(number);
    }

    public void displayDescription(String description) {
        help.displayDescription(description);
    }

    public void displayEnding(String epilog) {
        help.displayEnding(epilog);
    }

    public void displayOption(Option option) {
        help.displayOption(option);
    }

    public void appendTo(Appendable appendable) throws IOException {
        help.appendTo(appendable);
    }
}
