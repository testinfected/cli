package test.unit.org.testinfected.cli.args;

import org.junit.Test;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Args;
import org.testinfected.cli.args.ArgumentMissingException;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.coercion.IntegerCoercer;

import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testinfected.cli.args.Input.listOf;

public class OptionTest
{
    Args detected = new Args();

    @Test public void
    optionValueIsNullByDefault() throws ParsingException {
        Option option = new Option("option");
        assertNull(option.getValue(detected));
    }

    @Test public void
    optionWithoutArgumentIsConsideredBoolean() throws ParsingException {
        Option option = new Option("option");
        option.handle(detected, listOf());
        assertEquals(TRUE, option.getValue(detected));
    }

    @Test public void
    optionCanRequireAnArgument() throws ParsingException {
        Option option = new Option("option");
        option.takingArgument("ARG");

        try {
            option.handle(detected, listOf());
            fail();
        }
        catch (ArgumentMissingException expected) {
        }

        option.handle(detected, listOf("value"));
        assertEquals("value", option.getValue(detected));
    }

    @Test public void
    optionTypeCanBeEnforced() throws ParsingException {
        Option option = new Option("block size", new IntegerCoercer());
        option.takingArgument("SIZE");

        option.handle(detected, listOf("1024"));
        assertEquals(1024, option.getValue(detected));
    }

    @Test public void
    optionCanHaveADefaultValue() throws ParsingException {
        Option option = new Option("block size");
        option.defaultingTo(1024);

        assertTrue(option.hasDefaultValue());
        assertEquals(1024, option.getDefaultValue());
    }
}
