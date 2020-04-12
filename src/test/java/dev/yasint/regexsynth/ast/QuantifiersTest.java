package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.CharClasses.Posix.digit;
import static dev.yasint.regexsynth.ast.Quantifiers.*;
import static dev.yasint.regexsynth.core.RegexSynth.regexp;
import static org.junit.Assert.assertEquals;

public final class QuantifiersTest {

    @Test
    public void itShouldAppendOneOrMoreTimesQuantifierToExpression() {
        Pattern expression = RegexSynth.compile(
                regexp(oneOrMoreTimes(digit()))
        );
        assertEquals(expression.pattern(), "[0-9]+");
    }

    @Test
    public void itShouldAppendZeroOrMoreTimesQuantifierToExpression() {
        Pattern expression = RegexSynth.compile(
                regexp(zeroOrMoreTimes(digit()))
        );
        assertEquals(expression.pattern(), "[0-9]*");
    }

    @Test
    public void itShouldAppendExactlyOrMoreTimesQuantifierToExpression() {
        Pattern expression;
        expression = RegexSynth.compile(regexp(exactlyOrMoreTimes(2, digit())));
        assertEquals(expression.pattern(), "[0-9]{2,}");
        expression = RegexSynth.compile(regexp(exactlyOrMoreTimes(0, digit())));
        assertEquals(expression.pattern(), "[0-9]*");
        expression = RegexSynth.compile(regexp(exactlyOrMoreTimes(1, digit())));
        assertEquals(expression.pattern(), "[0-9]+");
    }

    @Test
    public void itShouldAppendOptionalQuantifierToExpression() {
        Pattern expression = RegexSynth.compile(
                regexp(optional(digit()))
        );
        assertEquals(expression.pattern(), "[0-9]?");
    }

    @Test
    public void itShouldAppendExactlyNQuantifierToExpression() {
        Pattern expression = RegexSynth.compile(
                regexp(exactly(5, digit()))
        );
        assertEquals(expression.pattern(), "[0-9]{5}");
    }

    @Test
    public void itShouldAppendBetweenQuantifierToExpression() {
        Pattern expression = RegexSynth.compile(
                regexp(between(5, 10, digit()))
        );
        assertEquals(expression.pattern(), "[0-9]{5,10}");
    }

    @Test
    public void itShouldAppendLazyQuantifierToExpression() {
        Pattern expression = RegexSynth.compile(
                regexp(lazy(between(5, 10, digit())))
        );
        assertEquals(expression.pattern(), "[0-9]{5,10}?");
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowAnExceptionWhenExactlyQuantifierIsRedundant() {
        RegexSynth.compile(
                regexp(exactly(1, digit()))
        );
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowAnExceptionWhenExactlyQuantifierAppliedExpressionIsRedundant() {
        RegexSynth.compile(
                regexp(exactly(0, digit()))
        );
    }

}
