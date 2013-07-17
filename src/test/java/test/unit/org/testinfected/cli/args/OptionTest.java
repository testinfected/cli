package test.unit.org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.IntegerCoercer;
import org.testinfected.cli.args.ArgumentMissingException;
import org.testinfected.cli.args.Option;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

public class OptionTest
{
    @Test public void
    optionValueIsNullByDefault() throws ParsingException {
        Option option = new Option("option");
        assertNull(option.getValue());
    }

    @Test public void
    optionWithoutArgumentIsConsideredBoolean() throws ParsingException {
        Option option = new Option("option");
        option.consume(arguments());
        assertEquals(Boolean.TRUE, option.getValue());
    }

    @Test public void
    optionCanRequireAnArgument() throws ParsingException {
        Option option = new Option("option");
        option.setArgument("ARG");

        try {
            option.consume(arguments());
            fail();
        }
        catch (ArgumentMissingException expected) {
        }

        option.consume(arguments("value"));
        assertEquals("value", option.getValue());
    }

    @Test public void
    optionTypeCanBeEnforced() throws ParsingException {
        Option option = new Option("block size", new IntegerCoercer());
        option.setArgument("SIZE");

        option.consume(arguments("1024"));
        assertEquals(1024, option.getValue());
    }

    @Test public void
    optionCanHaveADefaultValue() throws ParsingException {
        Option option = new Option("block size");
        option.setValue(1024);

        assertEquals(1024, option.getValue());
    }

    private Iterator<String> arguments(String... args) {
        return Arrays.asList(args).iterator();
    }
}
