package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.Expression;
import dev.yasint.regexsynth.exceptions.InvalidCodepointException;
import dev.yasint.regexsynth.unicode.UnicodeScript;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static dev.yasint.regexsynth.core.RegexConstructs.*;

/**
 * Synthesis :: Regular Expression Set
 * <p>
 * This generates a regular expression set when given a range
 * or chars. This class handles the simple character class and
 * ranged character classes expressions along with set negation.
 */
public final class SetExpression implements Expression {

    // Inside a set expression characters such as ^ ] / \ - " ' ` are invalid and
    // the expression will fail to compile. So, we check each element with this
    // pattern to escape that are similar. However, ^ no need to escape if it's
    // not initial (index 0) but we escape it anyways to make the code simpler
    // to understand.
    //
    private static final Pattern SET_RESTRICTED = Pattern.compile("[\\^\\]\\\\\\/\\-\"'`]");

    private Set<String> unicodeClasses; // This is not affected to codepoints. i.e. \P{...} \p{...}
    private Set<Integer> codepoints; // This will be sorted in natural order
    private boolean negated; // Whether this is negated ^ or not @mutable

    SetExpression(boolean negated) {
        this.negated = negated;
        this.codepoints = new TreeSet<>();
        this.unicodeClasses = new HashSet<>();
    }

    /**
     * Negates this set expression
     */
    void negate() {
        this.negated = true;
    }

    /**
     * Add a range of codepoints to this set. This operation will iterate
     * over the given range inclusively. And store them in the {@code codepoints}
     * sorted set.
     *
     * @param codepointA unicode codepoint from 0x000000
     * @param codepointB unicode codepoint upto 0x10FFFF
     */
    void addRange(final int codepointA, final int codepointB) {
        if (Character.isValidCodePoint(codepointA) && Character.isValidCodePoint(codepointB)) {
            if (codepointA > codepointB)
                throw new InvalidCodepointException("character range is out of order");
            if (codepointA == codepointB) {
                codepoints.add(codepointA);
                return;
            }
            for (int i = codepointA; i <= codepointB; i++) codepoints.add(i);
        } else {
            throw new InvalidCodepointException("invalid codepoint");
        }
    }

    /**
     * Add a single hexadecimal/integer codepoint to this set. Caller may invoke
     * this function multiple times to add all the values to the set.
     *
     * @param codepoint 0x000000 - 0x10FFFF
     */
    void addChar(final int codepoint) {
        if (!Character.isValidCodePoint(codepoint))
            throw new IllegalArgumentException("invalid codepoint");
        this.codepoints.add(codepoint);
    }

    // Set expression operations, available outside the package

    /**
     * Performs a union of two regular expressions set.
     * It will modify the source set @code{this} while operating.
     *
     * @param b set expression b
     * @return elements that belongs to this or b
     */
    public SetExpression union(final SetExpression b) {
        if (b.negated) {
            this.codepoints.removeAll(b.codepoints);
        } else {
            this.codepoints.addAll(b.codepoints);
        }
        return this;
    }

    /**
     * Performs a intersection of two regular expressions set.
     * It will modify the source set @code{this} while operating.
     *
     * @param b set expression b
     * @return elements that belongs to this and b
     */
    public SetExpression intersection(final SetExpression b) {
        if (b.negated) {
            this.codepoints.removeAll(b.codepoints);
        } else {
            this.codepoints.retainAll(b.codepoints);
        }
        return this;
    }

    /**
     * Performs a subtraction of two regular expressions set.
     * It will modify the source set @code{this} while operating.
     *
     * @param b set expression b
     * @return elements that belongs to this and not to b
     */
    public SetExpression difference(final SetExpression b) {
        if (!b.negated) b.negated = true;
        this.intersection(b);
        return this;
    }

    /**
     * This allows you to include unicode blocks to a set expression.
     * Note that when a block is included to a set. It does not
     * check for ranges, it simply append the correct syntax to
     * the set expression. i.e. [0-9A-Z\p{Arabic}]
     *
     * @param negated whether this unicode block is negated or not
     * @param block   valid unicode general category / script block
     * @return this
     */
    public SetExpression withUnicodeClass(final UnicodeScript block, final boolean negated) {
        unicodeClasses.add(
                Literals.unicodeClass(block, negated)
                        .toRegex().toString()
        );
        return this;
    }

    /**
     * Creates a character class expression. This algorithm uses
     * unicode codepoints to create character class ranges.
     *
     * @return set expression
     */
    @Override
    public StringBuilder toRegex() {

        // coping the tree set to this set will not change the order.
        // we need to copy this set's elements to this because we need to
        // access each element by it's index.
        final Integer[] chars = codepoints.toArray(new Integer[0]);

        // Empty set. (probably after operations) but also check
        // for unicode character classes.
        if (chars.length == 0 && unicodeClasses.isEmpty()) {
            return new StringBuilder(0);
        }

        // avoid creating a set expression. instead just
        // escape the sequence. [a] => a (only if its not negated)
        if (chars.length == 1 && !negated && unicodeClasses.isEmpty()) {
            return new StringBuilder().append(
                    toRegexInterpretable(chars[0])
            );
        }

        // we use a string-builder to construct the set expression iteratively.
        final StringBuilder expression = new StringBuilder();
        expression.append(OPEN_SQUARE_BRACKET);
        if (negated) expression.append(CARAT);

        int rangeStartIndex = -1;
        boolean isInRange = false;

        for (int curIndex = 0; curIndex < chars.length; curIndex++) {
            // Check if this can be a range
            if (curIndex + 1 < chars.length) {
                if (chars[curIndex + 1] - chars[curIndex] == 1) {
                    if (!isInRange) {
                        rangeStartIndex = curIndex;
                        isInRange = true;
                    }
                    continue;
                }
            }
            if (isInRange) {
                // Check if the range is only within two characters.
                // i.e. a-b then we can simplify it to [ab]
                if (curIndex - rangeStartIndex == 1 /*difference*/) {
                    expression
                            .append(toRegexInterpretable(chars[rangeStartIndex]))
                            .append(toRegexInterpretable(chars[curIndex]));
                } else {
                    expression
                            .append(toRegexInterpretable(chars[rangeStartIndex]))
                            .append(HYPHEN)
                            .append(toRegexInterpretable(chars[curIndex]));
                }
                // Reset range starting back to initial
                rangeStartIndex = -1;
                isInRange = false;
            } else {
                expression.append(toRegexInterpretable(chars[curIndex]));
            }
        }

        // Now we can append the unicode char classes if the user specified any.
        if (unicodeClasses.size() > 0) {
            for (String klass : unicodeClasses) {
                expression.append(klass);
            }
        }

        return expression.append(CLOSE_SQUARE_BRACKET);

    }

    private String toRegexInterpretable(final int codepoint) {

        // if the codepoint is a control character then represent them
        // as hexadecimal values in the regex. in here we can escape
        // sequences like \a \t \n \v \f \r
        //
        // Invariant: (codePoint >= 0x00 && codePoint <= 0x1F) || (codePoint >= 0x7F && codePoint <= 0x9F);
        //
        if (Character.isISOControl(codepoint)) {
            // RE2 only accepts \x00 style. It does not allow \x0
            // syntax. So, below formatter formats a codepoint to
            // its 2digit hex value.
            return String.format("\\x%02X", codepoint);
        }

        // if it's a supplementary unicode character we execute this block.
        // RE2J regex does not support surrogate pairs inside sets. So, RE2J
        // supports \x{10FFFF} style syntax in expressions.
        //
        // Invariant: codepoint >= 0x010000 && codepoint <= 0x10FFFF
        //
        if (Character.isSupplementaryCodePoint(codepoint)) {
            return String.format("\\x{%s}", Integer.toHexString(codepoint));
        }

        // if it's bmp codepoint (such codepoints can be stored in
        // single 16bit char in java) and it's a set restricted
        // then just escape it with a backslash.
        //
        if (Character.isBmpCodePoint(codepoint)) {
            final String c = Character.toString((char) codepoint);
            if (SET_RESTRICTED.matches(c)) {
                return BACKSLASH + c;
            }
        }

        // Else we just represent as it is. Also, note that the same function
        // is being used to create set range elements. RE2 accepts syntax like
        // \xFF-\x{FFFF} or a-\x{FFFF}. So either way it's fine.
        //
        return Character.toString(((char) codepoint));

    }

}
