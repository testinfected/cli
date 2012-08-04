package test.unit.org.testinfected.cli.option;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.IntegerCoercer;
import org.testinfected.cli.option.ArgumentMissingException;
import org.testinfected.cli.option.Option;
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
    optionRequiresShortOrLongOptToBeValid() {
        Option option = new Option("option");
        assertFalse(option.isValid());
        option.setShortForm("b");
        assertTrue(option.isValid());
        option.setShortForm(null);
        assertFalse(option.isValid());
        option.setLongForm("block-size");
        assertTrue(option.isValid());
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
        option.setArgumentPattern("ARG");

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
        Option option = new Option("block size");
        option.setArgumentPattern("SIZE");
        option.setCoercer(new IntegerCoercer());

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
