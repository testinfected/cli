package test.unit.org.testinfected.cli.args;

import org.junit.Test;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Args;
import org.testinfected.cli.args.ArgumentMissingException;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.coercion.IntegerCoercer;

import java.util.Arrays;
import java.util.Iterator;

import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
        option.handleArguments(detected, arguments());
        assertEquals(TRUE, option.getValue(detected));
    }

    @Test public void
    optionCanRequireAnArgument() throws ParsingException {
        Option option = new Option("option");
        option.setArgument("ARG");

        try {
            option.handleArguments(detected, arguments());
            fail();
        }
        catch (ArgumentMissingException expected) {
        }

        option.handleArguments(detected, arguments("value"));
        assertEquals("value", option.getValue(detected));
    }

    @Test public void
    optionTypeCanBeEnforced() throws ParsingException {
        Option option = new Option("block size", new IntegerCoercer());
        option.setArgument("SIZE");

        option.handleArguments(detected, arguments("1024"));
        assertEquals(1024, option.getValue(detected));
    }

    @Test public void
    optionCanHaveADefaultValue() throws ParsingException {
        Option option = new Option("block size");
        option.setDefaultValue(1024);

        assertTrue(option.hasDefaultValue());
        assertEquals(1024, option.getDefaultValue());
    }

    private Iterator<String> arguments(String... args) {
        return Arrays.asList(args).iterator();
    }
}
