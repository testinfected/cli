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

import org.testinfected.cli.ParsingException;

public class ArgumentMissingException extends ParsingException
{
    private final Option option;

    public ArgumentMissingException(Option option) {
        this.option = option;
    }

    public String getUnsatisfiedOption() {
        return option.getName();
    }

    public String getMessage() {
        return String.format("option %s expects an argument", option.getName());
    }
}
