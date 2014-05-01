package com.vtence.cli.gnu;

import com.vtence.cli.args.Help;
import com.vtence.cli.args.Operand;
import com.vtence.cli.args.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class GnuHelp implements Help {
    private static final int MEDIUM_WIDTH = 30;

    private final List<Option<?>> options = new ArrayList<Option<?>>();
    private final List<Operand<?>> operands = new ArrayList<Operand<?>>();

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

    public void setProgram(String name) {
        this.program = name;
    }

    public void setVersion(String number) {
        versionNumber = number;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnding(String epilog) {
        this.epilog = epilog;
    }

    public void add(Option<?> option) {
        options.add(option);
    }

    public void add(Operand<?> operand) {
        operands.add(operand);
    }

    public void printTo(Appendable output) throws IOException {
        final Formatter usage = new Formatter(output);
        formatProgram(usage);
        formatDescription(usage);
        formatUsage(usage);
        formatOperands(usage);
        formatOptions(usage);
        formatEpilog(usage);
    }

    private void formatProgram(Formatter help) {
        if (program != null)  help.format("%s%s%n%n", program, version());
    }

    private String version() {
        return versionNumber != null ? " version " + versionNumber : "";
    }

    private void formatDescription(Formatter help) throws IOException {
        if (description != null) help.format("%s%n%n", description);
    }

    private void formatUsage(Formatter help) {
        help.format("Usage:");
        if (program != null) help.format(" %s", program);
        for (Option option : options) {
            help.format(" [%s%s]", shortestFormOf(option), argumentIfAny(option));
        }
        for (Operand operand : operands) {
            help.format(" %s", operand.getDisplayName());
        }
        help.format("%n");
    }

    private String shortestFormOf(Option<?> option) {
        return hasShortForm(option) ? shortFormOf(option) : longFormOf(option);
    }

    private String argumentIfAny(Option<?> option) {
        return option.takesArgument() ? " " + option.getArgument() : "";
    }

    private void formatOperands(Formatter help) {
        if (operands.isEmpty()) return;
        help.format("%nArguments:%n");
        for (Operand<?> operand : operands) {
            help.format("%s%n", descriptionOf(operand));
        }
    }

    private String descriptionOf(Operand<?> operand) {
        final Formatter line = new Formatter();
        line.format(firstColumnLayout(), operand.getDisplayName());
        if (operand.hasDescription()) {
            if (shouldWrap(line)) wrap(line);
            line.format(secondColumnLayout(), operand.getDescription());
        }
        return line.toString();
    }

    private void formatOptions(Formatter help) throws IOException {
        if (options.isEmpty()) return;
        help.format("%nOptions:%n");
        for (Option<?> option : options) {
            help.format("%s%n", descriptionOf(option));
        }
    }

    private String descriptionOf(Option<?> option) {
        final Formatter line = new Formatter();
        line.format(firstColumnLayout(), allFormsOf(option) + argumentIfAny(option));
        if (option.hasDescription()) {
            if (shouldWrap(line)) wrap(line);
            line.format(secondColumnLayout(), option.getDescription());
        }
        return line.toString();
    }

    private String firstColumnLayout() {
        return "%-" + columnWidth + "s";
    }

    private String allFormsOf(Option<?> option) {
        if (hasShortForm(option) && hasLongForm(option)) return shortFormOf(option) + ", " + longFormOf(option);
        if (hasShortForm(option)) return shortFormOf(option);
        return noShortForm() + longFormOf(option);
    }

    private boolean hasShortForm(Option<?> option) {
        return shortFormOf(option) != null;
    }

    private boolean hasLongForm(Option<?> option) {
        return longFormOf(option) != null;
    }

    private String shortFormOf(Option<?> option) {
        return option.formMatching("(-[^-\\s])");
    }

    private String longFormOf(Option<?> option) {
        return option.formMatching("(--[^-\\s][^\\s]+)");
    }

    private String noShortForm() {
        return "    ";
    }

    public boolean shouldWrap(Formatter line) {
        return line.toString().length() > columnWidth;
    }

    private void wrap(Formatter line) {
        line.format("%n" + firstColumnLayout(), "");
    }

    private String secondColumnLayout() {
        return " %s";
    }

    private void formatEpilog(Formatter help) {
        if (epilog != null) help.format("%n%s%n", epilog);
    }
}
