package com.vtence.cli.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringsTest {

    @Test public void
    recognizesEmptyStrings() {
        assertTrue(Strings.empty(""));
        assertFalse(Strings.empty("obviously not"));
    }

    @Test public void
    considersNullAsEmpty() {
        assertTrue(Strings.empty(null));
    }

    @Test public void
    recognizesBlankStrings() {
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

    @Test public void
    quotesStrings() {
        assertEquals("`quoted'", Strings.quote("quoted"));
    }

    @Test public void
    perfect() {
        new Strings();
    }
}
