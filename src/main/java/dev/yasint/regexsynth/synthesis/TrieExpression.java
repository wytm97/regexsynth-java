package dev.yasint.regexsynth.synthesis;

import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.util.Common;

import java.util.*;

import static dev.yasint.regexsynth.api.MetaCharacters.*;

/**
 * Synthesis :: String minimization
 */
public final class TrieExpression implements Expression {

    private static final String NULL_KEY = "";
    // Initial node of the trie. (null - children)
    private final Node root = new Node();

    public TrieExpression() { /*available for testing*/ }

    /**
     * Inserts one word into the trie. O(N) complexity
     *
     * @param word string input
     */
    public void insert(final String word) {
        Node current = this.root;
        for (int i = 0; i < word.length(); ++i) {
            final String c = Character.toString(word.charAt(i));
            if (!current.containsKey(c)) {
                current.put(c, new Node());
            }
            current = current.get(c);
        }
        // [ "": null ] as terminator
        current.put(NULL_KEY, null);
    }

    /**
     * Inserts a collection of words into the trie. O(N)
     *
     * @param words string inputs
     */
    public void insertAll(final Collection<String> words) {
        for (String word : words)
            insert(word);
    }

    @Override
    public StringBuilder toRegex() {
        return this.root.toRegex();
    }

    private static final class Node implements Expression {

        private final Map<String, Node> nodes;

        private Node() {
            this.nodes = new TreeMap<>();
        }

        private boolean containsKey(final String key) {
            return this.nodes.containsKey(key);
        }

        private void put(final String c, final Node node) {
            this.nodes.put(c, node);
        }

        private Node get(final String _char) {
            return this.nodes.get(_char);
        }

        @Override
        public StringBuilder toRegex() {

            if (this.nodes.containsKey(NULL_KEY) && this.nodes.size() == 1) {
                return null; // Terminate; final state
            }

            final List<String> alternations = new ArrayList<>();
            final List<String> charClasses = new ArrayList<>();

            boolean hasOptionals = false;
            // for each leaf node of this node (adjacent nodes)
            for (Map.Entry<String, Node> entry : this.nodes.entrySet()) {
                // escape any special regular expression constructs is present
                final String escaped = Common.asRegexLiteral(entry.getKey());
                // if it's not a final state
                if (entry.getValue() != null) {
                    // get the leaf node's expression (depth-first check)
                    final StringBuilder subExpression = entry.getValue().toRegex();
                    if (subExpression != null) {
                        // concat(a,b)
                        alternations.add(escaped + subExpression.toString());
                    } else {
                        // or this a character class: jun,jul => ju[nl]
                        charClasses.add(escaped);
                    }
                } else {
                    hasOptionals = true;
                }
            }

            final boolean hasCharClass = alternations.isEmpty();
            if (charClasses.size() > 0) {
                if (charClasses.size() == 1) {
                    alternations.add(charClasses.get(0)); // [a] => a
                } else {
                    final StringBuilder set = new StringBuilder();
                    set.append(OPEN_SQUARE_BRACKET);
                    for (final String it : charClasses)
                        set.append(it);
                    set.append(CLOSE_SQUARE_BRACKET);
                    alternations.add(set.toString()); // [abc]
                }
            }

            final StringBuilder expression = new StringBuilder();

            if (alternations.size() == 1) {
                expression.append(alternations.get(0));
            } else {
                expression.append(PAREN_OPEN).append(QUESTION_MARK).append(COLON);
                for (int i = 0; i < alternations.size(); i++) {
                    expression.append(alternations.get(i));
                    if (i != alternations.size() - 1) {
                        expression.append(ALTERNATION);
                    }
                }
                expression.append(PAREN_CLOSE);
            }

            if (hasOptionals) {
                if (hasCharClass) {
                    // optional abc?
                    return expression.append(QUESTION_MARK);
                } else {
                    // a quicker way to insert (?:...)
                    expression
                            .insert(0, "" + PAREN_OPEN + QUESTION_MARK + COLON)
                            .append(PAREN_CLOSE)
                            .append(QUESTION_MARK);
                    return expression;
                }
            }

            return expression;

        }

    }

}
