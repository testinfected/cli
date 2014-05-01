package com.vtence.cli.gnu;

import org.junit.Test;
import com.vtence.cli.ParsingException;
import com.vtence.cli.args.Option;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class GnuSyntaxTest
{
    GnuSyntax syntax = new GnuSyntax();

    @Test
    public void dashIsPrefixForShortOption() {
        Option<?> option = syntax.defineOption("-b");
        assertTrue(option.matches("-b"));
    }

    @Test
    public void doubleDashIsPrefixForLongOption() {
        Option<?> option = syntax.defineOption("--block-size");
        assertTrue(option.matches("--block-size"));
    }

    @Test
    public void shortAndLongOptionCanBeBothProvided() {
        Option<?> option = syntax.defineOption("-b", "--block-size");
        assertTrue(option.matches("-b"));
        assertTrue(option.matches("--block-size"));
    }

    @Test
    public void orderInWhichOptionAreProvidedDoesNotMatter() {
        Option<?> option = syntax.defineOption("--block-size", "-b");
        assertTrue(option.matches("-b"));
        assertTrue(option.matches("--block-size"));
    }

    @Test
    public void shortOptionAcceptsAnArgumentPattern() {
        Option<?> option = syntax.defineOption("-b SIZE");
        assertEquals("SIZE", option.getArgument());
    }

    @Test
    public void longOptionAcceptsAnArgumentPattern() {
        Option<?> option = syntax.defineOption("--block-size SIZE");
        assertEquals("SIZE", option.getArgument());
    }

    @Test
    public void optionAcceptsADescription() {
        Option<?> option = syntax.defineOption("-b", "description");
        assertEquals("description", option.getDescription());
    }

    @Test
    public void optionsCanBeSpecifiedInLiteralForm() throws ParsingException {
        Option<?> option = syntax.defineOption("-b", "--block-size SIZE", "Specifies block size");
        assertEquals("Specifies block size", option.getDescription());
        assertTrue(option.matches("-b"));
        assertTrue(option.matches("--block-size"));
        assertEquals("SIZE", option.getArgument());
    }

    @Test(expected = IllegalArgumentException.class)
    public void oneOfLongOrShortOptionIsRequired() {
        syntax.defineOption();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shortOptionCannotBeSpecifiedTwice() {
        syntax.defineOption("-s", "-q");
    }

    @Test(expected = IllegalArgumentException.class)
    public void longOptionCannotBeSpecifiedTwice() {
        syntax.defineOption("--silent", "--quiet");
    }

    @Test(expected = IllegalArgumentException.class)
    public void descriptionCannotBeSpecifiedTwice() {
        syntax.defineOption("Operate silently", "Operate quietly");
    }

    @Test(expected = IllegalArgumentException.class)
    public void argumentCannotBeSpecifiedTwice() {
        syntax.defineOption("-b 1024", "--block-size 2048");
    }
}
