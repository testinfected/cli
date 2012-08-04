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

package test.unit.com.pyxis.cli.args.gnu;

import com.pyxis.cli.ParsingException;
import com.pyxis.cli.args.UnrecognizedOptionException;
import com.pyxis.cli.args.gnu.GnuParser;
import com.pyxis.cli.option.Option;
import com.pyxis.cli.option.OptionBuilder;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static com.pyxis.cli.option.OptionBuilder.optionNamed;
import static org.junit.Assert.*;

public class GnuParserTest
{
    GnuParser parser = new GnuParser();
    Collection<Option> options = new ArrayList<Option>();

    String[] actualParameters;

    @Test public void
    doesNotConsumeAnyArgumentIfNoOptionIsDefined() throws ParsingException {
        parse("1", "2", "3");
        assertEquals("[1, 2, 3]", parameterString());
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
        Option blockSize = define(optionNamed("block size").withShortForm("b").wantsArgument("SIZE"));

        parse("-b", "1024");
        assertEquals("1024", blockSize.getValue());
    }

    @Test public void
    detectsOptionsByTheirLongForm() throws ParsingException {
        Option blockSize = define(optionNamed("block size").withLongForm("block-size").wantsArgument("SIZE"));

        parse("--block-size", "1024");
        assertEquals("1024", blockSize.getValue());
    }

    @Test public void
    consumesOptionsAndReturnsExtraParameters() throws ParsingException {
        define(optionNamed("raw").withLongForm("raw"));

        parse("--raw", "input", "output");
        assertEquals("[input, output]", parameterString());
    }

    @Test public void
    supportsMultipleOptionsAndParameters() throws ParsingException {
        Option human = define(optionNamed("human").withShortForm("h").withDescription("Human readable format"));
        Option blockSize = define(optionNamed("block size").withLongForm("block-size").wantsArgument("SIZE"));
        Option debug = define(optionNamed("debug").withShortForm("x"));

        parse("-h", "--block-size", "1024", "-x", "input", "output");
        assertNotNull(human.getValue());
        assertNotNull(blockSize.getValue());
        assertNotNull(debug.getValue());
        assertEquals("[input, output]", parameterString());
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
        actualParameters = parser.parse(options, input);
    }

    private String parameterString() {
        return Arrays.toString(actualParameters);
    }
}