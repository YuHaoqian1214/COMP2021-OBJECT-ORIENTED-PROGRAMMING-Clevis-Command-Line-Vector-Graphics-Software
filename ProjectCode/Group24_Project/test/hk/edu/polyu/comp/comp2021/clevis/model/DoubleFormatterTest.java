package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.util.DoubleFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DoubleFormatterTest {

    @Test
    @DisplayName("format: positive number with rounding down")
    void testFormatPositiveRoundingDown() {
        assertEquals("1.23", DoubleFormatter.format(1.234));
    }

    @Test
    @DisplayName("format: positive number with rounding up")
    void testFormatPositiveRoundingUp() {
        assertEquals("1.24", DoubleFormatter.format(1.235));
    }

    @Test
    @DisplayName("format: exact decimal")
    void testFormatExact() {
        assertEquals("1.23", DoubleFormatter.format(1.23));
    }

    @Test
    @DisplayName("format: negative number")
    void testFormatNegative() {
        assertEquals("-1.23", DoubleFormatter.format(-1.234));
    }

    @Test
    @DisplayName("format: zero")
    void testFormatZero() {
        assertEquals("0.00", DoubleFormatter.format(0.00));
    }

    @Test
    @DisplayName("format: throws NumberFormatException for NaN")
    void testFormatNaN() {
        assertThrows(NumberFormatException.class, () -> DoubleFormatter.format(Double.NaN));
    }

    @Test
    @DisplayName("format: throws NumberFormatException for positive infinity")
    void testFormatPositiveInfinity() {
        assertThrows(NumberFormatException.class, () -> DoubleFormatter.format(Double.POSITIVE_INFINITY));
    }

    @Test
    @DisplayName("format: throws NumberFormatException for negative infinity")
    void testFormatNegativeInfinity() {
        assertThrows(NumberFormatException.class, () -> DoubleFormatter.format(Double.NEGATIVE_INFINITY));
    }
}