package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableCollection;

public class CommandLine
{
    private final Parser parser;
    private final Collection<Option> options = new ArrayList<Option>();
    private final List<Operand> operands = new ArrayList<Operand>();

    private String program;
    private String description;
    private String version;
    private String ending;

    public CommandLine(Parser parser) {
        this.parser = parser;
    }

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

    public Args parseArguments(String... args) throws ParsingException {
        Args detected =  new Args();
        addDefaultOptions(detected);
        List<String> nonOptionArguments = parseOptions(detected, args);
        List<String> more = parseOperands(detected, nonOptionArguments);
        detected.addAll(more);

        for (Option option : options) {
            if (option.isIn(detected)) option.call(detected);
        }

        return detected;
    }

    private void addDefaultOptions(Args detected) {
        for (Option option : options) {
            if (option.hasDefaultValue()) detected.put(option.getName(), option.getDefaultValue());
        }
    }

    private List<String> parseOperands(Args detected, List<String> arguments) throws ParsingException {
        List<String> more = new ArrayList<String>();
        Iterator<String> args = arguments.iterator();
        for (Operand operand : operands) {
            operand.consume(detected, args);
        }
        while (args.hasNext()) more.add(args.next());
        return more;
    }

    private List<String> parseOptions(Args detected, String[] args) throws ParsingException {
        return parser.parse(detected, unmodifiableCollection(options), args);
    }

    public void printTo(Help help) {
        help.printProgram(program);
        help.printVersion(version);
        help.printDescription(description);
        for (Option option : options) {
            option.printTo(help);
        }
        for (Operand operand : operands) {
            operand.printTo(help);
        }
        help.printEnding(ending);
    }
}
