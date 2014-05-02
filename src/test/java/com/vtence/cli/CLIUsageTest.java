package com.vtence.cli;

import org.junit.Test;
import com.vtence.cli.args.Args;
import com.vtence.cli.args.ArgumentMissingException;
import com.vtence.cli.args.ArgumentSpec;
import com.vtence.cli.args.InvalidArgumentException;
import com.vtence.cli.args.MissingOperandException;
import com.vtence.cli.args.OperandSpec;
import com.vtence.cli.args.Option;
import com.vtence.cli.args.UnrecognizedOptionException;
import com.vtence.cli.coercion.TypeCoercer;

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
            flag("-x").describedAs("Turns debugging on");
        }};
        cli.parse("-x");
        assertTrue(cli.has("-x"));
    }

    @Test public void
    definingAnOptionThatExpectsAnArgument() throws Exception {
        cli = new CLI() {{
            option("-b").takingArgument("SIZE");
        }};
        cli.parse("-b", "1024");

        assertTrue(cli.has("-b"));
        assertEquals("1024", cli.get("-b"));
    }

    @Test public void
    definingAnOptionWithAnAliasForm() throws Exception {
        cli = new CLI() {{
            option("-x").alias("--debug");
        }};
        cli.parse("--debug");
        assertTrue(cli.has("-x"));
        assertTrue(cli.has("--debug"));
    }

    @Test public void
    specifyingTheTypeOfAnOptionArgument() throws Exception {
        cli = new CLI() {{
            option("-b").takingArgument("SIZE").ofType(int.class);
        }};
        cli.parse("-b", "1024");
        int blockSize = cli.<Integer>get("-b");
        assertEquals(1024, blockSize);
    }

    @Test public void
    specifyingADefaultValueForAnOption() throws Exception {
        cli = new CLI() {{
            option("-b").takingArgument("SIZE").ofType(int.class).defaultingTo(1024);
        }};
        cli.parse();
        assertEquals(1024, cli.get("-b"));
    }

    @Test public void
    retrievingArgumentsInATypeSafeWay() throws ParsingException {
        cli = new CLI();
        ArgumentSpec<Boolean> verbose = cli.option("-v").ofType(Boolean.class);
        ArgumentSpec<Integer> size = cli.option("--block-size").takingArgument("SIZE").ofType(int.class).defaultingTo(1024);
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
            option("-h", "--host HOSTNAME", "Hostname to bind to");
        }};
        cli.parse("--host", "0.0.0.0");
        assertTrue(cli.has("-h"));
        assertTrue(cli.has("--host"));
        assertEquals("0.0.0.0", cli.get("-h"));
    }

    @Test public void
    usingBuiltInCoercers() throws Exception {
        cli = new CLI() {{
            option("-c").takingArgument("CLASSNAME").ofType(Class.class);
            operand("file").ofType(File.class);
        }};
        cli.parse("-c", "java.lang.String", "/path/to/file");
        assertEquals(String.class, cli.get("-c"));
        assertEquals(new File("/path/to/file"), cli.get("file"));
    }

    @Test public void
    aMoreComplexExampleThatUsesAMixOfDifferentArguments() throws Exception {
        cli = new CLI() {{
            flag("-h").describedAs("Human readable format");
            option("-b").alias("--block-size").takingArgument("SIZE").ofType(int.class);
            flag("-x");
            operand("input").as("INFILE").describedAs("The input file");
            operand("output", "OUTFILE", "The output file");
        }};

        cli.parse("-h", "--block-size", "1024", "-x", "input", "output", "extra", "more extra");
        assertEquals(6, cli.options().size());
        assertTrue(cli.has("-h"));
        assertEquals(1024, cli.get("--block-size"));
        assertTrue(cli.has("-x"));
        assertEquals("input", cli.get("input"));
        assertEquals("output", cli.get("output"));
        assertEquals(asList("extra", "more extra"), cli.others());
    }

    @Test public void
    usingACustomOptionType() throws Exception {
        cli = new CLI() {{
            coerceType(BigDecimal.class).using(new BigDecimalCoercer());
            option("--size VALUE").ofType(BigDecimal.class);
        }};
        cli.parse("--size", "1000.00");
        assertEquals(new BigDecimal("1000.00"), cli.get("--size"));
    }

    @Test public void
    executingACallbackWhenAnOptionIsDetected() throws Exception {
        final CaptureLocale captureLocale = new CaptureLocale();
        cli = new CLI() {{
            option("-l LOCALE").ofType(Locale.class).whenPresent(captureLocale);
        }};

        cli.parse("-l", "FR");
        assertEquals(Locale.FRENCH, captureLocale.locale);
    }

    @Test public void
    displayingHelp() throws Exception {
        cli = new CLI() {{
            name("program"); version("1.0");
            description("Does some cool things.");
            flag("--raw", "Specifies raw output format");
            option("-b", "--block-size SIZE", "Specifies block size");
            flag("-x", "Turn debugging on");
            operand("in", "INPUT", "The source file");
            operand("out", "OUTPUT", "The destination file");
            epilog("use --help to show this help message");
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
    detectingAnUnrecognizedOption() throws Exception {
        cli = new CLI();
        try {
            cli.parse("--whatever");
            fail("Expected exception " + UnrecognizedOptionException.class.getName());
        } catch (UnrecognizedOptionException expected) {
            assertEquals("--whatever", expected.getOption());
            assertThat(expected.getMessage(), containsString("whatever"));
        }
    }

    @Test public void
    passingAnInvalidArgumentToAnOption() throws Exception {
        cli = new CLI() {{
            option("-b SIZE").ofType(int.class);
        }};
        try {
            cli.parse("-b", "LITERAL");
            fail("Expected exception " + InvalidArgumentException.class.getName());
        } catch (InvalidArgumentException expected) {
            assertEquals("-b", expected.getUnsatisfiedArgument());
            assertEquals("LITERAL", expected.getOffendingValue());
            assertThat(expected.getMessage(), containsString("-b"));
            assertThat(expected.getMessage(), containsString("LITERAL"));
        }
    }

    @Test public void
    specifyingAnUnsupportedType() throws Exception {
        try {
            cli = new CLI() {{
                operand("size").ofType(Object.class);
            }};
            fail("Expected exception " + IllegalArgumentException.class.getName());
        } catch (IllegalArgumentException expected) {
            assertThat(expected.getMessage(), containsString(Object.class.getName()));
        }
    }

    @Test public void
    omittingARequiredOptionArgument() throws Exception {
        cli = new CLI() {{
            option("-b SIZE").ofType(int.class);
        }};
        try {
            cli.parse("-b");
            fail("Expected exception " + ArgumentMissingException.class.getName());
        } catch (ArgumentMissingException expected) {
            assertEquals("-b", expected.getUnsatisfiedOption());
            assertThat(expected.getMessage(), containsString("-b"));
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
        } catch (MissingOperandException expected) {
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
        } catch (InvalidArgumentException expected) {
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

