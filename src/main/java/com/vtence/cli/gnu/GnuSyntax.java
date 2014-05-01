package com.vtence.cli.gnu;

import com.vtence.cli.args.Option;
import com.vtence.cli.args.Syntax;
import com.vtence.cli.util.Strings;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.vtence.cli.util.Strings.quote;

public class GnuSyntax implements Syntax
{
    private static final Pattern SHORT_FORM = Pattern.compile("(-[^-\\s])(?:\\s(.+))?");
    private static final Pattern LONG_FORM = Pattern.compile("(--[^-\\s][^\\s]+)(?:\\s(.+))?");

    private static final int IDENTIFIER = 1;
    private static final int ARGUMENT = 2;

    public Option<String> defineOption(String... definition) {
        OptionDefinition option = new OptionDefinition();

        for (String token : definition) {
            Matcher longForm = LONG_FORM.matcher(token);
            if (longForm.matches()) {
                option.longForm(identifier(longForm));
                option.argument(argument(longForm));
                continue;
            }

            Matcher shortForm = SHORT_FORM.matcher(token);
            if (shortForm.matches()) {
                option.shortForm(identifier(shortForm));
                option.argument(argument(shortForm));
                continue;
            }

            option.description(token);
        }

        return option.define();
    }

    private String identifier(Matcher matcher) {
        return matcher.group(IDENTIFIER);
    }

    private String argument(Matcher matcher) {
        return matcher.group(ARGUMENT);
    }

    private static class OptionDefinition {
        private String shortForm;
        private String longForm;
        private String argument;
        private String description;

        public void shortForm(String form) {
            if (shortForm != null)
                throw new IllegalArgumentException("Short form given twice: " + quote(shortForm) + " and " + quote(form));
            this.shortForm = form;
        }

        public void longForm(String form) {
            if (longForm != null)
                throw new IllegalArgumentException("Long form given twice: " + quote(longForm) + " and " + quote(form));
            this.longForm = form;
        }

        public void argument(String name) {
            if (Strings.blank(name)) return;
            if (argument != null)
                throw new IllegalArgumentException("Argument given twice: " + quote(argument) + " and " + quote(name));
            this.argument = name;
        }

        public void description(String desc) {
            if (description != null)
                throw new IllegalArgumentException("Description given twice: " + quote(description) + " and " + quote(desc));
            this.description = desc;
        }

        public Option<String> define() {
            Option<String> option;
            if (shortForm == null && longForm == null) {
                throw new IllegalArgumentException("Option require a short and/or long form");
            }
            if (shortForm != null && longForm != null) {
                option = Option.option(shortForm);
                option.alias(longForm);
            } else if (shortForm != null) {
                option = Option.option(shortForm);
            } else {
                option = Option.option(longForm);
            }
            option.takingArgument(argument);
            option.describedAs(description);

            return option;
        }
    }
}
