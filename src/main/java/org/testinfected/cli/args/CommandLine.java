package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;

import java.util.ArrayList;
import java.util.List;

public class CommandLine
{
    private final Parser parser;
    private final List<Option> options = new ArrayList<Option>();
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

    public Args parse(String... args) throws ParsingException {
        Args detected =  new Args();
        addDefaultOptions(detected);
        List<String> nonOptionArguments = parseOptions(detected, Input.listOf(args));
        List<String> others = parseOperands(detected, new Input(nonOptionArguments));
        detected.addOthers(others);
        callDetectedOptions(detected);
        return detected;
    }

    private void addDefaultOptions(Args detected) {
        for (Option option : options) {
            if (option.hasDefaultValue()) detected.put(option.getName(), option.getDefaultValue());
        }
    }

    private List<String> parseOptions(Args detected, Input args) throws ParsingException {
        return parser.parse(detected, new Options(options), args);
    }

    private List<String> parseOperands(Args detected, Input args) throws ParsingException {
         for (Operand operand : operands) {
             operand.consume(detected, args);
         }
         return args.remaining();
     }

    private void callDetectedOptions(Args detected) {
        for (Option option : options) {
            if (option.isIn(detected)) option.call(detected);
        }
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
