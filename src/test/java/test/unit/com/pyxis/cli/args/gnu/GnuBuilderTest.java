package test.unit.com.pyxis.cli.args.gnu;

import com.pyxis.cli.ParsingException;
import com.pyxis.cli.args.gnu.GnuBuilder;
import com.pyxis.cli.option.Option;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GnuBuilderTest
{
    private GnuBuilder builder = new GnuBuilder();

    @Test
    public void dashIsPrefixForShortOption() {
        Option option = define("-b");
        assertEquals("b", option.getShortForm());
    }

    @Test
    public void doubleDashIsPrefixForLongOption() {
        Option option = define("--block-size");
        assertEquals("block-size", option.getLongForm());
    }

    @Test
    public void shortOptionAcceptsAnArgumentPattern() {
        Option option = define("-b SIZE");
        assertEquals("SIZE", option.getArgumentPattern());
    }

    @Test
    public void longOptionAcceptsAnArgumentPattern() {
        Option option = define("--block-size SIZE");
        assertEquals("SIZE", option.getArgumentPattern());
    }

    @Test
    public void optionAcceptsADescription() {
        Option option = define("-b", "description");
        assertEquals("description", option.getDescription());
    }

    @Test
    public void optionsCanBeSpecifiedInLiteralForm() throws ParsingException {
        Option option = define("-b", "--block-size SIZE", "Specifies block size");
        assertEquals("Specifies block size", option.getDescription());
        assertEquals("b", option.getShortForm());
        assertEquals("block-size", option.getLongForm());
        assertEquals("SIZE", option.getArgumentPattern());
    }

    private Option define(String... options) {
        return builder.defineOption("option", options);
    }
}
