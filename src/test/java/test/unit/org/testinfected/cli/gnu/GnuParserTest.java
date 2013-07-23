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

package test.unit.org.testinfected.cli.gnu;

import org.junit.Test;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Args;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.Options;
import org.testinfected.cli.args.UnrecognizedOptionException;
import org.testinfected.cli.gnu.GnuParser;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testinfected.cli.args.Input.listOf;
import static org.testinfected.cli.args.Option.named;

public class GnuParserTest
{
    GnuParser parser = new GnuParser();

    Collection<Option> options = new ArrayList<Option>();
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
        Option debug = define(named("debug").withShortForm("x"));
        Option verbose = define(named("verbose").withShortForm("v"));

        parse("-x");
        assertEquals(true, debug.getValue(detected));
        assertNull(verbose.getValue(detected));
    }

    @Test public void
    detectsDefinedOptionsByTheirLongForm() throws ParsingException {
        Option raw = define(named("raw").withLongForm("raw"));

        parse("--raw");
        assertEquals(TRUE, raw.getValue(detected));
    }

    @Test public void
    detectsDefinedOptionsParameters() throws ParsingException {
        Option blockSize = define(named("block").withShortForm("b").withLongForm("block-size").takingArgument("SIZE"));
        parse("-b", "1024");
        assertEquals("1024", blockSize.getValue(detected));
    }

    @Test public void
    returnNonOptionArguments() throws ParsingException {
        define(named("raw").withLongForm("raw"));

        parse("--raw", "input", "output");
        assertEquals(asList("input", "output"), nonOptions);
    }

    @Test public void
    supportsMultipleOptionsWithParameters() throws ParsingException {
        Option human = define(named("human").withShortForm("h").describedAs("Human readable format"));
        Option blockSize = define(named("block").withLongForm("block-size").takingArgument("SIZE"));
        Option debug = define(named("debug").withShortForm("x"));

        parse("-h", "--block-size", "1024", "-x", "input", "output");
        assertEquals(TRUE, human.getValue(detected));
        assertEquals("1024", blockSize.getValue(detected));
        assertEquals(TRUE, debug.getValue(detected));
        assertEquals(asList("input", "output"), nonOptions);
    }

    @Test public void
    complainsOfUnrecognizedOptions() throws ParsingException {
        try {
            parse("-x");
            fail();
        }
        catch (UnrecognizedOptionException expected) {
            assertEquals("-x", expected.getTrigger());
        }
    }

    private Option define(Option option) {
        options.add(option);
        return option;
    }

    private void parse(String... args)
            throws ParsingException {
        nonOptions.addAll(parser.parse(detected, new Options(options), listOf(args)));
    }
}