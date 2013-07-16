package test.unit.org.testinfected.cli;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testinfected.cli.CommandLine;
import org.testinfected.cli.MissingOperandException;
import org.testinfected.cli.args.gnu.GnuParser;
import org.testinfected.cli.option.Option;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.testinfected.cli.OperandBuilder.operandNamed;
import static org.testinfected.cli.option.OptionBuilder.optionNamed;

@RunWith(JMock.class)
public class CommandLineTest {
    Mockery context = new JUnit4Mockery();
    CommandLine cl = new CommandLine();

    @Test public void
    hasInitiallyNoOperand() {
        assertNull(cl.getOperandValue("input"));
    }

    @Test public void
    operandValuesAreAccessibleByName() throws Exception {
        cl.addOperand(operandNamed("input").make());
        cl.addOperand(operandNamed("output").make());

        cl.parse(new GnuParser(), "input", "output");

        assertEquals("input", cl.getOperandValue("input"));
        assertEquals("output", cl.getOperandValue("output"));
    }

    @Test public void
    complainsWhenRequiredOperandsAreNotProvided() throws Exception {
        cl.addOperand(operandNamed("input").make());
        cl.addOperand(operandNamed("output").make());

        try {
            cl.parse(new GnuParser(), "input");
            fail("Expected exception " + MissingOperandException.class.getName());
        } catch (MissingOperandException expected) {
            assertEquals("output", expected.getMissingOperand());
        }
    }

    @Test public void
    returnsLeftOverArguments() throws Exception {
        cl.addOperand(operandNamed("input").make());

        String[] extra = cl.parse(new GnuParser(), "input", "output");
        assertEquals("[output]", Arrays.toString(extra));
    }

    @Test public void
    optionsHaveNoValueUnlessGiven() throws Exception {
        cl.addOption(optionNamed("debug").withShortForm("d").make());
        assertFalse(cl.hasOptionValue("debug"));
        assertNull(cl.getOptionValue("debug"));

        cl.parse(new GnuParser(), "-d");
        assertTrue(cl.hasOptionValue("debug"));
        assertEquals(Boolean.TRUE, cl.getOptionValue("debug"));
    }

    @Test public void
    stubGetsCalledWhenOptionIsGiven() throws Exception {
        final Option.Stub turnDebugOn = context.mock(Option.Stub.class, "turn debug on");
        final Option debug = optionNamed("debug").withShortForm("d").whenPresent(turnDebugOn).make();
        cl.addOption(debug);

        final Option.Stub setLocale = context.mock(Option.Stub.class, "set locale");
        final Option locale = optionNamed("locale").withShortForm("l").whenPresent(setLocale).make();
        cl.addOption(locale);

        context.checking(new Expectations() {{
            never(turnDebugOn);
            one(setLocale).call(with(equal(locale)));
        }});

        cl.parse(new GnuParser(), "-l");
    }
}
