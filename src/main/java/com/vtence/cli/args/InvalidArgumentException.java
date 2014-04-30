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

public class InvalidArgumentException extends ParsingException
{
    private final String name;
    private final Object value;

    public InvalidArgumentException(String argument, Object value, Exception cause) {
        super(cause);
        this.name = argument;
        this.value = value;
    }

    public String getUnsatisfiedArgument() {
        return name;
    }

    public Object getOffendingValue() {
        return value;
    }

    public String getMessage() {
        return String.format("invalid %s `%s'", name, value);
    }
}
