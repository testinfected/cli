package org.testinfected.cli.args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Args {

    private final Map<String, Object> options = new HashMap<String, Object>();
    private final List<String> others = new ArrayList<String>();

    public Args() {}

    public Args(Args args) {
        options.putAll(args.options);
        others.addAll(args.others);
    }

    public int size() {
        return options.size();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) options.get(name);
    }

    public Map<String, ?> values() {
        return options;
    }

    public void put(String name, Object value) {
        options.put(name, value);
    }

    public boolean has(String name) {
        return options.containsKey(name);
    }

    public List<String> others() {
        return new ArrayList<String>(others);
    }

    public void addOthers(Collection<String> args) {
        others.addAll(args);
    }
}

