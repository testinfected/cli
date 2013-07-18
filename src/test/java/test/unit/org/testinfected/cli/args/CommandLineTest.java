package test.unit.org.testinfected.cli.args;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Args;
import org.testinfected.cli.args.CommandLine;
import org.testinfected.cli.args.MissingOperandException;
import org.testinfected.cli.args.OperandSpec;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.OptionSpec;
import org.testinfected.cli.gnu.GnuParser;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testinfected.cli.args.OperandSpec.operand;
import static org.testinfected.cli.args.OptionSpec.option;

@RunWith(JMock.class)
public class CommandLineTest {
    Mockery context = new JUnit4Mockery();
    CommandLine cl = new CommandLine(new GnuParser());

    @Test public void
    detectsInitiallyNoArgument() throws ParsingException {
        Args args = cl.parseArguments();
        assertEquals(0, args.size());
    }

    @Test public void
    detectsSpecifiedOptionsWhenPresent() throws ParsingException {
        add(option("debug").withShortForm("x"));
        add(option("verbose").withShortForm("v"));

        Args args = cl.parseArguments("-x");
        assertTrue(args.has("debug"));
        assertEquals(TRUE, args.get("debug"));
        assertFalse(args.has("verbose"));
        assertNull(args.get("verbose"));
    }

    @Test public void
    triggersActionsOnDetectedOptions() throws Exception {
        final Option.Action turnDebugOn = context.mock(Option.Action.class, "turn debug on");
        add(option("debug").withShortForm("d").whenPresent(turnDebugOn));

        final Option.Action setLocale = context.mock(Option.Action.class, "set locale");
        add(option("locale").withShortForm("l").whenPresent(setLocale));

        context.checking(new Expectations() {{
            never(turnDebugOn);
            one(setLocale).call(with(any(Args.class)), with(any(Option.class)));
        }});

        cl.parseArguments("-l");
    }

    @Test public void
    detectsSpecifiedOperandsWhenPresent() throws Exception {
        add(operand("input"));
        add(operand("output"));

        Args args = cl.parseArguments("input", "output");

        assertEquals("input", args.get("input"));
        assertEquals("output", args.get("output"));
    }

    @Test public void
    detectsBothOptionsAndOperands() throws Exception {
        add(option("verbose").withShortForm("v"));
        add(operand("input"));

        Args args = cl.parseArguments("-v", "input");
        assertEquals(2, args.size());
    }

    @Test public void
    complainsWhenRequiredOperandsAreMissing() throws Exception {
        add(operand("input"));
        add(operand("output"));

        try {
            cl.parseArguments("input");
            fail("Expected exception " + MissingOperandException.class.getName());
        } catch (MissingOperandException expected) {
            assertEquals("output", expected.getMissingOperand());
        }
    }

    @Test public void
    returnsLeftOverArguments() throws Exception {
        add(operand("input"));

        Args args = cl.parseArguments("input", "output");
        assertEquals(asList("output"), asList(args.more()));
    }

    private void add(OperandSpec operand) {
        cl.addOperand(operand.make());
    }

    private void add(OptionSpec option) {
        cl.addOption(option.make());
    }
}
