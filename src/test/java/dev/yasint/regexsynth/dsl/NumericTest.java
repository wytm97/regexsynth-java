package dev.yasint.regexsynth.dsl;

import dev.yasint.regexsynth.api.Expression;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.Numeric.integerRange;
import static dev.yasint.regexsynth.dsl.Numeric.leadingZero;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class NumericTest {

    @Test
    public void itShouldHandleSmallIntegerRanges() {
        assertEquals(integerRange(1, 10).toRegex().toString(), "(?:10|[1-9])");
        assertEquals(integerRange(1, 100).toRegex().toString(), "(?:100|[1-9][0-9]|[1-9])");
    }

    @Test
    public void itShouldHandlePreciseIntegerCases() {
        assertEquals(integerRange(1, 25675).toRegex().toString(),
                "(?:2567[0-5]|256[0-6][0-9]|25[0-5][0-9]{2}|2[0-4][0-9]{3}|" +
                        "1[0-9]{4}|[1-9][0-9]{3}|[1-9][0-9]{2}|[1-9][0-9]|[1-9])"
        );
    }

    @Test
    public void itShouldHandleRelativelyLargeIntegers() {
        Expression expression = integerRange(0, 999_999_999); // MAX
        assertEquals(expression.toRegex().toString(),
                "(?:[1-9][0-9]{8}|[1-9][0-9]{7}|[1-9][0-9]{6}|[1-9][0-9]{5}|[1-9]" +
                        "[0-9]{4}|[1-9][0-9]{3}|[1-9][0-9]{2}|[1-9][0-9]|[0-9])"
        );
    }

    @Test
    public void itShouldAddALeadingZeroToANumberOrARange() {
        assertEquals(leadingZero(integerRange(1, 12))
                .toRegex().toString(), "(?:0?(?:1[0-2]|[1-9]))");
    }

}
