package org.testinfected.cli.args;

import java.util.Collection;

public class Options {

    private final Collection<Option<?>> options;

    public Options(Collection<Option<?>> options) {
        this.options = options;
    }

    public Option<?> find(String option) {
        for (Option<?> candidate : options) {
            if (candidate.matches(option)) return candidate;
        }
        return null;
    }
}
