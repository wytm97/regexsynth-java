package dev.yasint.regexsynth.dsl;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.RegexSynth;
import dev.yasint.regexsynth.exceptions.QuantifierException;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.CharClasses.Posix.digit;
import static dev.yasint.regexsynth.dsl.Repetition.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public final class RepetitionTest {

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

    // Checking for syntax errors

    @Test
    public void itShouldNotAllowTwoFollowingQuantifiers() {

        assertEquals(
                assertThrows(
                        QuantifierException.class,
                        () -> zeroOrMoreTimes(optional(digit()))
                ).getMessage(),
                "cannot apply * because it's already quantified"
        );

        assertEquals(
                assertThrows(
                        QuantifierException.class,
                        () -> oneOrMoreTimes(zeroOrMoreTimes(digit()))
                ).getMessage(),
                "cannot apply + because it's already quantified"
        );

        assertEquals(
                assertThrows(
                        QuantifierException.class,
                        () -> exactlyOrMoreTimes(3, exactly(3, digit()))
                ).getMessage(),
                "cannot apply {n,} because it's already quantified"
        );

        assertEquals(
                assertThrows(
                        QuantifierException.class,
                        () -> optional(exactly(3, digit()))
                ).getMessage(),
                "cannot apply ? because it's already quantified"
        );

        assertEquals(
                assertThrows(
                        QuantifierException.class,
                        () -> between(3, 6, exactly(5, digit()))
                ).getMessage(),
                "cannot apply {m,n} because it's already quantified"
        );

    }

    @Test
    public void itShouldNotAppendLazyQuantifierIfGreedyQuantifierIsNotPresent() {
        assertEquals(
                assertThrows(
                        QuantifierException.class,
                        () -> lazy(digit())
                ).getMessage(),
                "must be a greedy quantifier"
        );
    }

    @Test
    public void itShouldNotAppendLazyQuantifierIfItsAlreadyAppended() {
        assertEquals(
                assertThrows(
                        QuantifierException.class,
                        () -> lazy(lazy(optional(digit())))
                ).getMessage(),
                "already marked as lazy"
        );
    }

    @Test
    public void itShouldNotAppendLazyQuantifierIfItsAlreadyAppended2() {
        assertThrows(
                QuantifierException.class,
                () -> oneOrMoreTimes(lazy(optional(digit())))
        );
        assertThrows(
                QuantifierException.class,
                () -> zeroOrMoreTimes(lazy(optional(digit())))
        );
        assertThrows(
                QuantifierException.class,
                () -> exactlyOrMoreTimes(4, lazy(optional(digit())))
        );
        assertThrows(
                QuantifierException.class,
                () -> optional(lazy(optional(digit())))
        );
        assertThrows(
                QuantifierException.class,
                () -> exactly(2, lazy(optional(digit())))
        );
        assertThrows(
                QuantifierException.class,
                () -> between(2, 4, lazy(optional(digit())))
        );
    }
    // Validation errors

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
