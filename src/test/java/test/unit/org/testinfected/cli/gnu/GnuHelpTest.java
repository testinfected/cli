package test.unit.org.testinfected.cli.gnu;

import org.junit.Test;
import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Help;
import org.testinfected.cli.gnu.GnuHelp;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.testinfected.cli.args.OperandSpec.operand;
import static org.testinfected.cli.args.OptionSpec.option;

public class GnuHelpTest
{
    String NL = System.getProperty("line.separator");
    GnuHelp help = new GnuHelp(20);

    @Test public void
    displaysEmptyUsageByDefault() throws IOException {
        assertHelp("Usage:");
    }

    @Test public void
    describesPositionalArguments() throws IOException {
        help.printOperand(operand("input").as("IN").help("The source file").make());
        help.printOperand(operand("output").as("OUT").help("The destination file").make());
        assertHelp(
                "Usage: IN OUT",
                "",
                "Arguments:",
                "IN                   The source file",
                "OUT                  The destination file"
        );
    }

    @Test public void
    startsWithProgramNameIfSpecified() throws IOException {
        help.printProgram("program");
        assertHelp(
                "program",
                "",
                "Usage: program");
    }

    @Test public void
    displaysVersionAlongProgramNameWhenGiven() throws IOException {
        help.printProgram("program");
        help.printVersion("1.0");
        assertHelp(
                "program version 1.0",
                "",
                "Usage: program");
    }


    @Test public void
    descriptionFollowsProgramNameWhenGiven() throws IOException {
        help.printProgram("program");
        help.printDescription("My cool program.");
        assertHelp(
                "program",
                "",
                "My cool program.",
                "",
                "Usage: program");
    }

    @Test public void
    includesDescriptionsOfOptions() throws ParsingException, IOException {
        help.printOption(option("raw").withLongForm("raw").describedAs("Specifies raw output format").make());
        help.printOption(option("block size").withShortForm("b").withLongForm("block-size").takingArgument("SIZE").describedAs("Specifies block size").make());
        help.printOption(option("debug").withShortForm("x").describedAs("Turn debugging on").make());

        assertHelp(
                "Usage: [--raw] [-b SIZE] [-x]",
                "",
                "Options:",
                "    --raw            Specifies raw output format",
                "-b, --block-size SIZE",
                "                     Specifies block size",
                "-x                   Turn debugging on");
    }

    @Test public void
    endsWithEpilog() throws ParsingException, IOException {
        help.printEnding("use \"program --help\" to get help");
        help.printOption(option("debug").withShortForm("x").describedAs("Turn debugging on").make());
        assertHelp(
                "Usage: [-x]",
                "",
                "Options:",
                "-x                   Turn debugging on",
                "",
                "use \"program --help\" to get help");
    }

    private void assertHelp(String... lines) throws IOException {
        StringBuilder expected = new StringBuilder();
        for (String line : lines) {
            expected.append(line).append(NL);
        }
        assertEquals("help", expected.toString(), format(help));
    }

    private String format(Help help) throws IOException {
        StringBuilder output = new StringBuilder();
        help.appendTo(output);
        return output.toString();
    }
}
