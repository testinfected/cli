package com.vtence.cli.gnu;

import org.junit.Test;
import com.vtence.cli.ParsingException;
import com.vtence.cli.args.Args;
import com.vtence.cli.args.Option;
import com.vtence.cli.args.Options;
import com.vtence.cli.args.UnrecognizedOptionException;

import java.util.ArrayList;
import java.util.Collection;

import static com.vtence.cli.args.Option.flag;
import static com.vtence.cli.args.Option.option;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static com.vtence.cli.args.Input.input;

public class GnuParserTest
{
    GnuParser parser = new GnuParser();

    Collection<Option<?>> options = new ArrayList<Option<?>>();
    Collection<String> nonOptions = new ArrayList<String>();
    Args detected = new Args();

    @Test public void
    detectsNothingWithoutDefinedOptions() throws ParsingException {
        parse("1", "2", "3");
        assertEquals(0, detected.size());
        assertEquals(asList("1", "2", "3"), nonOptions);
    }

    @Test public void
    detectsDefinedOptionsByTheirShortForm() throws ParsingException {
        Option<?> debug = define(option("-x"));
        Option<?> verbose = define(option("-v"));

        parse("-x");
        assertNotNull(debug.get(detected));
        assertNull(verbose.get(detected));
    }

    @Test public void
    detectsDefinedOptionsByTheirLongForm() throws ParsingException {
        Option<?> raw = define(flag("--raw"));

        parse("--raw");
        assertNotNull(raw.get(detected));
    }

    @Test public void
    detectsDefinedOptionsParameters() throws ParsingException {
        Option<String> blockSize = define(option("-b").alias("--block-size").takingArgument("SIZE"));
        parse("-b", "1024");
        assertEquals("1024", blockSize.get(detected));
    }

    @Test public void
    returnsNonOptionArguments() throws ParsingException {
        define(flag("--raw"));

        parse("--raw", "input", "output");
        assertEquals(asList("input", "output"), nonOptions);
    }

    @Test public void
    supportsMultipleOptionsWithParameters() throws ParsingException {
        Option<Boolean> human = define(flag("-h").describedAs("Human readable format"));
        Option<String> blockSize = define(option("-b").alias("--block-size").takingArgument("SIZE"));
        Option<Boolean> debug = define(flag("-x"));

        parse("-h", "--block-size", "1024", "-x", "input", "output");
        assertEquals(TRUE, human.get(detected));
        assertEquals("1024", blockSize.get(detected));
        assertEquals(TRUE, debug.get(detected));
        assertEquals(asList("input", "output"), nonOptions);
    }

    @Test public void
    complainsOfUnrecognizedOptions() throws ParsingException {
        try {
            parse("-x");
            fail();
        }
        catch (UnrecognizedOptionException expected) {
            assertEquals("-x", expected.getOption());
        }
    }

    private <T> Option<T> define(Option<T> option) {
        options.add(option);
        return option;
    }

    private void parse(String... args) throws ParsingException {
        nonOptions.addAll(parser.parse(detected, new Options(options), input(args)));
    }
}