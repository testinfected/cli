package test.unit.org.testinfected.cli.args;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testinfected.cli.args.CommandLine;
import org.testinfected.cli.args.MissingOperandException;
import org.testinfected.cli.args.OperandSpec;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.OptionSpec;
import org.testinfected.cli.gnu.GnuParser;

import java.util.Arrays;

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
    hasInitiallyNoArguments() {
        assertEquals(0, cl.getAllArgumentValues().size());
        assertNull(cl.getArgumentValue("input"));
    }

    @Test public void
    parsesAndReturnsDetectedOperands() throws Exception {
        add(operand("input"));
        add(operand("output"));

        cl.parse("input", "output");

        assertEquals("input", cl.getArgumentValue("input"));
        assertEquals("output", cl.getArgumentValue("output"));
    }

    @Test public void
    complainsWhenRequiredOperandsAreMissing() throws Exception {
        add(operand("input"));
        add(operand("output"));

        try {
            cl.parse("input");
            fail("Expected exception " + MissingOperandException.class.getName());
        } catch (MissingOperandException expected) {
            assertEquals("output", expected.getMissingOperand());
        }
    }

    @Test public void
    returnsLeftOverArguments() throws Exception {
        add(operand("input"));

        String[] extra = cl.parse("input", "output");
        assertEquals("[output]", Arrays.toString(extra));
    }

    @Test public void
    parsesAndReturnsDetectedOptions() throws Exception {
        add(option("debug").withShortForm("d"));
        add(option("verbose").withShortForm("v"));

        cl.parse("-d");
        assertTrue(cl.hasArgumentValue("debug"));
        assertEquals(Boolean.TRUE, cl.getArgumentValue("debug"));
        assertFalse(cl.hasArgumentValue("verbose"));
        assertNull(cl.getArgumentValue("verbose"));
    }

    @Test public void
    triggersActionsOnDetectedOptions() throws Exception {
        final Option.Action turnDebugOn = context.mock(Option.Action.class, "turn debug on");
        add(option("debug").withShortForm("d").whenPresent(turnDebugOn));

        final Option.Action setLocale = context.mock(Option.Action.class, "set locale");
        add(option("locale").withShortForm("l").whenPresent(setLocale));

        context.checking(new Expectations() {{
            never(turnDebugOn);
            one(setLocale).call(with(any(Option.class)));
        }});

        cl.parse("-l");
    }

    private void add(OperandSpec operand) {
        cl.addOperand(operand.make());
    }

    private void add(OptionSpec option) {
        cl.addOption(option.make());
    }
}
