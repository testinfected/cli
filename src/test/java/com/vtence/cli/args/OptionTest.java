package com.vtence.cli.args;

import org.junit.Test;
import com.vtence.cli.ParsingException;
import com.vtence.cli.coercion.IntegerCoercer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static com.vtence.cli.args.Input.input;

public class OptionTest
{
    Args detected = new Args();

    @Test
    public void hasInitiallyANullValue() throws ParsingException {
        Option<?> option = Option.option("-o");
        assertNull(option.get(detected));
    }

    @Test
    public void isConsideredOnWhenDetectedAndTakingNoArgument() throws ParsingException {
        Option<String> option = Option.option("-o");
        option.handle(detected, input());
        assertEquals("true", option.get(detected));

        Option<Boolean> flag = Option.flag("-f");
        flag.handle(detected, input());
        assertEquals(true, flag.get(detected));
    }

    @Test
    public void canRequireAnArgument() throws ParsingException {
        Option<?> option = Option.option("-o");
        option.takingArgument("ARG");

        try {
            option.handle(detected, input());
            fail("Expected exception " + ArgumentMissingException.class.getName());
        } catch (ArgumentMissingException expected) {
            assertTrue(true);
        }

        option.handle(detected, input("value"));
        assertEquals("value", option.get(detected));
    }

    @Test
    public void typeCanBeEnforced() throws ParsingException {
        Option<Integer> option = Option.option("-b").takingArgument("SIZE").ofType(new IntegerCoercer());

        option.handle(detected, input("1024"));
        int size = option.get(detected);
        assertEquals(1024, size);
    }

    @Test
    public void canHaveMultipleForms() {
        Option<String> option = Option.option("-h").alias("--host");

        assertTrue("matching short form", option.matches("-h"));
        assertTrue("matching long form", option.matches("--host"));
    }
}
