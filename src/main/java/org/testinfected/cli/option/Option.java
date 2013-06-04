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

package org.testinfected.cli.option;

import org.testinfected.cli.ParsingException;
import org.testinfected.cli.args.ArgsDescription;
import org.testinfected.cli.coercion.StringCoercer;
import org.testinfected.cli.coercion.TypeCoercer;

import java.util.Iterator;

public class Option {
    private static final Boolean SWITCH_ON = Boolean.TRUE;

    private String name;
    private String shortForm;
    private String longForm;
    private String argumentPattern;
    private String description;
    private Object value;
    private TypeCoercer typeCoercer = new StringCoercer();
    private Stub stub = Stub.NOTHING;

    public Option(String name) {
        this.name = name;
    }

    public Option validate() {
        if (!isValid()) throw new IllegalArgumentException("Either short form or long form is required");
        return this;
    }

    public boolean isValid() {
        return hasShortForm() || hasLongForm();
    }

    public void consume(Iterator<String> arguments) throws ParsingException {
        if (takesArgument() && outOf(arguments)) throw new ArgumentMissingException(this);
        value = takesArgument() ? convert(arguments.next()) : SWITCH_ON;
    }

    private boolean outOf(Iterator<String> arguments) {
        return !arguments.hasNext();
    }

    public String getName() {
        return name;
    }

    public boolean matches(String identifier) {
        return identifier.equals(shortForm) || identifier.equals(longForm);
    }

    public void setShortForm(String form) {
        this.shortForm = form;
    }

    public String getShortForm() {
        return shortForm;
    }

    public boolean hasShortForm() {
        return shortForm != null;
    }

    public void setLongForm(String form) {
        this.longForm = form;
    }

    public String getLongForm() {
        return longForm;
    }

    public boolean hasLongForm() {
        return longForm != null;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public void setArgumentPattern(String arg) {
        this.argumentPattern = arg;
    }

    public String getArgumentPattern() {
        return argumentPattern;
    }

    public boolean takesArgument() {
        return argumentPattern != null;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public void setCoercer(TypeCoercer typeCoercer) {
        this.typeCoercer = typeCoercer;
    }

    public void setStub(Stub stub) {
        this.stub = stub;
    }

    public boolean wasGiven() {
        return value != null;
    }

    public void call() {
        stub.call(this);
    }

    public boolean hasBothForms() {
        return hasShortForm() && hasLongForm();
    }

    public void describeTo(ArgsDescription description) {
        description.formatOption(this);
    }

    public interface Stub {

        public static final Stub NOTHING = new NoOp();

        public static class NoOp implements Stub {
            public void call(Option option) {
            }
        }

        void call(Option option);
    }

    private Object convert(String value) throws InvalidArgumentException {
        try {
            return typeCoercer.convert(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(this, value, e);
        }
    }
}

