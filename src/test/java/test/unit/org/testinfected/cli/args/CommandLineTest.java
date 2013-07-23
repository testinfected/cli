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
import org.testinfected.cli.args.Operand;
import org.testinfected.cli.args.Option;
import org.testinfected.cli.coercion.LocaleCoercer;
import org.testinfected.cli.gnu.GnuParser;

import java.util.Locale;

import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JMock.class)
public class CommandLineTest {
    Mockery context = new JUnit4Mockery();
    CommandLine cl = new CommandLine(new GnuParser());

    @Test public void
    detectsInitiallyNoArgument() throws ParsingException {
        Args args = cl.parse();
        assertEquals(0, args.size());
    }

    @Test public void
    detectsSpecifiedOptionsWhenPresent() throws ParsingException {
        cl.add(Option.named("debug").withShortForm("x"));
        cl.add(Option.named("verbose").withShortForm("v"));

        Args args = cl.parse("-x");
        assertTrue(args.has("debug"));
        assertEquals(TRUE, args.get("debug"));
        assertFalse(args.has("verbose"));
        assertNull(args.get("verbose"));
    }

    @SuppressWarnings("unchecked")
    @Test public void
    triggersActionsOnDetectedOptions() throws Exception {
        final Option.Action<Boolean> turnDebugOn = context.mock(Option.Action.class, "turn debug on");
        cl.add(Option.named("debug").withShortForm("d").whenPresent(turnDebugOn));

        final Option.Action<Locale> setLocale = context.mock(Option.Action.class, "set locale");
        cl.add(Option.named("locale").withShortForm("l").ofType(new LocaleCoercer()).whenPresent(setLocale));

        context.checking(new Expectations() {{
            never(turnDebugOn);
            one(setLocale).call(with(any(Args.class)), with(any(Option.class)));
        }});

        cl.parse("-l");
    }

    @Test public void
    detectsSpecifiedOperandsWhenPresent() throws Exception {
        cl.add(Operand.named("input"));
        cl.add(Operand.named("output"));

        Args args = cl.parse("input", "output");

        assertEquals("input", args.get("input"));
        assertEquals("output", args.get("output"));
    }

    @Test public void
    detectsBothOptionsAndOperands() throws Exception {
        cl.add(Option.named("verbose").withShortForm("v"));
        cl.add(Operand.named("input"));

        Args args = cl.parse("-v", "input");
        assertEquals(2, args.size());
    }

    @Test public void
    complainsWhenRequiredOperandsAreMissing() throws Exception {
        cl.add(Operand.named("input"));
        cl.add(Operand.named("output"));

        try {
            cl.parse("input");
            fail("Expected exception " + MissingOperandException.class.getName());
        } catch (MissingOperandException expected) {
            assertEquals("output", expected.getMissingOperand());
        }
    }

    @Test public void
    returnsUnprocessedArguments() throws Exception {
        cl.add(Operand.named("input"));

        Args args = cl.parse("input", "output");
        assertEquals(asList("output"), args.others());
    }
}
