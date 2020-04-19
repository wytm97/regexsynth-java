package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Expression;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static dev.yasint.regexsynth.ast.Groups.nonCaptureGroup;
import static dev.yasint.regexsynth.core.Constructs.ALTERNATION;

public final class Operators {

    /**
     * Creates an alternation between the passed expressions.
     * <code>[A-Z]|[a-z]|[0-9]</code>
     *
     * @param expressions alternations
     * @return wrapped alternated expressions
     */
    public static Expression either(final Expression... expressions) {
        final String alternations = Arrays.stream(Objects.requireNonNull(expressions))
                .map(Expression::toRegex)
                .collect(Collectors.joining(ALTERNATION));
        return nonCaptureGroup(() -> new StringBuilder(alternations));
    }

    /**
     * Creates an alternation between multiple strings.
     * <code>{January,February,March} = (?:Jan|Febr)uary|March)</code>
     *
     * @param strings alternation strings
     * @return wrapped alternated strings
     */
    public static Expression either(final String... strings) {
        return either(new HashSet<>(Arrays.asList(strings)));
    }

    /**
     * Creates an alternation between multiple strings.
     * <code>{January,February,March} = (?:Jan|Febr)uary|March)</code>
     *
     * @param strings alternation strings
     * @return wrapped alternated strings
     */
    public static Expression either(final Set<String> strings) {
        final TrieStructure trie = new TrieStructure();
        trie.insertAll(strings);
        return trie;
    }

    /**
     * Creates a concatenation of two given regular expressions. Note:
     * it simply just append the second expression. (a followed by b)
     *
     * @param a expression a
     * @param b expression b
     * @return concatenated expression.
     */
    public static Expression concat(final Expression a, final Expression b) {
        return () -> Objects.requireNonNull(a).toRegex()
                .append(Objects.requireNonNull(b).toRegex());
    }

    /**
     * Concatenates multiple expressions at once. Expressions
     * will combined in absolute order.
     *
     * @param expressions multiple expressions
     * @return concatenated expressions.
     */
    public static Expression concat(final Expression... expressions) {
        return () -> Arrays.stream(Objects.requireNonNull(expressions))
                .map(Expression::toRegex)
                .reduce(new StringBuilder(), StringBuilder::append);
    }

}
