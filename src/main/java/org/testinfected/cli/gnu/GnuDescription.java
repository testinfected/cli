package org.testinfected.cli.gnu;

import org.testinfected.cli.args.ArgsDescription;
import org.testinfected.cli.args.Option;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public class GnuDescription implements ArgsDescription {
    private static final int MEDIUM_WIDTH = 30;

    private final List<Option> options = new ArrayList<Option>();

    private final int optionsColumnWidth;
    private String banner;

    public GnuDescription() {
        this(MEDIUM_WIDTH);
    }

    public GnuDescription(int optionsColumnWidth) {
        this.optionsColumnWidth = optionsColumnWidth;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public void formatOption(Option option) {
        options.add(option);
    }

    public void appendTo(Appendable output) throws IOException {
        output.append("Usage: ");
        if (bannerPresent()) appendBannerTo(output);
        if (optionsSpecified()) describeOptionsTo(output);
    }

    private boolean bannerPresent() {
        return banner != null;
    }

    private void appendBannerTo(Appendable output) throws IOException {
        output.append(banner);
    }

    private boolean optionsSpecified() {
        return !options.isEmpty();
    }

    private void describeOptionsTo(Appendable output) throws IOException {
        final Formatter usage = new Formatter(output);
        usage.format("%n%nOptions:");
        for (Option option : options) {
            usage.format("%n%s", descriptionOf(option));
        }
    }

    private CharSequence descriptionOf(Option option) {
        final Formatter line = new Formatter();
        line.format(optionColumnLayout(), descriptionOfSwitchAndArgument(option));
        if (option.hasDescription()) {
            if (shouldWrap(line)) wrap(line);
            line.format(infoColumnLayout(), option.getDescription());
        }
        return line.toString();
    }

    private String optionColumnLayout() {
        return "%-" + optionsColumnWidth + "s";
    }

    private String descriptionOfSwitchAndArgument(Option option) {
        return option.requiresArgument() ?
                describeSwitch(option) + " " + option.getArgumentPattern() :
                describeSwitch(option);
    }

    public boolean shouldWrap(Formatter line) {
        return line.toString().length() > optionsColumnWidth;
    }

    private void wrap(Formatter line) {
        line.format("%n" + optionColumnLayout(), "");
    }

    private String infoColumnLayout() {
        return " %s";
    }

    private String describeSwitch(Option option) {
        if (option.hasBothForms()) return describeBothForms(option);
        if (option.hasShortForm()) return describeShortFormOnly(option);
        return describeLongFormOnly(option);
    }

    private String describeBothForms(Option option) {
        return String.format("-%s, --%s", option.getShortForm(), option.getLongForm());
    }

    private String describeShortFormOnly(Option option) {
        return String.format("-%s", option.getShortForm());
    }

    private String describeLongFormOnly(Option option) {
        return String.format("    --%s", option.getLongForm());
    }
}
