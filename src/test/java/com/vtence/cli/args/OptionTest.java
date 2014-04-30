package com.vtence.cli.args;

import org.junit.Test;
import com.vtence.cli.ParsingException;
import com.vtence.cli.coercion.IntegerCoercer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static com.vtence.cli.args.Input.listOf;

public class OptionTest
{
    Args detected = new Args();

    @Test public void
    optionValueIsNullByDefault() throws ParsingException {
        Option<?> option = Option.named("option");
        assertNull(option.get(detected));
    }

    @Test public void
    optionWithoutArgumentIsConsideredOn() throws ParsingException {
        Option<String> option = Option.named("option");
        option.handle(detected, listOf());
        assertEquals("true", option.get(detected));

        Option<Boolean> flag = Option.flag("flag");
        flag.handle(detected, listOf());
        assertEquals(true, flag.get(detected));
    }

    @Test public void
    optionCanRequireAnArgument() throws ParsingException {
        Option<?> option = Option.named("option");
        option.takingArgument("ARG");

        try {
            option.handle(detected, listOf());
            fail();
        } catch (ArgumentMissingException expected) {
            assertTrue(true);
        }

        option.handle(detected, listOf("value"));
        assertEquals("value", option.get(detected));
    }

    @Test public void
    optionTypeCanBeEnforced() throws ParsingException {
        Option<String> option = Option.named("block size");
        option.takingArgument("SIZE").ofType(new IntegerCoercer());

        option.handle(detected, listOf("1024"));
        assertEquals(1024, option.get(detected));
    }

    @Test public void
    optionCanHaveADefaultValue() throws ParsingException {
        Option<String> option = Option.named("host");
        option.defaultingTo("localhost");

        assertTrue(option.hasDefaultValue());
        assertEquals("localhost", option.getDefaultValue());
    }
}
