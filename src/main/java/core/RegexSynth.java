package core;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class RegexSynth {

    private RegexSynth() {
    }

    /**
     * Creates a complete regular expression. It combines
     * all the sub expressions into one.
     *
     * @param expressions sub-expressions
     * @return string representation of the expression
     */
    public static String regexp(final Expression... expressions) {
        return Arrays.stream(expressions)
                .map(Expression::toRegex)
                .collect(Collectors.joining());
    }

    /**
     * Compiles the created regular expression pattern into a
     * RE2 {@link Pattern} instance.
     *
     * @param expression final expression
     * @param flags      global modifiers
     * @return Re2J Pattern instance
     */
    public static Pattern compile(final String expression, final Flags... flags) {
        int fl = 0;
        for (final Flags flag : flags) fl += flag.val;
        return Pattern.compile(expression, fl);
    }

    /**
     * Creates a list of matched groups in the {@link Matcher}
     * instance. This is just a convenience function.
     *
     * @param matcher matched instance for {@link CharSequence}
     * @return match that maps to a group id
     */
    public static Map<Integer, String> getMatchedGroups(final Matcher matcher) {
        final Map<Integer, String> groups = new HashMap<>();
        while (matcher.find())
            for (int i = 1; i <= matcher.groupCount(); i++)
                groups.put(i, matcher.group(i));
        return groups;
    }

    public static enum Flags {

        // RE2 matches unicode by default

        CASE_INSENSITIVE(Pattern.CASE_INSENSITIVE), // false by default
        MULTILINE(Pattern.MULTILINE), // false by default
        DOTALL(Pattern.DOTALL), // false by default (matches \n)
        DISABLE_UNICODE_GROUPS(Pattern.DISABLE_UNICODE_GROUPS); // false by default

        public final int val;

        private Flags(int val) {
            this.val = val;
        }

    }

}
