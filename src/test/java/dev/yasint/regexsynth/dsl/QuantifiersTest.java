package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.CharClasses.Posix.digit;
import static dev.yasint.regexsynth.ast.Quantifiers.*;
import static org.junit.Assert.assertEquals;

public final class QuantifiersTest {

    @Test
    public void itShouldAppendOneOrMoreTimesQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                oneOrMoreTimes(digit())
        ).compile();
        assertEquals(expression.pattern(), "(?:[0-9])+");
    }

    @Test
    public void itShouldAppendZeroOrMoreTimesQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                zeroOrMoreTimes(digit())
        ).compile();
        assertEquals(expression.pattern(), "(?:[0-9])*");
    }

    @Test
    public void itShouldAppendExactlyOrMoreTimesQuantifierToExpression() {
        Pattern expression;
        expression = new RegexSynth(exactlyOrMoreTimes(2, digit())).compile();
        assertEquals(expression.pattern(), "(?:[0-9]){2,}");
        expression = new RegexSynth(exactlyOrMoreTimes(0, digit())).compile();
        assertEquals(expression.pattern(), "(?:[0-9])*");
        expression = new RegexSynth(exactlyOrMoreTimes(1, digit())).compile();
        assertEquals(expression.pattern(), "(?:[0-9])+");
    }

    @Test
    public void itShouldAppendOptionalQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                optional(digit())
        ).compile();
        assertEquals(expression.pattern(), "(?:[0-9])?");
    }

    @Test
    public void itShouldAppendExactlyNQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                exactly(5, digit())
        ).compile();
        assertEquals(expression.pattern(), "(?:[0-9]){5}");
    }

    @Test
    public void itShouldAppendBetweenQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                between(5, 10, digit())
        ).compile();
        assertEquals(expression.pattern(), "(?:[0-9]){5,10}");
    }

    @Test
    public void itShouldAppendLazyQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                lazy(between(5, 10, digit()))
        ).compile();
        assertEquals(expression.pattern(), "(?:[0-9]){5,10}?");
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowAnExceptionWhenExactlyQuantifierIsRedundant() {
        new RegexSynth(exactly(1, digit())).compile();
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowAnExceptionWhenExactlyQuantifierAppliedExpressionIsRedundant() {
        new RegexSynth(exactly(0, digit())).compile();
    }

}
