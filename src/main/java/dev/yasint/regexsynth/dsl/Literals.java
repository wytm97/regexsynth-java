package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Expression;
import dev.yasint.regexsynth.unicode.UnicodeScript;

import java.util.Objects;

import static dev.yasint.regexsynth.core.RegexConstructs.*;
import static dev.yasint.regexsynth.util.Common.asRegexLiteral;

public final class Literals {

    /**
     * Treats a given string as a literal inside the regular
     * expression. All the reserved characters will be escaped
     * using the backslash character.
     *
     * @param literals some string
     * @return escaped literal
     */
    public static Expression literal(final String literals) {
        return () -> new StringBuilder(
                asRegexLiteral(Objects.requireNonNull(literals))
        );
    }

    /**
     * Encloses a given string in quoted literals using the regex
     * construct \Q...\E The whole string will be treated as a
     * string literal.
     *
     * @param literals some string or text
     * @return strict quoted literal string or text
     */
    public static Expression quotedLiteral(final String literals) {
        return () -> new StringBuilder()
                .append(QUOTE_START)
                .append(Objects.requireNonNull(literals))
                .append(QUOTE_END);
    }

    /**
     * Appends the given unicode block to the expression. It can only be
     * one of the values from the {@link UnicodeScript}. And you can negate
     * the unicode class by specifying the negated to {@code true} or {@code false}.
     *
     * @param block   unicode general category block / script block
     * @param negated whether the block is negated or not
     * @return unicode character block
     */
    public static Expression unicodeClass(final UnicodeScript block, final boolean negated) {
        final String blockName = Objects.requireNonNull(block).getBlock();
        final StringBuilder expression = new StringBuilder();
        if (blockName.length() == 1) {
            expression
                    .append(negated ? "\\P" : "\\p")
                    .append(blockName);
        } else {
            expression
                    .append(negated ? "\\P" : "\\p")
                    .append(OPEN_CURLY_BRACE)
                    .append(blockName)
                    .append(CLOSE_CURLY_BRACE);
        }
        return () -> expression;
    }

}
