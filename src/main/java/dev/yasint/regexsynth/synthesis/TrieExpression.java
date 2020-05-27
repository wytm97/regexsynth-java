package dev.yasint.regexsynth.synthesis;

import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.util.Common;

import java.util.*;

import static dev.yasint.regexsynth.api.MetaCharacters.*;

/**
 * Synthesis :: String minimization
 */
public class TrieExpression implements Expression {

    private static final String NULL_KEY = ""; // Null key represents an empty string
    private final Node root = new Node(); // Initial node of the trie. (null - children)

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
        private final List<String> alternations;
        private final List<String> charClasses;

        private boolean hasOptionals = false;
        private boolean hasCharacterClasses = false;

        private Node() {
            this.nodes = new TreeMap<>();
            this.alternations = new ArrayList<>();
            this.charClasses = new ArrayList<>();
        }

        /**
         * Checks whether a given key is present in this
         * leaf node of the trie.
         *
         * @param _char rune / character
         * @return true if present
         */
        private boolean containsKey(final String _char) {
            return this.nodes.containsKey(_char);
        }

        /**
         * Inserts a given key and a child node to this
         * leaf node's children {@code nodes}
         *
         * @param _char key / rune / character
         * @param node  child node
         */
        private void put(final String _char, final Node node) {
            this.nodes.put(_char, node);
        }

        /**
         * Retrieves a child node from this leaf node of trie
         * when given the key.
         *
         * @param _char key of the child node
         * @return mapping child node
         */
        private Node get(final String _char) {
            return this.nodes.get(_char);
        }

        private void synthesizeStringAlternations() {
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
                    this.hasOptionals = true;
                }
            }
        }

        private void synthesizeCharacterClasses() {
            this.hasCharacterClasses = alternations.isEmpty();
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
        }

        @Override
        public StringBuilder toRegex() {

            if (this.nodes.containsKey(NULL_KEY) && this.nodes.size() == 1) {
                return null; // Terminate; final state, means this is an null edge
            }
            this.synthesizeStringAlternations();
            this.synthesizeCharacterClasses();

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

            if (this.hasOptionals) {
                if (this.hasCharacterClasses) {
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
