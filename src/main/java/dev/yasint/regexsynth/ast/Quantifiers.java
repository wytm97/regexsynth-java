package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Expression;

import java.util.Objects;

import static dev.yasint.regexsynth.core.Constructs.*;

public final class Quantifiers {

    /**
     * Appends a one or more times (greedy) quantifier to a expression (+)
     * <code>[0-9]+</code> means a number between 0 to 9 one or many times.
     *
     * @param expression repetition of what?
     * @return quantified expression
     */
    public static Expression oneOrMoreTimes(final Expression expression) {
        return () -> Objects.requireNonNull(expression)
                .toRegex()
                .append(PLUS);
    }

    /**
     * Appends a zero or more times (greedy) quantifier to a expression (*)
     * <code>[0-9]*</code> means a number between 0 to 9 zero or many times.
     *
     * @param expression repetition of what?
     * @return quantified expression
     */
    public static Expression zeroOrMoreTimes(final Expression expression) {
        return () -> Objects.requireNonNull(expression)
                .toRegex()
                .append(ASTERISK);
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
        if (times == 0) return zeroOrMoreTimes(expression);
        if (times == 1) return oneOrMoreTimes(expression);
        return () -> Objects.requireNonNull(expression)
                .toRegex()
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
        return () -> Objects.requireNonNull(expression)
                .toRegex()
                .append(QUESTION_MARK);
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
        if (times == 0) { // Causes the token to be ignored so inform the user,
            throw new RuntimeException("redundant sub-sequence");
        }
        if (times == 1) { // Redundant quantifier
            throw new RuntimeException("redundant quantifier");
        }
        return () -> Objects.requireNonNull(expression)
                .toRegex()
                .append(OPEN_CURLY_BRACE)
                .append(times)
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
        if (m > n) throw new RuntimeException("quantifier range is out of order");
        if (m == 0 && n == 0) throw new RuntimeException("redundant sub-sequence");
        if (m == 0 && n == 1) return optional(expression);
        if (m == 1 && n == 1) return expression;
        return () -> Objects.requireNonNull(expression).toRegex()
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
        return optional(expression);
    }

}
