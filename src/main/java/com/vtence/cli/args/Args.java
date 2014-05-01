package com.vtence.cli.args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Args {

    private final Map<String, Object> arguments = new HashMap<String, Object>();
    private final List<String> others = new ArrayList<String>();

    public Args() {}

    public Args(Args args) {
        arguments.putAll(args.arguments);
        others.addAll(args.others);
    }

    public int size() {
        return arguments.size();
    }

    public <T> void put(String name, T value) {
        arguments.put(name, value);
    }

    public boolean has(String name) {
        return get(name) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) arguments.get(name);
    }

    public Map<String, ?> values() {
        Map<String, Object> values = new HashMap<String, Object>();
        for (String name : arguments.keySet()) {
            values.put(name, arguments.get(name));
        }
        return values;
    }

    public List<String> others() {
        return new ArrayList<String>(others);
    }

    public void addOthers(Collection<String> args) {
        others.addAll(args);
    }
}

