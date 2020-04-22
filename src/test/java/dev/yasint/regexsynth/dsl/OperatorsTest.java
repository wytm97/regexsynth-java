package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.CharClasses.Posix.*;
import static dev.yasint.regexsynth.ast.Operators.concat;
import static dev.yasint.regexsynth.ast.Operators.either;
import static org.junit.Assert.assertEquals;

public final class OperatorsTest {

    @Test
    public void itShouldCreateAlternationBetweenMultipleExpressions() {
        Pattern pattern = new RegexSynth(
                either(digit(), uppercase(), lowercase())
        ).compile();
        assertEquals(pattern.pattern(), "(?:[0-9]|[A-Z]|[a-z])");
    }

    @Test
    public void itShouldCreateAlternationBetweenMultipleStrings() {
        Pattern pattern = new RegexSynth(
                either("http", "https", "ws", "wss")
        ).compile();
        assertEquals(pattern.pattern(), "(?:https?|wss?)");
    }

    @Test
    public void itShouldConcatMultipleExpressionsIntoOne() {
        Pattern pattern = new RegexSynth(
                concat(digit(), punctuation())
        ).compile();
        assertEquals(pattern.pattern(), "[0-9][!-\\/:-@[-\\`{-~]");
    }

}
