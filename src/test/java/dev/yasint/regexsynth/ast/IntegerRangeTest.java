package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.Numeric.integerRange;
import static org.junit.Assert.assertTrue;

public final class IntegerRangeTest {

    @Test
    public void itShouldReturnExpectedRange() {
        int start = 65555, end = 78000;
        Pattern expression = new RegexSynth(integerRange(start, end)).compile();
        for (int i = start; i <= end; i++) {
            assertTrue(expression.matches(String.valueOf(i)));
        }
    }

}
