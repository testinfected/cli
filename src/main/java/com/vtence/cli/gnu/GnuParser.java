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

/**
 * Not quite GNU compliant yet
 */
public class GnuParser implements Parser
{
    public List<String> parse(Args detected, Options options, Input args) throws ParsingException {
        List<String> nonOptions = new ArrayList<String>();

        while (args.hasNext()) {
            String token = args.next();

            if (token.startsWith("--")) {
                Option<?> option = options.find(token);
                if (option == null) throw new UnrecognizedOptionException(token);
                option.handle(detected, args);
                continue;
            }

            if (token.startsWith("-")) {
                Option<?> option = options.find(token);
                if (option == null) throw new UnrecognizedOptionException(token);
                option.handle(detected, args);
                continue;
            }

            nonOptions.add(token);
        }

        return nonOptions;
    }
}
