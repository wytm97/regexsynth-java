package dev.yasint.regexsynth.synthesis;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.RegexSynth;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.Numeric.integerRange;
import static org.junit.jupiter.api.Assertions.assertTrue;

public final class RangeExpressionTest {

    @Test
    public void itShouldReturnExpectedRange() {
        int start = 65555, end = 78000;
        Pattern expression = new RegexSynth(
                integerRange(start, end)
        ).compile().getPattern();
        for (int i = start; i <= end; i++) {
            assertTrue(expression.matches(String.valueOf(i)));
        }
    }

    @Test
    public void itShouldReturn100to1000Range() {

        RangeExpression e = new RangeExpression(1, 10000);
        StringBuilder builder = e.toRegex();
        System.out.println(builder);


    }

}
