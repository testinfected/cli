package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableCollection;

public class CommandLine
{
    private final Collection<Option> options = new ArrayList<Option>();
    private final List<Operand> operands = new ArrayList<Operand>();

    private String program;
    private String description;
    private String version;
    private String ending;

    public CommandLine() {}

    public void setProgram(String name) {
        this.program = name;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setDescription(String text) {
        this.description = text;
    }

    public void setEnding(String epilog) {
        this.ending = epilog;
    }

    public void addOption(Option option) {
        options.add(option);
    }

    public void addOperand(Operand operand) {
        operands.add(operand);
    }

    public boolean hasArgumentValue(String name) {
        return getAllArgumentValues().get(name) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getArgumentValue(String name) {
        return (T) getAllArgumentValues().get(name);
    }

    public Map<String, ?> getAllArgumentValues() {
        Map<String, Object> arguments = new HashMap<String, Object>();
        for (Option option : options) {
            arguments.put(option.getName(), option.getValue());
        }
        for (Operand operand : operands) {
            arguments.put(operand.getName(), operand.getValue());
        }
        return arguments;
    }

    public String[] parse(Parser parser, String... args) throws ParsingException {
        String[] extraArguments = parseArgs(parser, args);
        callStubs();
        return extraArguments;
    }

    private String[] parseArgs(Parser parser, String... args)
            throws ParsingException {
        return consumeOperands(parser.parse(unmodifiableCollection(options), args));
    }

    private String[] consumeOperands(List<String> positionalArguments) throws ParsingException {
        Iterator<String> args = positionalArguments.iterator();
        for (Operand operand : operands) {
            operand.consume(args);
        }
        return leftOver(args);
    }

    private String[] leftOver(Iterator<String> args) {
        List<String> leftOver = new ArrayList<String>();
        while (args.hasNext()) leftOver.add(args.next());
        return leftOver.toArray(new String[leftOver.size()]);
    }

    public void printHelp(Help help) {
        help.displayProgram(program);
        help.displayVersion(version);
        help.displayDescription(description);
        help.displayEnding(ending);
        for (Option option : options) {
            option.describeTo(help);
        }
        for (Operand operand : operands) {
            operand.describeTo(help);
        }
    }

    private void callStubs() {
        for (Option option : options) {
            if (option.isDetected()) option.call();
        }
    }
}
