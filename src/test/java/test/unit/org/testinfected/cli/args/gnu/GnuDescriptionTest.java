package test.unit.org.testinfected.cli.args.gnu;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.ArgsDescription;
import org.testinfected.cli.gnu.GnuDescription;
import org.junit.Test;

import java.io.IOException;

import static org.testinfected.cli.args.OptionBuilder.optionNamed;
import static org.junit.Assert.assertEquals;

public class GnuDescriptionTest
{
    GnuDescription description = new GnuDescription(20);

    @Test public void
    displaysEmptyUsageByDefault() throws IOException {
        assertEquals("Usage: ", getHelp(description));
    }

    @Test public void
    displaysBannerIfIncluded() throws IOException {
        description.setBanner("My cool program v1.0");
        assertEquals("Usage: My cool program v1.0", getHelp(description));
    }

    @Test public void
    helpMessageIncludesBannerAndDescriptionsOfOptions() throws ParsingException, IOException {
        description.setBanner("My cool program v1.0");
        description.formatOption(optionNamed("raw").withLongForm("raw").withDescription("Specifies raw ouput format").make());
        description.formatOption(optionNamed("block size").withShortForm("b").withLongForm("block-size").withRequiredArg("SIZE").withDescription("Specifies block size").make());
        description.formatOption(optionNamed("debug").withShortForm("x").withDescription("Turn debugging on").make());

        assertEquals(
                "Usage: My cool program v1.0\n" +
                        "\n" +
                        "Options:\n" +
                        "    --raw            Specifies raw ouput format\n" +
                        "-b, --block-size SIZE\n" +
                        "                     Specifies block size\n" +
                        "-x                   Turn debugging on",
                getHelp(description));
    }

    private String getHelp(ArgsDescription formatter) throws IOException {
        StringBuilder buffer = new StringBuilder();
        formatter.appendTo(buffer);
        return buffer.toString();
    }
}
