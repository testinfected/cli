package org.testinfected.cli.gnu;

import org.testinfected.cli.args.Option;
import org.testinfected.cli.args.Syntax;
import org.testinfected.cli.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GnuSyntax implements Syntax
{
    private static final Pattern LONG_FORM = Pattern.compile("--([^\\s]+)(?:\\s(.+))?");
    private static final Pattern SHORT_FORM = Pattern.compile("-([^\\s]+)(?:\\s+(.+))?");

    private static final int IDENTIFIER = 1;
    private static final int ARGUMENT = 2;

    public Option<?> defineOption(String name, String... definition) {
        Option<Boolean> option = Option.named(name);

        Schema schema = new Schema(option);
        for (String token : definition) {
            Matcher longForm = LONG_FORM.matcher(token);
            if (detected(longForm)) {
                option.withLongForm(schema.validateLongForm(identifier(longForm)));
                option.takingArgument(schema.validateArgument(argument(longForm)));
                continue;
            }

            Matcher shortForm = SHORT_FORM.matcher(token);
            if (detected(shortForm)) {
                option.withShortForm(schema.validateShortForm(identifier(shortForm)));
                option.takingArgument(schema.validateArgument(argument(shortForm)));
                continue;
            }

            option.describedAs(schema.validateDescription(token));
        }

        return option;
    }

    private boolean detected(Matcher longForm) {
        return longForm.matches();
    }

    private String identifier(Matcher matcher) {
        return matcher.group(IDENTIFIER);
    }

    private String argument(Matcher matcher) {
        return matcher.group(ARGUMENT);
    }

    private static class Schema
    {
        private final Option<?> option;

        public Schema(Option<?> option) {
            this.option = option;
        }

        public String validateShortForm(String shortForm) {
            if (option.hasShortForm()) throw new IllegalArgumentException("Short form given twice for option " + quoteName(option));
            return shortForm;
        }

        public String validateLongForm(String longForm) {
            if (option.hasLongForm()) throw new IllegalArgumentException("Long form given twice for option " + quoteName(option));
            return longForm;
        }

        public String validateArgument(String argument) {
            if (Strings.blank(argument)) return null;
            if (option.takesArgument()) throw new IllegalArgumentException("Argument pattern given twice for option " + quoteName(option));
            return argument;
        }

        public String validateDescription(String description) {
            if (option.hasDescription()) throw new IllegalArgumentException("Description given twice for option " + quoteName(option));
            return description;
        }

        private String quoteName(Option<?> option) {
            return "'" + option.getName() + "'";
        }
    }
}
