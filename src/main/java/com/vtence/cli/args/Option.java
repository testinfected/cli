package com.vtence.cli.args;

import com.vtence.cli.ParsingException;
import com.vtence.cli.coercion.BooleanCoercer;
import com.vtence.cli.coercion.StringCoercer;
import com.vtence.cli.coercion.TypeCoercer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Option<T> extends Argument<T> implements OptionSpec<T> {

    private static final String ON = Boolean.TRUE.toString();

    private final List<String> forms = new ArrayList<String>();
    private String argument;
    private String description;
    private T defaultValue;
    private Action<T> action = new Action<T>() {
        public void call(Args detected, Option<T> option) {
        }
    };

    public static Option<String> option(String form) {
        return new Option<String>(form, new StringCoercer());
    }

    public static Option<Boolean> flag(String form) {
        return new Option<Boolean>(form, new BooleanCoercer());
    }

    protected Option(String form, TypeCoercer<? extends T> type) {
        super(form, type);
        alias(form);
    }

    public boolean matches(String name) {
        return forms.contains(name);
    }

    public Option<T> alias(String form) {
        forms.add(form);
        return this;
    }

    public String formMatching(String pattern) {
        for (String form : forms) {
            if (form.matches(pattern)) {
                return form;
            }
        }
        return null;
    }

    public Option<T> describedAs(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    @SuppressWarnings("unchecked")
    public Option<String> takingArgument(String argument) {
        this.argument = argument;
        return ofType(new StringCoercer());
    }

    public String getArgument() {
        return argument;
    }

    public boolean takesArgument() {
        return argument != null;
    }

    public <S extends T> Option<T> defaultingTo(S value) {
        this.defaultValue = value;
        return this;
    }

    public <S> Option<S> ofType(Class<? extends S> type) {
        return ofType(coercerFor(type));
    }

    @SuppressWarnings("unchecked")
    public <S> Option<S> ofType(TypeCoercer<? extends S> type) {
        this.typeCoercer = (TypeCoercer<? extends T>) type;
        return (Option<S>) this;
    }

    public Option<T> whenPresent(Option.Action<T> action) {
        this.action = action;
        return this;
    }

    public Option<T> using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.coercers.putAll(coercers);
        return this;
    }

    public void initialize(Args args) {
        for (String form : forms) {
            args.put(form, defaultValue);
        }
    }

    public interface Action<T> {
        void call(Args detected, Option<T> option);
    }

    public boolean in(Args detected) {
        return detected.has(name);
    }

    public void call(Args detected) {
        action.call(detected, this);
    }

    public T get(Args args) {
        return args.get(name);
    }

    public void handle(Args detected, Input args) throws ParsingException {
        if (takesArgument() && args.empty()) throw new ArgumentMissingException(name, argument);
        T value = value(args);
        for (String form : forms) {
            detected.put(form, value);
        }
    }

    public void printTo(Help help) {
        help.add(this);
    }

    private T value(Input args) throws InvalidArgumentException {
        return takesArgument() ? convert(args.next()) : convert(ON);
    }
}

