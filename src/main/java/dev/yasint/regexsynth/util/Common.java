package dev.yasint.regexsynth.util;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.exceptions.InvalidGroupNameException;
import dev.yasint.regexsynth.synthesis.SetExpression;

public final class Common {

    // This regular expression checks whether the capture group name
    // variable is valid. It cannot contain the following characters
    // in the negated set. Also, the length must be 1...15 in range.
    //
    private static final Pattern VALID_GROUP_NAME = Pattern.compile(
            "^[^[:punct:][:digit:][:space:]]\\w{1,15}$"
    );

    // This regular expression matches all the special reserved chars.
    //
    private static final Pattern RESERVED = Pattern.compile(
            "[<(\\[{\\\\^\\-=$!|\\]})?*+.>/]"
    );

    // Utility methods

    /**
     * Escapes all the special regex constructs. {@code VALID_GROUP_NAME}
     * i.e. <code>https://</code> will transform to <code>https:\/\/</code>
     *
     * @param someString string to escape
     * @return escaped strings
     */
    public static String asRegexLiteral(final String someString) {
        int codepoint = toCodepoint(someString);
        if (Character.isSupplementaryCodePoint(codepoint)) {
            return String.format("\\x{%s}", Integer.toHexString(codepoint));
        }
        return RESERVED.matcher(someString).replaceAll("\\\\$0");

    }

    /**
     * This method checks whether a string qualifies to be a regex
     * named capture group. It's same as source code variable
     * declarations.
     *
     * @param name capture group name
     * @return the same string if it's valid
     */
    public static String asRegexGroupName(final String name) {
        boolean valid = VALID_GROUP_NAME.matcher(name).matches();
        if (!valid) throw new InvalidGroupNameException("invalid capture group name");
        return name;
    }

    /**
     * Converts a given character to its codepoint value. If the
     * the character is an empty string the min value (NULL) will
     * be returned.
     *
     * @param character as a string (supplementary/bmp) char
     * @return codepoint or NULL
     */
    public static int toCodepoint(final String character) {
        if (character.length() > 0) {
            return character.codePointAt(0);
        }
        return Character.MIN_VALUE;
    }

    public static boolean isNotASetExpression(final Expression target) {
        return !(target instanceof SetExpression);
    }

}
