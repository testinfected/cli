package org.testinfected.cli;

import org.testinfected.cli.args.ArgsDescription;
import org.testinfected.cli.args.ArgsParser;
import org.testinfected.cli.option.Option;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.addAll;
import static java.util.Collections.unmodifiableCollection;

public class CommandLine
{
    private final List<String> operands = new ArrayList<String>();
    private final Collection<Option> options = new ArrayList<Option>();

    private String banner = "";

    public CommandLine() {}

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public int getOperandCount() {
        return operands.size();
    }

    public String[] getOperands() {
        return operands.toArray(new String[operands.size()]);
    }

    public String getOperand(int index) {
        return operands.get(index);
    }

    public boolean hasOptionValue(String name) {
        return getOptionValues().containsKey(name);
    }

    public Map<String, ?> getOptionValues() {
        Map<String, Object> opts = new HashMap<String, Object>();
        for (Option opt : options) {
            if (opt.wasGiven()) opts.put(opt.getName(), opt.getValue());
        }

        return opts;
    }

    public Object getOptionValue(String name) {
        return getOptionValues().get(name);
    }

    public String[] parse(ArgsParser parser, String... args) throws ParsingException {
        parseArgs(parser, args);
        callStubs();
        return getOperands();
    }

    private void parseArgs(ArgsParser parser, String... args)
            throws ParsingException {
        String[] argsOperands = parser.parse(unmodifiableCollection(options), args);
        addAll(operands, argsOperands);
    }

    public void formatHelp(ArgsDescription description) {
        description.setBanner(banner);
        for (Option option : options) {
            option.describeTo(description);
        }
    }

    private void callStubs() {
        for (Option option : options) {
            if (option.wasGiven()) option.call();
        }
    }
}
