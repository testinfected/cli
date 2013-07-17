package test.unit.org.testinfected.cli.args;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testinfected.cli.args.CommandLine;
import org.testinfected.cli.args.MissingOperandException;
import org.testinfected.cli.gnu.GnuParser;
import org.testinfected.cli.args.Option;

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
    CommandLine cl = new CommandLine();

    @Test public void
    hasInitiallyNoArguments() {
        assertEquals(0, cl.getAllArgumentValues().size());
        assertNull(cl.getArgumentValue("input"));
    }

    @Test public void
    operandValuesAreAccessibleByName() throws Exception {
        cl.addOperand(operand("input"));
        cl.addOperand(operand("output"));

        cl.parse(new GnuParser(), "input", "output");

        assertEquals("input", cl.getArgumentValue("input"));
        assertEquals("output", cl.getArgumentValue("output"));
    }

    @Test public void
    complainsWhenRequiredOperandsAreNotProvided() throws Exception {
        cl.addOperand(operand("input"));
        cl.addOperand(operand("output"));

        try {
            cl.parse(new GnuParser(), "input");
            fail("Expected exception " + MissingOperandException.class.getName());
        } catch (MissingOperandException expected) {
            assertEquals("output", expected.getMissingOperand());
        }
    }

    @Test public void
    returnsLeftOverArguments() throws Exception {
        cl.addOperand(operand("input"));

        String[] extra = cl.parse(new GnuParser(), "input", "output");
        assertEquals("[output]", Arrays.toString(extra));
    }

    @Test public void
    optionsHaveNoValueUnlessDetected() throws Exception {
        cl.addOption(option("debug").withShortForm("d"));
        assertFalse(cl.hasArgumentValue("debug"));
        assertNull(cl.getArgumentValue("debug"));

        cl.parse(new GnuParser(), "-d");
        assertTrue(cl.hasArgumentValue("debug"));
        assertEquals(Boolean.TRUE, cl.getArgumentValue("debug"));
    }

    @Test public void
    triggersActionOnDetectedOptions() throws Exception {
        final Option.Action turnDebugOn = context.mock(Option.Action.class, "turn debug on");
        cl.addOption(option("debug").withShortForm("d").whenPresent(turnDebugOn));

        final Option.Action setLocale = context.mock(Option.Action.class, "set locale");
        cl.addOption(option("locale").withShortForm("l").whenPresent(setLocale));

        context.checking(new Expectations() {{
            never(turnDebugOn);
            one(setLocale).call(with(any(Option.class)));
        }});

        cl.parse(new GnuParser(), "-l");
    }
}
