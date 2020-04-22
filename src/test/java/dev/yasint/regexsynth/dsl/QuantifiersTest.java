package dev.yasint.regexsynth.dsl;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.RegexSynth;
import dev.yasint.regexsynth.exceptions.QuantifierException;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.CharClasses.Posix.digit;
import static dev.yasint.regexsynth.dsl.Quantifiers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class QuantifiersTest {

    @Test
    public void itShouldAppendOneOrMoreTimesQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                oneOrMoreTimes(digit())
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9])+");
    }

    @Test
    public void itShouldAppendZeroOrMoreTimesQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                zeroOrMoreTimes(digit())
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9])*");
    }

    @Test
    public void itShouldAppendExactlyOrMoreTimesQuantifierToExpression() {
        Pattern expression;
        expression = new RegexSynth(exactlyOrMoreTimes(2, digit())).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9]){2,}");
        expression = new RegexSynth(exactlyOrMoreTimes(0, digit())).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9])*");
        expression = new RegexSynth(exactlyOrMoreTimes(1, digit())).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9])+");
    }

    @Test
    public void itShouldAppendOptionalQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                optional(digit())
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9])?");
    }

    @Test
    public void itShouldAppendExactlyNQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                exactly(5, digit())
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9]){5}");
    }

    @Test
    public void itShouldAppendBetweenQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                between(5, 10, digit())
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9]){5,10}");
    }

    @Test
    public void itShouldAppendLazyQuantifierToExpression() {
        Pattern expression = new RegexSynth(
                lazy(between(5, 10, digit()))
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(?:[0-9]){5,10}?");
    }

    @Test()
    public void itShouldThrowAnExceptionWhenExactlyQuantifierIsRedundant() {
        Exception e = assertThrows(
                QuantifierException.class,
                () -> new RegexSynth(exactly(1, digit())).compile()
        );
        assertEquals(e.getMessage(), "redundant quantifier");
    }

    @Test()
    public void itShouldThrowAnExceptionWhenExactlyQuantifierAppliedExpressionIsRedundant() {
        Exception e = assertThrows(
                QuantifierException.class,
                () -> new RegexSynth(exactly(0, digit())).compile()
        );
        assertEquals(e.getMessage(), "redundant sub-sequence");
    }

}
