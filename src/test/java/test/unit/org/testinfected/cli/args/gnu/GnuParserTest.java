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

package test.unit.org.testinfected.cli.args.gnu;

import org.junit.Test;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.UnrecognizedOptionException;
import org.testinfected.cli.gnu.GnuParser;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.OptionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.testinfected.cli.args.OptionBuilder.optionNamed;

public class GnuParserTest
{
    GnuParser parser = new GnuParser();
    Collection<Option> options = new ArrayList<Option>();

    List<String> positionalArguments;

    @Test public void
    doesNotConsumeAnyArgumentIfNoOptionIsDefined() throws ParsingException {
        parse("1", "2", "3");
        assertEquals(Arrays.asList("1", "2", "3"), positionalArguments);
    }

    @Test public void
    detectsOptionsByTheirShortForm() throws ParsingException {
        Option debug = define(optionNamed("debug").withShortForm("x"));
        Option verbose = define(optionNamed("verbose").withShortForm("v"));

        parse("-x");
        assertEquals(true, debug.getValue());
        assertNull(verbose.getValue());
    }

    @Test public void
    optionsCanDeclareParameters() throws ParsingException {
        Option blockSize = define(optionNamed("block size").withShortForm("b").withRequiredArg("SIZE"));

        parse("-b", "1024");
        assertEquals("1024", blockSize.getValue());
    }

    @Test public void
    detectsOptionsByTheirLongForm() throws ParsingException {
        Option blockSize = define(optionNamed("block size").withLongForm("block-size").withRequiredArg("SIZE"));

        parse("--block-size", "1024");
        assertEquals("1024", blockSize.getValue());
    }

    @Test public void
    consumesOptionsAndReturnsRemainingArguments() throws ParsingException {
        define(optionNamed("raw").withLongForm("raw"));

        parse("--raw", "input", "output");
        assertEquals(Arrays.asList("input", "output"), positionalArguments);
    }

    @Test public void
    supportsMultipleOptionsAndParameters() throws ParsingException {
        Option human = define(optionNamed("human").withShortForm("h").withDescription("Human readable format"));
        Option blockSize = define(optionNamed("block size").withLongForm("block-size").withRequiredArg("SIZE"));
        Option debug = define(optionNamed("debug").withShortForm("x"));

        parse("-h", "--block-size", "1024", "-x", "input", "output");
        assertNotNull(human.getValue());
        assertNotNull(blockSize.getValue());
        assertNotNull(debug.getValue());
        assertEquals(Arrays.asList("input", "output"), positionalArguments);
    }

    @Test public void
    complainsOfInvalidOptions() throws ParsingException {
        try {
            parse("-x");
            fail();
        }
        catch (UnrecognizedOptionException expected) {
            assertEquals("-x", expected.getTrigger());
        }
    }

    private Option define(OptionBuilder builder) {
        Option option = builder.make();
        options.add(option);
        return option;
    }

    private void parse(String... input)
            throws ParsingException {
        positionalArguments = parser.parse(options, input);
    }
}