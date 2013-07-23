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

package org.testinfected.cli.args;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public class Option {
    private final Map<Class, TypeCoercer<?>> coercers = new HashMap<Class, TypeCoercer<?>>();

    private static final Boolean ON = Boolean.TRUE;

    private final String name;

    private String shortForm;
    private String longForm;
    private String argument;
    private String description;
    private Object defaultValue;
    private TypeCoercer typeCoercer;
    private Action action = Action.NOTHING;

    public static Option named(String name) {
        return new Option(name);
    }

    public Option(String name) {
        this(name, new StringCoercer());
    }

    public Option(String name, TypeCoercer type) {
        this.name = name;
        this.typeCoercer = type;
    }

    public String getName() {
        return name;
    }

    public Option withShortForm(String shortForm) {
        this.shortForm = shortForm;
        return this;
    }

    public String getShortForm() {
        return shortForm;
    }

    public boolean hasShortForm() {
        return shortForm != null;
    }

    public Option withLongForm(String longForm) {
        this.longForm = longForm;
        return this;
    }

    public String getLongForm() {
        return longForm;
    }

    public boolean hasLongForm() {
        return longForm != null;
    }

    public Option describedAs(String description) {
        this.description = description;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public Option takingArgument(String argument) {
        this.argument = argument;
        return this;
    }

    public String getArgument() {
        return argument;
    }

    public boolean takesArgument() {
        return argument != null;
    }

    public Option defaultingTo(Object value) {
        this.defaultValue = value;
        return this;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public boolean hasDefaultValue() {
        return defaultValue != null;
    }

    public Option ofType(Class type) {
        return ofType(coercerFor(type));
    }

    public Option using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.coercers.putAll(coercers);
        return this;
    }

    public Option ofType(TypeCoercer type) {
        this.typeCoercer = type;
        return this;
    }

    public Option whenPresent(Option.Action action) {
        this.action = action;
        return this;
    }

    public interface Action {
        public static final Action NOTHING = new NoOp();

        public static class NoOp implements Action {
            public void call(Args detected, Option option) {
            }
        }

        void call(Args detected, Option option);
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

    public <T> T getValue(Args args) {
        return args.get(name);
    }

    public void handle(Args detected, Input args) throws ParsingException {
        if (takesArgument() && args.empty()) throw new ArgumentMissingException(this);
        detected.put(name, value(args));
    }

    public void printTo(Help help) {
        help.print(this);
    }

    private Object value(Input args) throws InvalidArgumentException {
        return takesArgument() ? convert(args.next()) : ON;
    }

    private Object convert(String value) throws InvalidArgumentException {
        try {
            return typeCoercer.convert(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(name, value, e);
        }
    }

    private TypeCoercer coercerFor(Class type) {
        if (!coercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return coercers.get(type);
    }
}

