package com.rique.sodiumfpscapfix;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FpsCapSupportTest {
    @Test
    void clampRespectsBounds() {
        assertEquals(10, FpsCapSupport.clamp(Integer.MIN_VALUE));
        assertEquals(10, FpsCapSupport.clamp(0));
        assertEquals(10, FpsCapSupport.clamp(9));
        assertEquals(10, FpsCapSupport.clamp(10));
        assertEquals(11, FpsCapSupport.clamp(11));
        assertEquals(1217, FpsCapSupport.clamp(1217));
        assertEquals(999_999, FpsCapSupport.clamp(999_999));
        assertEquals(1_000_000, FpsCapSupport.clamp(1_000_000));
        assertEquals(1_000_000, FpsCapSupport.clamp(1_000_001));
        assertEquals(1_000_000, FpsCapSupport.clamp(Integer.MAX_VALUE));
    }

    @Test
    void parseAcceptsNormalAndBoundaryValues() {
        assertEquals(10, FpsCapSupport.parseAndClamp("9"));
        assertEquals(10, FpsCapSupport.parseAndClamp("10"));
        assertEquals(11, FpsCapSupport.parseAndClamp("11"));
        assertEquals(1217, FpsCapSupport.parseAndClamp("1217"));
        assertEquals(999_999, FpsCapSupport.parseAndClamp("999999"));
        assertEquals(1_000_000, FpsCapSupport.parseAndClamp("1000000"));
        assertEquals(1_000_000, FpsCapSupport.parseAndClamp("1000001"));
    }

    @Test
    void parseHandlesLeadingZeroesAndOverflow() {
        assertEquals(42, FpsCapSupport.parseAndClamp("00042"));
        assertEquals(10, FpsCapSupport.parseAndClamp("0000000000"));
        assertEquals(10, FpsCapSupport.parseAndClamp(""));
        assertEquals(1_000_000, FpsCapSupport.parseAndClamp("100000000000000000000000"));
        assertEquals(1_000_000, FpsCapSupport.parseAndClamp("999999999999"));
    }

    @Test
    void parseRejectsNonDigitInput() {
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("abc"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("12a3"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("-1"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("+5"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp(" 10"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("10 "));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("1.5"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("1e5"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("0x10"));
        assertThrows(NumberFormatException.class, () -> FpsCapSupport.parseAndClamp("١٢٣"));
    }
}
