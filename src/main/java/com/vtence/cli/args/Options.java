package com.vtence.cli.args;

import java.util.Collection;

public class Options {

    private final Collection<Option<?>> options;

    public Options(Collection<Option<?>> options) {
        this.options = options;
    }

    public Option<?> find(String form) {
        for (Option<?> candidate : options) {
            if (candidate.matches(form)) return candidate;
        }
        return null;
    }
}
