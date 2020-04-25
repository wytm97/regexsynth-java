package dev.yasint.regexsynth.dsl;

import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.exceptions.QuantifierException;

import java.util.Objects;

import static dev.yasint.regexsynth.api.MetaCharacters.*;
import static dev.yasint.regexsynth.dsl.Groups.nonCaptureGroup;

public final class Repetition {

    /**
     * Appends a one or more times (greedy) quantifier to a expression (+)
     * <code>[0-9]+</code> means a number between 0 to 9 one or many times.
     *
     * @param expression repetition of what?
     * @return quantified expression
     */
    public static Expression oneOrMoreTimes(final Expression expression) {
        if (expression instanceof GreedyQuantifier || expression instanceof ReluctantQuantifier) {
            throw new QuantifierException("cannot apply + because it's already quantified");
        }
        return (GreedyQuantifier) () -> nonCaptureGroup(
                Objects.requireNonNull(expression)
        ).toRegex().append(PLUS);
    }

    /**
     * Appends a zero or more times (greedy) quantifier to a expression (*)
     * <code>[0-9]*</code> means a number between 0 to 9 zero or many times.
     *
     * @param expression repetition of what?
     * @return quantified expression
     */
    public static Expression zeroOrMoreTimes(final Expression expression) {
        if (expression instanceof GreedyQuantifier || expression instanceof ReluctantQuantifier) {
            throw new QuantifierException("cannot apply * because it's already quantified");
        }
        return (GreedyQuantifier) () -> nonCaptureGroup(
                Objects.requireNonNull(expression)
        ).toRegex().append(ASTERISK);
    }

    /**
     * Appends a exactly or more (greedy) quantifier to a expression ({2,}).
     * <code>{2,}</code> means exactly 2 times or more but not 1 times or 0
     * times
     *
     * @param times      a number larger than 1
     * @param expression repetition of what?
     * @return quantified expression
     */
    public static Expression exactlyOrMoreTimes(final int times, final Expression expression) {
        // Invalid arguments validation
        if (expression instanceof GreedyQuantifier || expression instanceof ReluctantQuantifier) {
            throw new QuantifierException("cannot apply {n,} because it's already quantified");
        } else if (times > 1000) {
            throw new QuantifierException("max repetition is 1000");
        }
        if (times == 0) return zeroOrMoreTimes(expression);
        if (times == 1) return oneOrMoreTimes(expression);
        return (GreedyQuantifier) () -> nonCaptureGroup(
                Objects.requireNonNull(expression)
        ).toRegex()
                .append(OPEN_CURLY_BRACE)
                .append(times).append(COMMA) // {3,}
                .append(CLOSE_CURLY_BRACE);
    }

    /**
     * Appends a one or not at all quantifier to a expression (?).
     * <code>abc?</code> means "a" followed by "b" and optional "c"
     *
     * @param expression optional of what?
     * @return quantified expression
     */
    public static Expression optional(final Expression expression) {
        if (expression instanceof GreedyQuantifier || expression instanceof ReluctantQuantifier) {
            throw new QuantifierException("cannot apply ? because it's already quantified");
        }
        return (GreedyQuantifier) () -> nonCaptureGroup(
                Objects.requireNonNull(expression)
        ).toRegex().append(QUESTION_MARK); // ?
    }

    /**
     * Appends a exactly (n) quantifier to a expression ({6}).
     * <code>(?:abc){5}</code> means abc exactly 5 times repeated.
     *
     * @param times      exactly how many times?
     * @param expression repetition of what?
     * @return quantified expression
     */
    public static Expression exactly(final int times, final Expression expression) {
        // Invalid arguments validation
        if (expression instanceof GreedyQuantifier || expression instanceof ReluctantQuantifier) {
            throw new QuantifierException("cannot apply {n} because it's already quantified");
        } else if (times == 0) {
            throw new QuantifierException("redundant sub-sequence");
        } else if (times == 1) {
            throw new QuantifierException("redundant quantifier");
        } else if (times > 1000) {
            throw new QuantifierException("max repetition is 1000");
        }
        return (GreedyQuantifier) () -> nonCaptureGroup(Objects.requireNonNull(expression))
                .toRegex()
                .append(OPEN_CURLY_BRACE)
                .append(times) // i.e. {3} exactly
                .append(CLOSE_CURLY_BRACE);
    }

    /**
     * Appends a between (n,m) quantifier to a expression ({2,4}).
     * <code>(?:abc){2,4}</code> means abc exactly 2 to 4 times repeated.
     *
     * @param m          starting range inclusive
     * @param n          ending range inclusive
     * @param expression repetition of what?
     * @return quantified expression
     */
    public static Expression between(final int m, final int n, final Expression expression) {
        // Invalid arguments validation
        if (expression instanceof GreedyQuantifier || expression instanceof ReluctantQuantifier) {
            throw new QuantifierException("cannot apply {m,n} because it's already quantified");
        } else if (m > 1000 || n > 1000) {
            throw new QuantifierException("max repetition is {1,1000}");
        } else if (m > n) {
            throw new QuantifierException("range is out of order");
        } else if (m == 0 && n == 0) {
            throw new QuantifierException("redundant sub-sequence");
        } // below is default
        // Optimizations for the quantifiers
        if (m == 0 && n == 1) return optional(expression);
        if (m == 1 && n == 1) return expression;
        if (m == n) return exactly(m, expression);
        return (GreedyQuantifier) () -> nonCaptureGroup(
                Objects.requireNonNull(expression)
        ).toRegex()
                .append(OPEN_CURLY_BRACE)
                .append(m).append(COMMA).append(n)
                .append(CLOSE_CURLY_BRACE);
    }

    /**
     * Makes a quantifier explicitly lazy. <code>*? ?? +? {x,}?</code>
     * This is an advanced operation. Use it on your own discretion.
     *
     * @param expression quantified expression
     * @return lazy-ly quantified expression.
     */
    public static Expression lazy(final Expression expression) {
        if (expression instanceof ReluctantQuantifier) {
            throw new QuantifierException("already marked as lazy");
        }
        if (!(expression instanceof GreedyQuantifier)) {
            throw new QuantifierException("must be a greedy quantifier");
        }
        return (ReluctantQuantifier) () -> expression
                .toRegex()
                .append(QUESTION_MARK);
    }

    // Typed interfaces for checking quantifying errors.

    public interface GreedyQuantifier extends Expression { }

    public interface ReluctantQuantifier extends Expression { }

}
