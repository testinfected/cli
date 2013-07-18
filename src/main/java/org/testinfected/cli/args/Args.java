package org.testinfected.cli.args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Args {

    private final Map<String, Object> values = new HashMap<String, Object>();
    private final List<String> arguments = new ArrayList<String>();

    public int size() {
        return values.size();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) values.get(name);
    }

    public Map<String, ?> values() {
        return new HashMap<String, Object>(values);
    }

    public void put(String name, Object value) {
        values.put(name, value);
    }

    public boolean has(String name) {
        return values.containsKey(name);
    }

    public String[] more() {
        return arguments.toArray(new String[arguments.size()]);
    }

    public void addAll(Collection<String> args) {
        arguments.addAll(args);
    }

    public void add(Args args) {
        values.putAll(args.values);
        arguments.addAll(args.arguments);
    }
}

