/*
 * Copyright (c) 2007 Pyxis Technologies inc.
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

import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public class OptionSpec
{
    private final Map<Class, TypeCoercer<?>> coercers = new HashMap<Class, TypeCoercer<?>>();

    private final String name;
    private String argument;
    private String shortForm;
    private String longForm;
    private String description;
    private Object defaultValue;
    private TypeCoercer type = new StringCoercer();
    private Option.Action action = Option.Action.NOTHING;

    public static OptionSpec option(String name) {
        return new OptionSpec(name);
    }

    protected OptionSpec(String name) {
        this.name = name;
    }

    public OptionSpec takingArgument(String argument) {
        this.argument = argument;
        return this;
    }

    public OptionSpec withShortForm(String shortForm) {
        this.shortForm = shortForm;
        return this;
    }

    public OptionSpec withLongForm(String longForm) {
        this.longForm = longForm;
        return this;
    }

    public OptionSpec describedAs(String text) {
        this.description = text;
        return this;
    }

    public OptionSpec defaultingTo(Object value) {
        this.defaultValue = value;
        return this;
    }

    public OptionSpec ofType(Class type) {
        return ofType(coercerFor(type));
    }

    public OptionSpec ofType(TypeCoercer type) {
        this.type = type;
        return this;
    }

    public OptionSpec whenPresent(Option.Action action) {
        this.action = action;
        return this;
    }

    public OptionSpec using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.coercers.putAll(coercers);
        return this;
    }

    public Option make() {
        if (shortForm == null && longForm == null)
            throw new IllegalArgumentException("Either short form or long form is required for option '" + name + "'");
        Option option = new Option(name, type);
        option.setShortForm(shortForm);
        option.setLongForm(longForm);
        option.setValue(defaultValue);
        option.setDescription(description);
        option.setArgument(argument);
        option.setAction(action);
        return option;
    }

    private TypeCoercer coercerFor(Class type) {
        if (!coercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return coercers.get(type);
    }

    public String getName() {
        return name;
    }

    public boolean hasShortForm() {
        return shortForm != null;
    }

    public boolean hasArgument() {
        return argument != null;
    }

    public boolean hasLongForm() {
        return longForm != null;
    }

    public boolean hasDescription() {
        return description != null;
    }
}
