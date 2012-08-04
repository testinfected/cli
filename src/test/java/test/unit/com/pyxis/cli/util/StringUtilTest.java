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

package test.unit.com.pyxis.cli.util;

import com.pyxis.cli.util.StringUtil;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {
    @Test public void
    recognizesEmptyStrings() {
        assertTrue(StringUtil.isEmpty(""));
        assertFalse(StringUtil.isEmpty("obviously not"));
    }

    @Test public void
    considersNullAsEmpty() {
        assertTrue(StringUtil.isEmpty(null));
    }

    @Test public void recognizesBlankStrings() {
        assertTrue(StringUtil.isBlank("  "));
        assertFalse(StringUtil.isBlank("obviously not"));
    }

    @Test public void
    doesNotConfuseBlankAndEmpty() {
        assertFalse(StringUtil.isEmpty("  "));
    }

    @Test public void
    considersNullAsBlankString() {
        assertTrue(StringUtil.isBlank(null));
    }
}
