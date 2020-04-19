package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Expression;
import dev.yasint.regexsynth.core.RegexSynth;

import java.util.Arrays;
import java.util.Objects;

import static dev.yasint.regexsynth.core.Constructs.*;
import static dev.yasint.regexsynth.core.Utility.asRegexGroupName;

/**
 * Contains all the grouping constructs supported by the
 * RE2 regex engine.
 *
 * @implNote All are pure functions
 * @since 1.0.0
 */
public final class Groups {

    /**
     * Creates a non capturing group. You can use this to group
     * and to avoid unnecessary capturing, and the match
     * process will be more efficient. As the name implies it will
     * not include in the final result.
     *
     * @param expressions sub-expression of this group
     * @return non-capturing group.
     */
    public static Expression nonCaptureGroup(final Expression... expressions) {
        return () -> Arrays.stream(Objects.requireNonNull(expressions))
                .map(Expression::toRegex)
                .reduce(
                        new StringBuilder()
                                .append(PAREN_OPEN)
                                .append(QUESTION_MARK)
                                .append(COLON),
                        StringBuilder::append
                ).append(PAREN_CLOSE);
    }

    /**
     * Creates a capturing group. By using this you can group that part of
     * the regular expression together. This allows you to apply a quantifier
     * to a entire group or to restrict alternation to part of the regex.
     *
     * @param expressions sub-expressions of this group
     * @return capturing group
     */
    public static Expression captureGroup(final Expression... expressions) {
        return () -> Arrays.stream(Objects.requireNonNull(expressions))
                .map(Expression::toRegex)
                .reduce(
                        new StringBuilder().append(PAREN_OPEN),
                        StringBuilder::append
                ).append(PAREN_CLOSE);
    }

    /**
     * Creates a named capturing group. This is very much like the capturing
     * group. it allows you to specify a capture group in the matched result.
     * But also with a name to the group <code>(?P&lt;name&gt;...)</code>
     *
     * @param name        name of this capturing group
     * @param expressions sub-expressions of this group
     * @return named capturing group
     */
    public static Expression namedCaptureGroup(final String name, final Expression... expressions) {
        return () -> Arrays.stream(Objects.requireNonNull(expressions))
                .map(Expression::toRegex)
                .reduce(
                        new StringBuilder()
                                .append(PAREN_OPEN)
                                .append(QUESTION_MARK)
                                .append(NAMED_CAPTURE_GROUP_PREFIX)
                                .append(LESS_THAN)
                                .append(asRegexGroupName(name))
                                .append(GREATER_THAN),
                        StringBuilder::append
                ).append(PAREN_CLOSE);
    }

    /**
     * TODO: in a future release
     *
     * @param modifiers *
     * @return *
     */
    private static Expression withInlineModifiers(final RegexSynth.Flags... modifiers) {
        // (?misU-misU:...) => default is -misU then add flags
        // before - separator. This should support remainder of the pattern
        // style where if you specify just (?misU) pattern applies
        // it to the rest but can be overriden by nested-capturing groups.
        throw new UnsupportedOperationException();
    }

}
