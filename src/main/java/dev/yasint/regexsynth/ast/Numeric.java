package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Expression;

import static dev.yasint.regexsynth.ast.CharClasses.rangedSet;
import static dev.yasint.regexsynth.ast.Groups.nonCaptureGroup;
import static dev.yasint.regexsynth.core.Constructs.QUESTION_MARK;

public final class Numeric {

    /**
     * Appends a optional zero to any expression where usually this is
     * used with digits/ranges.
     *
     * @param another expression
     * @return expression with optional leading zero
     */
    public static Expression leadingZero(final Expression another) {
        // Wrap in non capture group to avoid expression collisions.
        // Insert the leading zero and append the zero or one quantifier
        return nonCaptureGroup(
                () -> another.toRegex().insert(0, "0" + QUESTION_MARK)
        );
    }

    /**
     * Creates a ranged integer based on from and to values inclusively.
     * The resulting expression is wrapped around a non-capturing group
     * to avoid condition collisions.
     *
     * @param from starting integer
     * @param to   ending integer
     * @return range expression
     */
    public static Expression integerRange(final int from, final int to) {
        if (from > to) throw new RuntimeException("integer range is out of order");
        if (from == to) return Literals.literal(String.valueOf(from));
        if (from >= 0 && to <= 9) return rangedSet(String.valueOf(from), String.valueOf(to));
        return nonCaptureGroup(() -> new StringBuilder(
                new IntegerRange(from, to).create()
        ));
    }

    private static Expression binary() {
        throw new UnsupportedOperationException();
    }

    private static Expression octal() {
        throw new UnsupportedOperationException();
    }

    private static Expression hex() {
        throw new UnsupportedOperationException();
    }

}
