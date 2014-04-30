/*
 * Copyright (c) 2006 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */

package com.vtence.cli.gnu;

import org.junit.Test;
import com.vtence.cli.ParsingException;
import com.vtence.cli.args.Args;
import com.vtence.cli.args.Option;
import com.vtence.cli.args.Options;
import com.vtence.cli.args.UnrecognizedOptionException;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static com.vtence.cli.args.Input.listOf;
import static com.vtence.cli.args.Option.named;

public class GnuParserTest
{
    GnuParser parser = new GnuParser();

    Collection<Option<?>> options = new ArrayList<Option<?>>();
    Collection<String> nonOptions = new ArrayList<String>();
    Args detected = new Args();

    @Test public void
    detectsNothingWithoutDefinedOption() throws ParsingException {
        parse("1", "2", "3");
        assertEquals(0, detected.size());
        assertEquals(asList("1", "2", "3"), nonOptions);
    }

    @Test public void
    detectsDefinedOptionsByTheirShortForm() throws ParsingException {
        Option<Boolean> debug = define(named("debug").withShortForm("x"));
        Option<Boolean> verbose = define(named("verbose").withShortForm("v"));

        parse("-x");
        assertEquals(true, debug.get(detected));
        assertNull(verbose.get(detected));
    }

    @Test public void
    detectsDefinedOptionsByTheirLongForm() throws ParsingException {
        Option<Boolean> raw = define(named("raw").withLongForm("raw"));

        parse("--raw");
        assertEquals(TRUE, raw.get(detected));
    }

    @Test public void
    detectsDefinedOptionsParameters() throws ParsingException {
        Option<String> blockSize = define(named("block").withShortForm("b").withLongForm("block-size").takingArgument("SIZE"));
        parse("-b", "1024");
        assertEquals("1024", blockSize.get(detected));
    }

    @Test public void
    returnNonOptionArguments() throws ParsingException {
        define(Option.named("raw").withLongForm("raw"));

        parse("--raw", "input", "output");
        assertEquals(asList("input", "output"), nonOptions);
    }

    @Test public void
    supportsMultipleOptionsWithParameters() throws ParsingException {
        Option<Boolean> human = define(named("human").withShortForm("h").describedAs("Human readable format"));
        Option<String> blockSize = define(named("block").withLongForm("block-size").takingArgument("SIZE"));
        Option<Boolean> debug = define(named("debug").withShortForm("x"));

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

    private void parse(String... args)
            throws ParsingException {
        nonOptions.addAll(parser.parse(detected, new Options(options), listOf(args)));
    }
}