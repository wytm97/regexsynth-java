package ast;

import core.Expression;
import core.Utility;

import java.util.*;

import static core.Constructs.*;

/**
 * Synthesis :: String minimization
 */
public final class TrieStructure implements Expression {

    // Initial node of the trie. (null - children)
    private final Node root = new Node();

    TrieStructure() {
    }

    public void insert(final String word) {
        Node current = this.root;
        for (int i = 0; i < word.length(); ++i) {
            final String c = "" + word.charAt(i);
            if (!current.containsKey(c)) {
                current.put(c, new Node());
            }
            current = current.get(c);
        }
        // [ "": null ] as terminator
        current.put("", null);
    }

    public void insertAll(final Collection<String> words) {
        for (String word : words)
            insert(word);
    }

    @Override
    public StringBuilder toRegex() {
        return this.root.toRegex();
    }

    private static final class Node {

        private final Map<String, Node> nodes;

        private Node() {
            this.nodes = new TreeMap<>();
        }

        private StringBuilder toRegex() {

            if (this.nodes.containsKey("") && this.nodes.size() == 1) {
                return null; // Terminate; final state
            }

            final List<String> alternations = new ArrayList<>();
            final List<String> charClasses = new ArrayList<>();

            boolean hasOptionals = false;
            for (Map.Entry<String, Node> entry : this.nodes.entrySet()) {
                final String escaped = Utility.asRegexLiteral(entry.getKey());
                if (entry.getValue() != null) {
                    final StringBuilder subExpression = entry.getValue().toRegex();
                    if (subExpression != null) {
                        alternations.add(escaped + subExpression.toString());
                    } else {
                        charClasses.add(escaped);
                    }
                } else {
                    hasOptionals = true;
                }
            }

            final boolean hasCharClass = alternations.isEmpty();
            if (charClasses.size() > 0) {
                if (charClasses.size() == 1) {
                    alternations.add(charClasses.get(0));
                } else {
                    final StringBuilder set = new StringBuilder();
                    set.append(OPEN_SQUARE_BRACKET);
                    for (final String it : charClasses)
                        set.append(it);
                    set.append(CLOSE_SQUARE_BRACKET);
                    alternations.add(set.toString());
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
                    return expression.append(QUESTION_MARK);
                } else {
                    expression.insert(0, "" + PAREN_OPEN + QUESTION_MARK + COLON);
                    expression.append(PAREN_CLOSE).append(QUESTION_MARK);
                    return expression;
                }
            }

            return expression;

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

    }

}
