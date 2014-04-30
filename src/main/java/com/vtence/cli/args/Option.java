/*
 * Copyright (c) 2006 Pyxis Technologies inc.
 *
 * This is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA,
 * or see the FSF site: http://www.fsf.org.
 */

package com.vtence.cli.args;

import com.vtence.cli.ParsingException;
import com.vtence.cli.coercion.BooleanCoercer;
import com.vtence.cli.coercion.StringCoercer;
import com.vtence.cli.coercion.TypeCoercer;

import java.util.Map;

public class Option<T> extends Argument<T> implements OptionSpec<T> {

    private static final String ON = Boolean.TRUE.toString();

    private String shortForm;
    private String longForm;
    private String argument;
    private String description;
    private T defaultValue;
    private Action<T> action = new Action<T>() {
        public void call(Args detected, Option<T> option) {
        }
    };

    public static Option<String> named(String name) {
        return new Option<String>(name, new StringCoercer());
    }

    public static Option<Boolean> flag(String name) {
        return new Option<Boolean>(name, new BooleanCoercer());
    }

    protected Option(String name, TypeCoercer<? extends T> type) {
        super(name, type);
    }

    public Option<T> withShortForm(String shortForm) {
        this.shortForm = shortForm;
        return this;
    }

    public String getShortForm() {
        return shortForm;
    }

    public boolean hasShortForm() {
        return shortForm != null;
    }

    public Option<T> withLongForm(String longForm) {
        this.longForm = longForm;
        return this;
    }

    public String getLongForm() {
        return longForm;
    }

    public boolean hasLongForm() {
        return longForm != null;
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

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean hasDefaultValue() {
        return defaultValue != null;
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

    public interface Action<T> {
        void call(Args detected, Option<T> option);
    }

    public boolean matches(String identifier) {
        return identifier.equals(shortForm) || identifier.equals(longForm);
    }

    public boolean isIn(Args detected) {
        return detected.has(name);
    }

    public void call(Args detected) {
        action.call(detected, this);
    }

    public T get(Args args) {
        return args.get(name);
    }

    public void handle(Args detected, Input args) throws ParsingException {
        if (takesArgument() && args.empty()) throw new ArgumentMissingException(this);
        detected.put(this, value(args));
    }

    public void printTo(Help help) {
        help.add(this);
    }

    private T value(Input args) throws InvalidArgumentException {
        return takesArgument() ? convert(args.next()) : convert(ON);
    }
}

