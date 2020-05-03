package dev.yasint.regexsynth.dsl;

        import dev.yasint.regexsynth.api.Expression;
        import dev.yasint.regexsynth.exceptions.NumericRangeException;
        import dev.yasint.regexsynth.synthesis.RangeExpression;

        import static dev.yasint.regexsynth.api.MetaCharacters.QUESTION_MARK;
        import static dev.yasint.regexsynth.dsl.CharClasses.rangedSet;
        import static dev.yasint.regexsynth.dsl.Groups.nonCaptureGroup;

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
        return nonCaptureGroup(() -> another.toRegex().insert(0, "0" + QUESTION_MARK));
    }

    /**
     * Creates a ranged integer based on from and to values inclusively.
     * The resulting expression is wrapped around a non-capturing group
     * to avoid condition collisions.
     *
     * @param from starting integer MIN_INT = 0
     * @param to   ending integer MAX_INT = 10^9 - 1
     * @return range expression
     */
    public static Expression integerRange(final int from, final int to) {
        if (from > to)
            throw new NumericRangeException("integer range is out of order");
        if (from == to)
            return Literals.literal(String.valueOf(from));
        if (from >= 0 && to <= 9)
            return rangedSet(String.valueOf(from), String.valueOf(to));
        return nonCaptureGroup(new RangeExpression(from, to));
    }

}
