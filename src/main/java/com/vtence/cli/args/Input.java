package com.vtence.cli.args;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class Input {

    private final List<String> args;
    private int head = 0;

    public static Input listOf(String... args) {
        return new Input(asList(args));
    }

    public Input(Collection<String> args) {
        this.args = new ArrayList<String>(args);
    }

    public boolean hasNext() {
        return args.size() > head;
    }

    public boolean empty() {
        return !hasNext();
    }

    public String next() {
        return args.get(head++);
    }

    public List<String> remaining() {
        return args.subList(head, args.size());
    }
}
