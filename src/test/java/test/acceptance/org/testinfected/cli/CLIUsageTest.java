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

import org.junit.Ignore;
import org.junit.Test;
import org.testinfected.cli.CLI;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Args;
import org.testinfected.cli.args.ArgumentMissingException;
import org.testinfected.cli.args.InvalidArgumentException;
import org.testinfected.cli.args.MissingOperandException;
import org.testinfected.cli.args.OperandSpec;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.OptionSpec;
import org.testinfected.cli.args.UnrecognizedOptionException;
import org.testinfected.cli.coercion.TypeCoercer;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Locale;

import static java.util.Arrays.asList;
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
            operand("input").describedAs("The input file");
            operand("output").describedAs("The output file");
        }};
        cli.parse("input", "output");

        assertEquals("input", cli.get("input"));
        assertEquals("output", cli.get("output"));
    }

    @Test public void
    retrievingExtraArguments() throws ParsingException {
        cli = new CLI() {{
            operand("input");
        }};
        Args args = cli.parse("input", "output", "encoding");
        assertEquals(asList("output", "encoding"), args.others());
    }

    @Test public void
    specifyingTheTypeOfAPositionalArgument() throws ParsingException {
        cli = new CLI() {{
            operand("input").ofType(File.class);
        }};
        cli.parse("/path/to/input");

        File inputFile = cli.get("input");
        assertEquals("/path/to/input", inputFile.getAbsolutePath());
    }

    @Test public void
    usingSimpleOptionSwitches() throws ParsingException {
        cli = new CLI() {{
            option("debug").withShortForm("x").describedAs("Turns debugging on");
        }};
        cli.parse("-x");
        assertTrue(cli.has("debug"));
    }

    @Test public void
    definingAnOptionThatExpectsAnArgument() throws Exception {
        cli = new CLI() {{
            option("block").withShortForm("b").takingArgument("SIZE");
        }};
        cli.parse("-b", "1024");

        assertTrue(cli.has("block"));
        assertEquals("1024", cli.get("block"));
    }

    @Test public void
    definingAnOptionWithAShortFormAndALongForm() throws Exception {
        cli = new CLI() {{
            option("debug").withShortForm("x").withLongForm("debug");
        }};
        cli.parse("--debug");
        assertTrue(cli.has("debug"));
    }

    @Test public void
    specifyingTheTypeOfAnOptionArgument() throws Exception {
        cli = new CLI() {{
            option("block").withShortForm("b").takingArgument("SIZE").ofType(int.class);
        }};
        cli.parse("-b", "1024");
        int blockSize = cli.<Integer>get("block");
        assertEquals(1024, blockSize);
    }

    @Test public void
    specifyingADefaultValueForAnOption() throws Exception {
        cli = new CLI() {{
            option("block").withShortForm("b").takingArgument("SIZE").ofType(int.class).defaultingTo(1024);
        }};
        cli.parse();
        assertEquals(1024, cli.get("block"));
    }

    @Test public void
    retrievingArgumentsInATypeSafeWay() throws ParsingException {
        cli = new CLI();
        OptionSpec<Boolean> verbose = cli.option("verbose").withShortForm("v").ofType(Boolean.class);
        OptionSpec<Integer> size = cli.option("size").withLongForm("block-size").takingArgument("SIZE").ofType(int.class).defaultingTo(1024);
        OperandSpec<File> input = cli.operand("input").ofType(File.class);
        Args args = cli.parse("-v", "--block-size", "2048", "/path/to/input");

        File inputFile = input.get(args);
        assertEquals("/path/to/input", inputFile.getAbsolutePath());
        boolean verboseFlag = verbose.get(args);
        assertEquals(true, verboseFlag);
        int blockSize = size.get(args);
        assertEquals(2048, blockSize);
    }

    @Test public void
    specifyingAnOptionInLiteralForm() throws Exception {
        cli = new CLI() {{
            option("block", "-b", "--block-size SIZE", "Specifies block size");
        }};
        cli.parse("--block-size", "1024");
        assertTrue(cli.has("block"));
        assertEquals("1024", cli.get("block"));
    }

    @Test public void
    usingBuiltInCoercers() throws Exception {
        cli = new CLI() {{
            option("class").withShortForm("c").takingArgument("CLASSNAME").ofType(Class.class);
            operand("file").ofType(File.class);
        }};
        cli.parse("-c", "java.lang.String", "/path/to/file");
        assertEquals(String.class, cli.get("class"));
        assertEquals(new File("/path/to/file"), cli.get("file"));
    }

    @Test public void
    aMoreComplexExampleThatUsesAMixOfDifferentArguments() throws Exception {
        cli = new CLI() {{
            option("human").withShortForm("h").describedAs("Human readable format");
            option("block").withLongForm("block-size").takingArgument("SIZE").ofType(int.class);
            option("debug").withShortForm("x");
            operand("input").as("INFILE").describedAs("The input file");
            operand("output", "OUTFILE", "The output file");
        }};

        cli.parse("-h", "--block-size", "1024", "-x", "input", "output", "extra", "more extra");
        assertEquals(5, cli.options().size());
        assertTrue(cli.has("human"));
        assertEquals(1024, cli.get("block"));
        assertTrue(cli.has("debug"));
        assertEquals("input", cli.get("input"));
        assertEquals("output", cli.get("output"));
        assertEquals(asList("extra", "more extra"), cli.others());
    }

    @Test public void
    usingACustomOptionType() throws Exception {
        cli = new CLI() {{
            coerceType(BigDecimal.class).using(new BigDecimalCoercer());
            option("size", "--size VALUE").ofType(BigDecimal.class);
        }};
        cli.parse("--size", "1000.00");
        assertEquals(new BigDecimal("1000.00"), cli.get("size"));
    }

    @Test public void
    executingACallbackWhenAnOptionIsDetected() throws Exception {
        final CaptureLocale captureLocale = new CaptureLocale();
        cli = new CLI() {{
            option("locale", "-l LOCALE").ofType(Locale.class).whenPresent(captureLocale);
        }};

        cli.parse("-l", "FR");
        assertEquals(Locale.FRENCH, captureLocale.locale);
    }

    @Test public void
    displayingHelp() throws Exception {
        cli = new CLI() {{
            name("program"); version("1.0");
            description("Does some cool things.");
            option("raw", "--raw", "Specifies raw output format");
            option("block", "-b", "--block-size SIZE", "Specifies block size");
            option("debug", "-x", "Turn debugging on");
            operand("in", "INPUT", "The source file");
            operand("out", "OUTPUT", "The destination file");
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

    @Ignore("make this by design")
    @Test public void
    aShortOrLongFormMustBeSuppliedForOptionToBeValid() {
        try {
            new CLI() {{
                option("noform");
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
            assertEquals("-whatever", expected.getOption());
            assertThat(expected.getMessage(), containsString("whatever"));
        }
    }

    @Test public void
    passingAnInvalidArgumentToAnOption() throws Exception {
        cli = new CLI() {{
            option("block", "-b SIZE").ofType(int.class);
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
            option("block", "-b SIZE").ofType(int.class);
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
            operand("input");
            operand("output");
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
            operand("size").ofType(int.class);
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

    public static class CaptureLocale implements Option.Action<Locale> {
        public Locale locale = Locale.ENGLISH;

        public void call(Args detected, Option<Locale> option) {
            locale = option.get(detected);
        }
    }

    public static class BigDecimalCoercer implements TypeCoercer<BigDecimal> {
        public BigDecimal convert(String value) throws Exception {
            return new BigDecimal(value);
        }
    }
}

