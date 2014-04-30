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

package com.vtence.cli.gnu;

import com.vtence.cli.ParsingException;
import com.vtence.cli.args.Args;
import com.vtence.cli.args.Input;
import com.vtence.cli.args.Option;
import com.vtence.cli.args.Options;
import com.vtence.cli.args.Parser;
import com.vtence.cli.args.UnrecognizedOptionException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Not quite GNU compliant yet
 */
public class GnuParser implements Parser
{
    private static final Pattern LONG_OPTION = Pattern.compile("--(.+)");
    private static final Pattern SHORT_OPTION = Pattern.compile("-(.+)");
    private static final int IDENTIFIER = 1;

    public List<String> parse(Args detected, Options options, Input args) throws ParsingException {
        List<String> nonOptions = new ArrayList<String>();

        while (args.hasNext()) {
            String currentToken = args.next();

            Matcher longForm = LONG_OPTION.matcher(currentToken);
            if (detected(longForm)) {
                Option<?> option = options.find(identifier(longForm));
                if (option == null) throw new UnrecognizedOptionException(currentToken);
                option.handle(detected, args);
                continue;
            }

            Matcher shortForm = SHORT_OPTION.matcher(currentToken);
            if (detected(shortForm)) {
                Option<?> option = options.find(identifier(shortForm));
                if (option == null) throw new UnrecognizedOptionException(currentToken);
                option.handle(detected, args);
                continue;
            }

            nonOptions.add(currentToken);
        }

        return nonOptions;
    }

    private boolean detected(Matcher form) {
        return form.matches();
    }

    private String identifier(Matcher matcher) {
        return matcher.group(IDENTIFIER);
    }
}
