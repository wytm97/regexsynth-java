package ast;

import com.google.re2j.Pattern;
import core.RegexSynth;
import org.junit.Test;

import static ast.CharClasses.Posix.*;
import static ast.Operators.concat;
import static ast.Operators.either;
import static core.RegexSynth.regexp;
import static org.junit.Assert.assertEquals;

public final class OperatorsTest {

    @Test
    public void itShouldCreateAlternationBetweenMultipleExpressions() {
        String regexp = regexp(
                either(digit(), uppercaseChar(), lowercaseChar())
        );
        Pattern pattern = RegexSynth.compile(regexp);
        assertEquals(pattern.pattern(), "[0-9]|[A-Z]|[a-z]");
    }

    @Test
    public void itShouldCreateAlternationBetweenMultipleStrings() {
        String regexp = regexp(
                either("http", "https", "ws", "wss")
        );
        Pattern pattern = RegexSynth.compile(regexp);
        assertEquals(pattern.pattern(), "(?:https?|wss?)");
    }

    @Test
    public void itShouldConcatMultipleExpressionsIntoOne() {
        String regexp = regexp(
                concat(
                        digit(),
                        punctuationChar()
                )
        );
        Pattern pattern = RegexSynth.compile(regexp);
        assertEquals(pattern.pattern(), "[0-9][!-\\/:-@[-`{-~]");
    }

}
