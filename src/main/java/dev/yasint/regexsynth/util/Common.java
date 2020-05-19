package dev.yasint.regexsynth.util;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.exceptions.InvalidGroupNameException;
import dev.yasint.regexsynth.synthesis.SetExpression;

import static dev.yasint.regexsynth.api.MetaCharacters.BACKSLASH;

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
     * Escapes all the special regex constructs. {@code RESERVED}
     * i.e. <code>https://</code> will transform to <code>https:\/\/</code>
     *
     * @param someString string to escape
     * @return escaped strings
     */
    public static String asRegexLiteral(final String someString) {
        if (someString.length() == 1) { // quickly return if its only 1 char
            return RESERVED.matcher(someString).replaceAll("\\\\$0");
        }
        final StringBuilder composed = new StringBuilder();
        for (int i = 0; i < someString.toCharArray().length; i++) {
            int codepoint = someString.codePointAt(i); // always returns a complete codepoint
            if (Character.isSupplementaryCodePoint(codepoint)) {
                composed.append(String.format("\\x{%s}", Integer.toHexString(codepoint)));
                i++; // Skip the next character because it's an high-surrogate
            } else { // definitely an BMP codepoint character
                String val = String.valueOf(someString.charAt(i));
                composed.append(RESERVED.matches(val) ? BACKSLASH : "").append(val);
            }
        }
        return composed.toString();
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
