package org.testinfected.cli.gnu;

import org.testinfected.cli.args.Help;
import org.testinfected.cli.args.Operand;
import org.testinfected.cli.args.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class GnuHelp implements Help {
    private static final int MEDIUM_WIDTH = 30;

    private final List<Option> options = new ArrayList<Option>();
    private final List<Operand> operands = new ArrayList<Operand>();

    private final int columnWidth;
    private String program;
    private String versionNumber;
    private String description;
    private String epilog;

    public GnuHelp() {
        this(MEDIUM_WIDTH);
    }

    public GnuHelp(int columnWidth) {
        this.columnWidth = columnWidth;
    }

    public void displayProgram(String name) {
        this.program = name;
    }

    public void displayVersion(String number) {
        versionNumber = number;
    }

    public void displayDescription(String description) {
        this.description = description;
    }

    public void displayEnding(String epilog) {
        this.epilog = epilog;
    }

    public void displayOption(Option option) {
        options.add(option);
    }

    public void displayOperand(Operand operand) {
        operands.add(operand);
    }

    public void appendTo(Appendable output) throws IOException {
        final Formatter usage = new Formatter(output);
        formatProgram(usage);
        formatDescription(usage);
        formatUsage(usage);
        formatOptions(usage);
        formatEpilog(usage);
    }

    private void formatProgram(Formatter usage) {
        if (program != null)  usage.format("%s%s%n%n", program, version());
    }

    private String version() {
        return versionNumber != null ? " version " + versionNumber : "";
    }

    private void formatDescription(Formatter usage) throws IOException {
        if (description != null) usage.format("%s%n%n", description);
    }

    private void formatUsage(Formatter usage) {
        usage.format("Usage:");
        if (program != null) usage.format(" %s", program);
        for (Option option : options) {
            usage.format(" [%s%s]", shortestFormOf(option), argumentIfAny(option));
        }
        for (Operand operand : operands) {
            usage.format(" %s", operand.getDisplayName());
        }
        usage.format("%n");
    }

    private String shortestFormOf(Option option) {
        return option.hasShortForm() ? shortFormOf(option) : longFormOf(option);
    }

    private String argumentIfAny(Option option) {
        return option.requiresArgument() ? " " + option.getArgumentPattern() : "";
    }

    private void formatOptions(Formatter usage) throws IOException {
        if (options.isEmpty()) return;
        usage.format("%nOptions:%n");
        for (Option option : options) {
            usage.format("%s%n", descriptionOf(option));
        }
    }

    private CharSequence descriptionOf(Option option) {
        final Formatter line = new Formatter();
        line.format(optionColumnLayout(), allFormsOf(option) + argumentIfAny(option));
        if (option.hasDescription()) {
            if (shouldWrap(line)) wrap(line);
            line.format(infoColumnLayout(), option.getDescription());
        }
        return line.toString();
    }

    private String optionColumnLayout() {
        return "%-" + columnWidth + "s";
    }

    private String allFormsOf(Option option) {
        if (option.hasBothForms()) return shortFormOf(option) + ", " + longFormOf(option);
        if (option.hasShortForm()) return shortFormOf(option);
        return noShortForm() + longFormOf(option);
    }

    private String shortFormOf(Option option) {
        return String.format("-%s", option.getShortForm());
    }

    private String longFormOf(Option option) {
        return String.format("--%s", option.getLongForm());
    }

    private String noShortForm() {
        return "    ";
    }

    public boolean shouldWrap(Formatter line) {
        return line.toString().length() > columnWidth;
    }

    private void wrap(Formatter line) {
        line.format("%n" + optionColumnLayout(), "");
    }

    private String infoColumnLayout() {
        return " %s";
    }

    private void formatEpilog(Formatter usage) {
        if (epilog != null) usage.format("%n%s%n", epilog);
    }
}
