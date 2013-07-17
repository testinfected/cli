/*
 * Copyright (c) 2007 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */

package org.testinfected.cli.gnu;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.Parser;
import org.testinfected.cli.args.UnrecognizedOptionException;
import org.testinfected.cli.args.Option;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;

/**
 * Not quite GNU compliant yet
 */
public class GnuParser implements Parser
{
    private static final Pattern LONG_OPTION = Pattern.compile("--(.+)");
    private static final Pattern SHORT_OPTION = Pattern.compile("-(.+)");

    public List<String> parse(Iterable<Option> options, String... args) throws ParsingException {
        List<String> arguments = new ArrayList<String>();

        for (Iterator<String> tokens = asList(args).iterator(); tokens.hasNext(); ) {
            String currentToken = tokens.next();

            Matcher longForm = LONG_OPTION.matcher(currentToken);
            if (detected(longForm)) {
                Option option = findOption(options, longForm);
                if (option == null) throw new UnrecognizedOptionException(currentToken);
                option.consume(tokens);
                continue;
            }

            Matcher shortForm = SHORT_OPTION.matcher(currentToken);
            if (detected(shortForm)) {
                Option option = findOption(options, shortForm);
                if (option == null) throw new UnrecognizedOptionException(currentToken);
                option.consume(tokens);
                continue;
            }

            arguments.add(currentToken);
        }

        return arguments;
    }

    private boolean detected(Matcher form) {
        return form.matches();
    }

    private Option findOption(Iterable<Option> candidates, Matcher matcher) {
        for (Option candidate : candidates) {
            if (candidate.matches(identifier(matcher))) return candidate;
        }

        return null;
    }

    private String identifier(Matcher matcher) {
        return matcher.group(1);
    }
}
