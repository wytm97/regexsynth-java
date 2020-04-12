package ast;

import core.Expression;
import core.RegexSynth;
import org.junit.Test;

import static ast.Numeric.integerRange;
import static ast.Numeric.leadingZero;
import static org.junit.Assert.assertEquals;

public final class NumericTest {

    @Test
    public void itShouldHandleSmallIntegerRanges() {
        Expression expression;
        expression = integerRange(1, 10);
        assertEquals(expression.toRegex().toString(), "(?:10|[1-9])");
        RegexSynth.compile(RegexSynth.regexp(expression));
        expression = integerRange(1, 100);
        assertEquals(expression.toRegex().toString(), "(?:100|[1-9][0-9]|[1-9])");
        RegexSynth.compile(RegexSynth.regexp(expression));
    }

    @Test
    public void itShouldHandlePreciseIntegerCases() {
        Expression expression = integerRange(1, 25675);
        assertEquals(expression.toRegex().toString(),
                "(?:2567[0-5]|256[0-6][0-9]|25[0-5][0-9][0-9]" +
                        "|2[0-4][0-9][0-9][0-9]|1[0-9][0-9][0-9][0-9]|[1-9][0-9][0-9]" +
                        "[0-9]|[1-9][0-9][0-9]|[1-9][0-9]|[1-9])"
        );
        RegexSynth.compile(RegexSynth.regexp(expression));
    }

    @Test
    public void itShouldHandleRelativelyLargeIntegers() {
        Expression expression = integerRange(0, 999_999_999); // MAX
        assertEquals(expression.toRegex().toString(),
                "(?:[1-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9][0-9]|[1-9][0-9]" +
                        "[0-9][0-9][0-9][0-9][0-9][0-9]|[1-9][0-9][0-9][0-9][0-9]" +
                        "[0-9][0-9]|[1-9][0-9][0-9][0-9][0-9][0-9]|[1-9][0-9][0-9]" +
                        "[0-9][0-9]|[1-9][0-9][0-9][0-9]|[1-9][0-9][0-9]|[1-9][0-9]|[0-9])"
        );
        RegexSynth.compile(RegexSynth.regexp(expression));
    }

    @Test
    public void itShouldAddALeadingZeroToANumberOrARange() {
        Expression expression = leadingZero(integerRange(1, 12));
        RegexSynth.compile(RegexSynth.regexp(expression));
        assertEquals(expression.toRegex().toString(), "(?:0?(?:1[0-2]|[1-9]))");
    }

}
