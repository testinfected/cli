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

import java.util.Iterator;

public class Option {
    private static final Boolean SWITCH_ON = Boolean.TRUE;

    private final String name;

    private String shortForm;
    private String longForm;
    private String argument;
    private String description;
    private Object value;
    private TypeCoercer typeCoercer;
    private Action action = Action.NOTHING;

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

    public void setArgument(String arg) {
        this.argument = arg;
    }

    public String getArgument() {
        return argument;
    }

    public boolean takesArgument() {
        return argument != null;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isDetected() {
        return value != null;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public boolean matches(String identifier) {
        return identifier.equals(shortForm) || identifier.equals(longForm);
    }

    public void call() {
        action.call(this);
    }

    public void describeTo(Help help) {
        help.displayOption(this);
    }

    public void consume(Iterator<String> arguments) throws ParsingException {
        if (takesArgument() && noMore(arguments)) throw new ArgumentMissingException(this);
        value = takesArgument() ? convert(arguments.next()) : SWITCH_ON;
    }

    private boolean noMore(Iterator<String> arguments) {
        return !arguments.hasNext();
    }

    private Object convert(String value) throws InvalidArgumentException {
        try {
            return typeCoercer.convert(value);
        } catch (Exception e) {
            throw new InvalidArgumentException(name, value, e);
        }
    }

    public interface Action {
        public static final Action NOTHING = new NoOp();

        public static class NoOp implements Action {
            public void call(Option option) {
            }
        }

        void call(Option option);
    }
}

