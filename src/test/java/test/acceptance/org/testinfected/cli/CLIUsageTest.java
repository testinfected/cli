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

package test.acceptance.org.testinfected.cli;

import org.junit.Test;
import org.testinfected.cli.CLI;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.ArgumentMissingException;
import org.testinfected.cli.args.InvalidArgumentException;
import org.testinfected.cli.args.MissingOperandException;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.UnrecognizedOptionException;
import org.testinfected.cli.coercion.TypeCoercer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Locale;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CLIUsageTest {
    String NL = System.getProperty("line.separator");

    CLI cli;

    @Test public void
    usingPositionalArguments() throws Exception {
        cli = new CLI() {{
            define(operand("input").describedAs("The input file"));
            define(operand("output").describedAs("The output file"));
        }};
        cli.parse("input", "output");

        assertEquals("input", cli.get("input"));
        assertEquals("output", cli.get("output"));
    }

    @Test public void
    retrievingExtraArguments() throws ParsingException {
        cli = new CLI() {{
            define(operand("input"));
        }};
        String[] args = cli.parse("input", "output", "encoding");
        assertEquals("[output, encoding]", Arrays.toString(args));
    }

    @Test public void
    specifyingTheTypeOfAPositionalArgument() throws ParsingException {
        cli = new CLI() {{
            define(operand("input").ofType(File.class));
        }};
        cli.parse("/path/to/input");

        File inputFile = cli.get("input");
        assertEquals("/path/to/input", inputFile.getAbsolutePath());
    }

    @Test public void
    usingSimpleOptionSwitches() throws ParsingException {
        cli = new CLI() {{
            define(option("debug").withShortForm("x").describedAs("Turns debugging on"));
        }};
        cli.parse("-x");
        assertTrue(cli.has("debug"));
    }

    @Test public void
    definingAnOptionThatExpectsAnArgument() throws Exception {
        cli = new CLI() {{
            define(option("block").withShortForm("b").takingArgument("SIZE"));
        }};
        cli.parse("-b", "1024");

        assertTrue(cli.has("block"));
        assertEquals("1024", cli.get("block"));
    }

    @Test public void
    definingAnOptionWithAShortFormAndALongForm() throws Exception {
        cli = new CLI() {{
            define(option("debug").withShortForm("x").withLongForm("debug"));
        }};
        cli.parse("--debug");
        assertTrue(cli.has("debug"));
    }

    @Test public void
    specifyingTheTypeOfAnOptionArgument() throws Exception {
        cli = new CLI() {{
            define(option("block").withShortForm("b").takingArgument("SIZE").ofType(int.class));
        }};
        cli.parse("-b", "1024");
        int blockSize = cli.<Integer>get("block");
        assertEquals(1024, blockSize);
    }

    @Test public void
    specifyingADefaultValueForAnOption() throws Exception {
        cli = new CLI() {{
            define(option("block").withShortForm("b").takingArgument("SIZE").ofType(int.class).defaultingTo(1024));
        }};
        cli.parse();
        assertEquals(1024, cli.get("block"));
    }

    @Test public void
    specifyingAnOptionInLiteralForm() throws Exception {
        cli = new CLI() {{
            define(option("block", "-b", "--block-size SIZE", "Specifies block size"));
        }};
        cli.parse("--block-size", "1024");
        assertTrue(cli.has("block"));
        assertEquals("1024", cli.get("block"));
    }

    @Test public void
    usingBuiltInCoercers() throws Exception {
        cli = new CLI() {{
            define(option("class").withShortForm("c").takingArgument("CLASSNAME").ofType(Class.class));
            define(operand("file").ofType(File.class));
        }};
        cli.parse("-c", "java.lang.String", "/path/to/file");
        assertEquals(String.class, cli.get("class"));
        assertEquals(new File("/path/to/file"), cli.get("file"));
    }

    @Test public void
    aMoreComplexExampleThatUsesAMixOfDifferentArguments() throws Exception {
        cli = new CLI() {{
            define(option("human").withShortForm("h").describedAs("Human readable format"));
            define(option("block").withLongForm("block-size").takingArgument("SIZE").ofType(int.class));
            define(option("debug").withShortForm("x"));
            define(operand("input").as("INFILE").describedAs("The input file"));
            define(operand("output", "OUTFILE", "The output file"));
        }};

        String[] args = cli.parse("-h", "--block-size", "1024", "-x", "input", "output", "extra", "more extra");
        assertEquals(5, cli.argumentCount());
        assertTrue(cli.has("human"));
        assertEquals(1024, cli.get("block"));
        assertTrue(cli.has("debug"));
        assertEquals("input", cli.get("input"));
        assertEquals("output", cli.get("output"));
        assertEquals("[extra, more extra]", Arrays.toString(args));
    }

    @Test public void
    usingACustomOptionType() throws Exception {
        cli = new CLI() {{
            coerceType(BigDecimal.class).using(new BigDecimalCoercer());
            define(option("size", "--size VALUE").ofType(BigDecimal.class));
        }};
        cli.parse("--size", "1000.00");
        assertEquals(new BigDecimal("1000.00"), cli.get("size"));
    }

    @Test public void
    executingACallbackWhenAnOptionIsDetected() throws Exception {
        final CaptureLocale captureLocale = new CaptureLocale();
        cli = new CLI() {{
            define(option("locale", "-l LOCALE").ofType(Locale.class).whenPresent(captureLocale));
        }};

        cli.parse("-l", "FR");
        assertEquals(Locale.FRENCH, captureLocale.locale);
    }

    @Test public void
    displayingHelp() throws Exception {
        cli = new CLI() {{
            name("program"); version("1.0");
            description("Does some cool things.");
            define(option("raw", "--raw", "Specifies raw output format"));
            define(option("block", "-b", "--block-size SIZE", "Specifies block size"));
            define(option("debug", "-x", "Turn debugging on"));
            define(operand("in", "INPUT", "The source file"));
            define(operand("out", "OUTPUT", "The destination file"));
            ending("use --help to show this help message");
        }};
        assertEquals(
                "program version 1.0" + NL +
                NL +
                "Does some cool things." + NL +
                NL +
                "Usage: program [--raw] [-b SIZE] [-x] INPUT OUTPUT" + NL +
                NL +
                "Arguments:" + NL +
                "INPUT                          The source file" + NL +
                "OUTPUT                         The destination file" + NL +
                NL +
                "Options:" + NL +
                "    --raw                      Specifies raw output format" + NL +
                "-b, --block-size SIZE          Specifies block size" + NL +
                "-x                             Turn debugging on" + NL +
                NL +
                "use --help to show this help message" + NL,
                help(cli));
    }

    @Test public void
    aShortOrLongFormMustBeSuppliedForOptionToBeValid() {
        try {
            new CLI() {{
                define(option("noform"));
                fail("Expected exception " + IllegalArgumentException.class.getName());
            }};
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString("'noform'"));
        }
    }

    @Test public void
    detectingAnUnrecognizedOption() throws Exception {
        cli = new CLI();
        try {
            cli.parse("-whatever");
            fail("Expected exception " + UnrecognizedOptionException.class.getName());
        }
        catch (UnrecognizedOptionException expected) {
            assertEquals("-whatever", expected.getTrigger());
            assertThat(expected.getMessage(), containsString("whatever"));
        }
    }

    @Test public void
    passingAnInvalidArgumentToAnOption() throws Exception {
        cli = new CLI() {{
            define(option("block", "-b SIZE").ofType(int.class));
        }};
        try {
            cli.parse("-b", "LITERAL");
            fail("Expected exception " + InvalidArgumentException.class.getName());
        }
        catch (InvalidArgumentException expected) {
            assertEquals("block", expected.getUnsatisfiedArgument());
            assertEquals("LITERAL", expected.getOffendingValue());
            assertThat(expected.getMessage(), containsString("block"));
            assertThat(expected.getMessage(), containsString("LITERAL"));
        }
    }

    @Test public void
    omittingARequiredOptionArgument() throws Exception {
        cli = new CLI() {{
            define(option("block", "-b SIZE").ofType(int.class));
        }};
        try {
            cli.parse("-b");
            fail("Expected exception " + ArgumentMissingException.class.getName());
        }
        catch (ArgumentMissingException expected) {
            assertEquals("block", expected.getUnsatisfiedOption());
            assertThat(expected.getMessage(), containsString("block"));
        }
    }

    @Test public void
    omittingARequiredPositionalArgument() throws Exception {
        cli = new CLI() {{
            define(operand("input"));
            define(operand("output"));
        }};
        try {
            cli.parse("input");
            fail("Expected exception " + MissingOperandException.class.getName());
        }
        catch (MissingOperandException expected) {
            assertEquals("output", expected.getMissingOperand());
            assertThat(expected.getMessage(), containsString("output"));
        }
    }

    @Test public void
    usingAnInvalidPositionalArgument() throws Exception {
        cli = new CLI() {{
            define(operand("size").ofType(int.class));
        }};
        try {
            cli.parse("LITERAL");
            fail("Expected exception " + InvalidArgumentException.class.getName());
        }
        catch (InvalidArgumentException expected) {
            assertEquals("size", expected.getUnsatisfiedArgument());
            assertEquals("LITERAL", expected.getOffendingValue());
            assertThat(expected.getMessage(), containsString("size"));
            assertThat(expected.getMessage(), containsString("LITERAL"));
        }
    }

    private String help(CLI cli) throws IOException {
        StringBuilder output = new StringBuilder();
        cli.printHelp(output);
        return output.toString();
    }

    public static class CaptureLocale implements Option.Action {
        public Locale locale = Locale.ENGLISH;

        public void call(Option option) {
            locale = (Locale) option.getValue();
        }
    }

    public static class BigDecimalCoercer implements TypeCoercer<BigDecimal> {
        public BigDecimal convert(String value) throws Exception {
            return new BigDecimal(value);
        }
    }
}

