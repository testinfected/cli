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

package test.acceptance.com.pyxis.cli;

import com.pyxis.cli.CLI;
import com.pyxis.cli.ParsingException;
import com.pyxis.cli.args.UnrecognizedOptionException;
import com.pyxis.cli.coercion.TypeCoercer;
import com.pyxis.cli.option.ArgumentMissingException;
import com.pyxis.cli.option.InvalidArgumentException;
import com.pyxis.cli.option.Option;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

public class CLIUsageTest {
    CLI cli;

    @Test public void
    usingSimpleSwitches() throws ParsingException {
        cli = new CLI() {{
            define(option("debug").withShortForm("x").withDescription("Turns debugging on"));
        }};
        cli.parse("-x");
        assertTrue(cli.hasOption("debug"));
    }

    @Test public void
    definingAnOptionThatExpectsAnArgument() throws Exception {
        cli = new CLI() {{
            define(option("block size").withShortForm("b").wantsArgument("SIZE"));
        }};
        cli.parse("-b", "1024");

        assertTrue(cli.hasOption("block size"));
        assertEquals("1024", cli.getOption("block size"));
    }

    @Test public void
    definingAnOptionWithAShortFormAndALongForm() throws Exception {
        cli = new CLI() {{
            define(option("debug").withShortForm("x").withLongForm("debug"));
        }};
        cli.parse("--debug");
        assertTrue(cli.hasOption("debug"));
    }

    @Test public void
    usingCommandLineParameters() throws Exception {
        cli = new CLI() {{
            define(option("debug").withLongForm("debug"));
        }};
        cli.parse("--debug", "input", "output");

        assertEquals(2, cli.getOperandCount());
        assertEquals("input", cli.getParameter(0));
        assertEquals("output", cli.getParameter(1));
    }

    @Test public void
    aMoreComplexExampleThatUsesAMixOfDifferentOptions() throws Exception {
        cli = new CLI() {{
            define(option("human").withShortForm("h").withDescription("Human readable format"));
            define(option("block size").withLongForm("block-size").wantsArgument("SIZE"));
            define(option("debug").withShortForm("x"));
        }};

        cli.parse("-h", "--block-size", "1024", "-x", "input", "output");
        assertEquals(3, cli.getOptions().size());
        assertTrue(cli.hasOption("human"));
        assertEquals("1024", cli.getOption("block size"));
        assertTrue(cli.hasOption("debug"));
        assertEquals("[input, output]", Arrays.toString(cli.getParameters()));
    }

    @Test public void
    specifyingTheTypeOfAnOptionArgument() throws Exception {
        cli = new CLI() {{
            define(option("block size").withShortForm("b").wantsArgument("SIZE").asType(int.class));
        }};
        cli.parse("-b", "1024");
        assertEquals(1024, cli.getOption("block size"));
    }

    @Test public void
    specifyingADefaultValueForAnOption() throws Exception {
        cli = new CLI() {{
            define(option("block size").withShortForm("b").wantsArgument("SIZE").asType(int.class).defaultingTo(1024));
        }};
        cli.parse();
        assertEquals(1024, cli.getOption("block size"));
    }

    @Test public void
    specifyingAnOptionInLiteralForm() throws Exception {
        cli = new CLI() {{
            define(option("block size", "-b", "--block-size SIZE", "Specifies block size"));
        }};
        cli.parse("--block-size", "1024");
        assertTrue(cli.hasOption("block size"));
        assertEquals("1024", cli.getOption("block size"));
    }

    @Test public void
    usingBuiltInCoercers() throws Exception {
        cli = new CLI() {{
            define(option("file").withShortForm("f").wantsArgument("PATH").asType(File.class));
            define(option("class").withShortForm("c").wantsArgument("CLASS NAME").asType(Class.class));
        }};
        cli.parse("-f", "/path/to/file", "-c", "java.lang.String");
        assertEquals(new File("/path/to/file"), cli.getOption("file"));
        assertEquals(String.class, cli.getOption("class"));
    }

    @Test public void
    usingACustomOptionType() throws Exception {
        cli = new CLI() {{
            coerceType(BigDecimal.class).using(new BigDecimalCoercer());
            define(option("size", "--size VALUE").asType(BigDecimal.class));
        }};
        cli.parse("--size", "1000.00");
        assertEquals(new BigDecimal("1000.00"), cli.getOption("size"));
    }

    @Test public void
    executingACallbackWhenAnOptionIsDetected() throws Exception {
        final CaptureLocale captureLocale = new CaptureLocale();
        cli = new CLI() {{
            define(option("localeOption", "-l LOCALE").asType(Locale.class).whenPresent(captureLocale));
        }};

        cli.parse("-l", "FR");
        assertEquals(Locale.FRENCH, captureLocale.localeOption);
    }

    @Test public void
    displayingHelp() throws Exception {
        cli = new CLI() {{
            withBanner("My cool program v1.0");

            define(option("raw").withLongForm("raw").withDescription("Specifies raw ouput format"));
            define(option("block size").withShortForm("b").withLongForm("block-size").wantsArgument("SIZE").withDescription("Specifies block size"));
            define(option("debug").withShortForm("x").withDescription("Turn debugging on"));
        }};
        assertEquals(
                "Usage: My cool program v1.0\n" +
                        "\n" +
                        "Options:\n" +
                        "    --raw                      Specifies raw ouput format\n" +
                        "-b, --block-size SIZE          Specifies block size\n" +
                        "-x                             Turn debugging on",
                usage(cli));
    }

    // What would be more appropriate than an IllegalArgumentException ?
    @Test(expected = IllegalArgumentException.class)
    public void aShortOrLongFormMustBeSuppliedForOptionToBeValid() {
        cli = new CLI() {{
            define(option("missing a form"));            
        }};
    }

    @Test public void
    detectingAnUnrecognizedOption() throws Exception {
        cli = new CLI();
        try {
            cli.parse("-whatever");
            fail();
        }
        catch (UnrecognizedOptionException expected) {
            assertEquals("-whatever", expected.getTrigger());
            assertThat(expected.getMessage(), containsString("unrecognized"));

        }
    }

    @Test public void
    passingAnInvalidArgumentToAnOption() throws Exception {
        cli = new CLI() {{
            define(option("block size", "-b SIZE").asType(int.class));
        }};
        try {
            cli.parse("-b", "LITERAL");
            fail();
        }
        catch (InvalidArgumentException expected) {
            assertEquals("block size", expected.getUnsatisfiedOption().getName());
            assertEquals("LITERAL", expected.getParsedValue());
            assertThat(expected.getMessage(), containsString("invalid"));
        }
    }

    @Test public void
    omittingARequiredOptionArgument() throws Exception {
        cli = new CLI() {{
            define(option("block size", "-b SIZE").asType(int.class));
        }};
        try {
            cli.parse("-b");
            fail();
        }
        catch (ArgumentMissingException expected) {
            assertEquals("block size", expected.getUnsatisfiedOption().getName());
            assertThat(expected.getMessage(), containsString("expects"));
        }
    }

    private String usage(CLI cli) throws IOException {
        StringBuilder sb = new StringBuilder();
        cli.writeUsageTo(sb);
        return sb.toString();
    }

    public static class CaptureLocale implements Option.Stub {

        public Locale localeOption = Locale.ENGLISH;

        public void call(Option option) {
            localeOption = (Locale) option.getValue();
        }

    }

    public static class BigDecimalCoercer implements TypeCoercer<BigDecimal> {

        public BigDecimal convert(String value) throws Exception {
            return new BigDecimal(value);
        }

    }
}

