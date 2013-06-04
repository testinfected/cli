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

package test.unit.org.testinfected.cli.util;

import org.testinfected.cli.util.Strings;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {
    @Test public void
    recognizesEmptyStrings() {
        assertTrue(Strings.empty(""));
        assertFalse(Strings.empty("obviously not"));
    }

    @Test public void
    considersNullAsEmpty() {
        assertTrue(Strings.empty(null));
    }

    @Test public void recognizesBlankStrings() {
        assertTrue(Strings.blank("  "));
        assertFalse(Strings.blank("obviously not"));
    }

    @Test public void
    doesNotConfuseBlankAndEmpty() {
        assertFalse(Strings.empty("  "));
    }

    @Test public void
    considersNullAsBlankString() {
        assertTrue(Strings.blank(null));
    }
}
