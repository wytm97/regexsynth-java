package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Expression;

import java.util.Collections;
import java.util.LinkedList;

import static dev.yasint.regexsynth.core.Constructs.*;

/**
 * Synthesis :: Regular Expression Integer Range
 * <p>
 * This generates a regular expression number range given
 * inclusive start and end integers. This implementation's
 * running time is O(log n).
 * <p>
 * This code is originally based on a StackOverflow post answer.
 * However, the code has some optimizations and changes to match
 * our use-case. `https://bit.ly/3bIXZBy`
 */
public final class IntegerRange implements Expression {

    private final int start;
    private final int end;

    IntegerRange(int start, int end) {
        this.start = start;
        this.end = end;
    }

    private static LinkedList<IntegerRange> leftBounds(int start, int end) {
        final LinkedList<IntegerRange> result = new LinkedList<>();
        while (start < end) {
            final IntegerRange range = IntegerRange.fromStart(start);
            result.add(range);
            start = range.end + 1;
        }
        return result;
    }

    private static LinkedList<IntegerRange> rightBounds(int start, int end) {
        final LinkedList<IntegerRange> result = new LinkedList<>();
        while (start < end) {
            final IntegerRange range = IntegerRange.fromEnd(end);
            result.add(range);
            end = range.start - 1;
        }
        Collections.reverse(result);
        return result;
    }

    private static IntegerRange fromEnd(int end) {
        final char[] chars = String.valueOf(end).toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) { // O(N)
            if (chars[i] == '9') {
                chars[i] = '0';
            } else {
                chars[i] = '0';
                break;
            }
        }
        return new IntegerRange(Integer.parseInt(String.valueOf(chars)), end);
    }

    private static IntegerRange fromStart(int start) {
        final char[] chars = String.valueOf(start).toCharArray();
        for (int i = chars.length - 1; i >= 0; i--) { // O(N)
            if (chars[i] == '0') {
                chars[i] = '9';
            } else {
                chars[i] = '9';
                break;
            }
        }
        return new IntegerRange(start, Integer.parseInt(String.valueOf(chars)));
    }

    private static IntegerRange join(IntegerRange a, IntegerRange b) {
        return new IntegerRange(a.start, b.end);
    }

    String create() {

        // Create left boundaries
        final LinkedList<IntegerRange> left = IntegerRange.leftBounds(start, end);
        final IntegerRange lastLeft = left.removeLast();

        // Create right boundaries
        final LinkedList<IntegerRange> right = IntegerRange.rightBounds(lastLeft.start, end);
        final IntegerRange firstRight = right.removeFirst();

        // Merge all classes
        final LinkedList<IntegerRange> merged = new LinkedList<>(left);
        if (!lastLeft.overlaps(firstRight)) {
            merged.add(lastLeft);
            merged.add(firstRight);
        } else {
            merged.add(IntegerRange.join(lastLeft, firstRight));
        }
        merged.addAll(right);

        // Append the ranges from reverse order. So the match
        // will go from high to low. Otherwise even if its
        // has 2-digits it'll only match 1; if it's an option.
        final StringBuilder expression = new StringBuilder();
        for (int i = merged.size() - 1; i >= 0; i--) {
            expression.append(merged.get(i).toRegex());
            if (i != 0) expression.append(ALTERNATION);
        }
        return expression.toString();

    }

    private boolean overlaps(final IntegerRange r) {
        return end > r.start && r.end > start;
    }

    @Override
    public String toString() {
        return String.format(
                "RangeGen { start=%d, end=%d }",
                start, end
        );
    }

    @Override
    public StringBuilder toRegex() {
        final String startStr = String.valueOf(start);
        final String endStr = String.valueOf(end);
        final StringBuilder result = new StringBuilder();
        for (int pos = 0; pos < startStr.length(); pos++) {
            if (startStr.charAt(pos) == endStr.charAt(pos)) {
                result.append(startStr.charAt(pos));
            } else {
                result.append(OPEN_SQUARE_BRACKET)
                        .append(startStr.charAt(pos))
                        .append(HYPHEN)
                        .append(endStr.charAt(pos))
                        .append(CLOSE_SQUARE_BRACKET);
            }
        }
        return result;
    }

}
