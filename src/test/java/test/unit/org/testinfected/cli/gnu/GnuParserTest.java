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
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.OptionSpec;
import org.testinfected.cli.args.UnrecognizedOptionException;
import org.testinfected.cli.gnu.GnuParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.Boolean.TRUE;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.testinfected.cli.args.OptionSpec.option;

public class GnuParserTest
{
    GnuParser parser = new GnuParser();

    Collection<Option> options = new ArrayList<Option>();
    List<String> operands = new ArrayList<String>();

    @Test public void
    doesNotConsumeAnyArgumentIfNoOptionIsDefined() throws ParsingException {
        parse("1", "2", "3");
        assertEquals(Arrays.asList("1", "2", "3"), operands);
    }

    @Test public void
    detectsOptionsByTheirShortForm() throws ParsingException {
        Option debug = define(option("debug").withShortForm("x"));
        Option verbose = define(option("verbose").withShortForm("v"));

        parse("-x");
        assertEquals(true, debug.getValue());
        assertNull(verbose.getValue());
    }

    @Test public void
    detectsOptionsByTheirLongForm() throws ParsingException {
        Option raw = define(option("raw").withLongForm("raw"));

        parse("--raw");
        assertEquals(TRUE, raw.getValue());
    }

    @Test public void
    detectsOptionsParameters() throws ParsingException {
        Option blockSize = define(option("block").withShortForm("b").withLongForm("block-size").takingArgument("SIZE"));
        parse("-b", "1024");
        assertEquals("1024", blockSize.getValue());
    }

    @Test public void
    consumesOptionsAndReturnsRemainingArguments() throws ParsingException {
        define(option("raw").withLongForm("raw"));

        parse("--raw", "input", "output");
        assertEquals(Arrays.asList("input", "output"), operands);
    }

    @Test public void
    supportsMultipleOptionsAndParameters() throws ParsingException {
        Option human = define(option("human").withShortForm("h").describedAs("Human readable format"));
        Option blockSize = define(option("block").withLongForm("block-size").takingArgument("SIZE"));
        Option debug = define(option("debug").withShortForm("x"));

        parse("-h", "--block-size", "1024", "-x", "input", "output");
        assertEquals(TRUE, human.getValue());
        assertEquals("1024", blockSize.getValue());
        assertEquals(TRUE, debug.getValue());
        assertEquals(Arrays.asList("input", "output"), operands);
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

    private Option define(OptionSpec spec) {
        Option option = spec.make();
        options.add(option);
        return option;
    }

    private void parse(String... input)
            throws ParsingException {
        operands.addAll(parser.parse(options, input));
    }
}