package hk.edu.polyu.comp.comp2021.clevis.model.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A utility class providing functionality to format double values into strings with a fixed scale.
 * This class cannot be instantiated.
 */
public final class DoubleFormatter {
    /**
     * Private constructor to prevent instantiation of this utility class.
     * Throws an AssertionError if an attempt is made to instantiate.
     */
    private DoubleFormatter() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Formats a double value into a string with exactly 2 decimal places, using HALF_UP rounding mode.
     *
     * @param value the double value to be formatted
     * @return a string representation of the input value rounded to 2 decimal places
     * @throws NumberFormatException if the input value is NaN or infinite
     */
    public static String format(double value) {
        if(Double.isNaN(value)) {
            throw new NumberFormatException("Cannot format NaN value");
        }
        if(Double.isInfinite(value)) {
            throw new NumberFormatException("Cannot format infinite value");
        }

        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
    }
}