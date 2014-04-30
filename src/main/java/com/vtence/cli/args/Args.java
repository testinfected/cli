package com.vtence.cli.args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Args {

    private final Map<Argument<?>, Object> arguments = new HashMap<Argument<?>, Object>();
    private final List<String> others = new ArrayList<String>();

    public Args() {}

    public Args(Args args) {
        arguments.putAll(args.arguments);
        others.addAll(args.others);
    }

    public int size() {
        return arguments.size();
    }

    public <T> void put(Argument<T> arg, T value) {
        arguments.put(arg, value);
    }

    public boolean has(String name) {
        return get(name) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        for (Argument<?> arg : arguments.keySet()) {
            // This is a safe cast now that the put method is typed
            if (arg.getName().equals(name)) return (T) arguments.get(arg);
        }
        return null;
    }

    public Map<String, ?> values() {
        Map<String, Object> values = new HashMap<String, Object>();
        for (Argument<?> arg : arguments.keySet()) {
            values.put(arg.getName(), arguments.get(arg));
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

