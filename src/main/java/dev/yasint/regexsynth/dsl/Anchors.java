package dev.yasint.regexsynth.dsl;

import dev.yasint.regexsynth.api.Expression;

import java.util.Arrays;

import static dev.yasint.regexsynth.api.MetaCharacters.*;

/**
 * This class contains all the anchors/boundary matchers that
 * RE2 regex engine supports.
 *
 * @since 1.0.0
 */
public final class Anchors {

    /**
     * Inserts a word boundary at position. There are different
     * positions that qualify as a word boundary.
     * <p>
     * First, it can be used before the first character in the string,
     * if the first character is a word character. Second, after the
     * last character in the string, if the last character is a word
     * character. Third, between two characters in the string, where
     * one is a word character and the other is not a word character.
     *
     * @return boundary expression \b
     */
    public static Expression wordBoundary() {
        return () -> new StringBuilder(2).append(WORD_BOUNDARY);
    }

    /**
     * Inserts a negated word boundary at position. \B matches at any
     * position between two word characters as well as at any position
     * between two non-word characters.
     *
     * @return non-boundary expression \B
     */
    public static Expression nonWordBoundary() {
        return () -> new StringBuilder(2).append(NON_WORD_BOUNDARY);
    }

    /**
     * Inserts a start of line character at position. When we use an anchor
     * in the search expression, the engine does not advance through the
     * string or consume characters; it looks for a match in the specified
     * position only. i.e. ^http: matches "http:" only when it occurs at
     * the beginning of a line; else the engine will return immediately.
     *
     * @return expression start of line ^
     */
    public static Expression startOfLine() {
        return () -> new StringBuilder(1).append(CARAT);
    }

    /**
     * Inserts a end of line character at position. Use `$` with the multiline
     * option, the match can also occur at the end of a line. `$` matches `\n`
     * but does not match `\r\n`. However, you can get the functionality of
     * crlf (Carriage Return - Line Feed) using boolean flag setting to true.
     *
     * @param crlf \r\n combination, appended \r before \n if true.
     * @return expression
     */
    public static Expression endOfLine(final boolean crlf) {
        return () -> new StringBuilder(6)
                // appends \r if crlf is true.
                .append(crlf ? "\\x0D?" : "").append(DOLLAR);
    }

    /**
     * Inserts a start of text/string assertion at the position. This
     * only ever matches at the start of the string and never matches
     * at line breaks. However, \A is different from ^. Because ^ and $
     * match up until a newline character.
     *
     * @return expression \A absolute start
     */
    public static Expression startOfText() {
        return () -> new StringBuilder(2).append(BEGINNING_OF_TEXT);
    }

    /**
     * Inserts a end of text/string assertion at the position.
     * This function can be considered as a pure function.
     *
     * @return expression \z absolute end
     */
    public static Expression endOfText() {
        return () -> new StringBuilder(2).append(END_OF_TEXT);
    }

    /**
     * Appends a start of line and end of line anchors to an
     * expression. ^ some-other-expressions $
     *
     * @param expressions in-between expressions
     * @return wrapped expression ^...$
     */
    public static Expression exactLineMatch(final Expression... expressions) {
        return () -> Arrays.stream(expressions)
                .map(Expression::toRegex)
                .reduce(new StringBuilder().append(CARAT), StringBuilder::append)
                .append(DOLLAR);
    }

    /**
     * Wraps an expression with '\b' at start and end. If an
     * expression is wrapped around this, it ensures that the
     * regex does not match part of a longer word/expression.
     *
     * @param expressions to wrap.
     * @return new wrapped expression \b some-other-expression(s) \b
     */
    public static Expression exactWordBoundary(final Expression... expressions) {
        return () -> Arrays.stream(expressions)
                .map(Expression::toRegex)
                .reduce(new StringBuilder().append(WORD_BOUNDARY), StringBuilder::append)
                .append(WORD_BOUNDARY);
    }

}
