package test.unit.org.testinfected.cli.gnu;

import org.junit.Test;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.gnu.GnuSyntax;

import static org.junit.Assert.assertEquals;

public class GnuSyntaxTest
{
    GnuSyntax syntax = new GnuSyntax();

    @Test public void
    dashIsPrefixForShortOption() {
        Option option = define("-b");
        assertEquals("b", option.getShortForm());
    }

    @Test public void
    doubleDashIsPrefixForLongOption() {
        Option option = define("--block-size");
        assertEquals("block-size", option.getLongForm());
    }

    @Test public void
    shortOptionAcceptsAnArgumentPattern() {
        Option option = define("-b SIZE");
        assertEquals("SIZE", option.getArgument());
    }

    @Test public void
    longOptionAcceptsAnArgumentPattern() {
        Option option = define("--block-size SIZE");
        assertEquals("SIZE", option.getArgument());
    }

    @Test public void
    optionAcceptsADescription() {
        Option option = define("-b", "description");
        assertEquals("description", option.getDescription());
    }

    @Test public void
    optionsCanBeSpecifiedInLiteralForm() throws ParsingException {
        Option option = define("-b", "--block-size SIZE", "Specifies block size");
        assertEquals("Specifies block size", option.getDescription());
        assertEquals("b", option.getShortForm());
        assertEquals("block-size", option.getLongForm());
        assertEquals("SIZE", option.getArgument());
    }

    private Option define(String... options) {
        return syntax.defineOption("option", options);
    }
}
