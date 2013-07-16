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

package org.testinfected.cli.option;

import org.testinfected.cli.coercion.TypeCoercer;

import java.util.HashMap;
import java.util.Map;

public class OptionBuilder
{
    private final Option option;

    private final Map<Class, TypeCoercer<?>> typeCoercers = new HashMap<Class, TypeCoercer<?>>();

    public static OptionBuilder optionNamed(String name) {
        return option(new Option(name));
    }

    public static OptionBuilder option(Option underConstruction) {
        return new OptionBuilder(underConstruction);
    }

    protected OptionBuilder(Option underConstruction) {
        this.option = underConstruction;
    }

    public OptionBuilder withRequiredArg(String value) {
        option.setArgumentPattern(value);
        return this;
    }

    public OptionBuilder withShortForm(String shortForm) {
        option.setShortForm(shortForm);
        return this;
    }

    public OptionBuilder withDescription(String text) {
        option.setDescription(text);
        return this;
    }

    public OptionBuilder withLongForm(String longForm) {
        option.setLongForm(longForm);
        return this;
    }

    public OptionBuilder defaultingTo(Object value) {
        option.setValue(value);
        return this;
    }

    public OptionBuilder ofType(Class type) {
        return coerceWith(coercerFor(type));
    }

    public OptionBuilder coerceWith(TypeCoercer typeCoercer) {
        option.setCoercer(typeCoercer);
        return this;
    }

    public OptionBuilder whenPresent(Option.Stub stub) {
        option.setStub(stub);
        return this;
    }

    public Option make() {
        return option.validate();
    }

    public OptionBuilder using(Map<Class<?>, TypeCoercer<?>> coercers) {
        this.typeCoercers.putAll(coercers);
        return this;
    }

    private TypeCoercer coercerFor(Class type) {
        if (!typeCoercers.containsKey(type))
            throw new IllegalArgumentException("Don't know how to coerce type " + type.getName());

        return typeCoercers.get(type);
    }
}
