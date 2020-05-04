package dev.yasint.regexsynth.dsl;

import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.exceptions.GenericException;
import dev.yasint.regexsynth.exceptions.SetElementException;
import dev.yasint.regexsynth.synthesis.SetExpression;
import dev.yasint.regexsynth.unicode.UnicodeScript;
import dev.yasint.regexsynth.util.Common;

import java.util.Objects;

import static dev.yasint.regexsynth.api.MetaCharacters.PERIOD;
import static dev.yasint.regexsynth.util.Common.isNotASetExpression;

/**
 * Contains all the set constructs and character classes.
 * RegexSynth supports all the POSIX character classes.
 *
 * @since 1.0.0
 */
public final class CharClasses {

    /**
     * Matches any character, possibly including newline \n if
     * the 's' {@link dev.yasint.regexsynth.api.RegexSynth.Flags#DOTALL}
     * flag is turned on.
     *
     * @return match anything
     */
    public static Expression anything() {
        return simpleSet(PERIOD);
    }

    // Set Operations

    /**
     * Simply converts a given set to a negated character class.
     * <code>[^acd]</code>. This is a impure function. Because
     * it mutates the source set.
     *
     * @param set source set to convert
     * @return negated set expression
     */
    public static Expression negated(final Expression set) {
        if (isNotASetExpression(set)) {
            throw new GenericException("must be a set expression");
        }
        ((SetExpression) set).negate();
        return set;
    }

    /**
     * Performs a union operation on two sets
     *
     * @param setA source set
     * @param setB target set
     * @return result
     */
    public static Expression union(final Expression setA, final Expression setB) {
        if (isNotASetExpression(setA) || isNotASetExpression(setB)) {
            throw new GenericException("union only supported for set expressions");
        }
        return ((SetExpression) setA).union((SetExpression) setB);
    }

    public static Expression difference(final Expression setA, final Expression setB) {
        if (isNotASetExpression(setA) || isNotASetExpression(setB)) {
            throw new GenericException("difference only supported for set expressions");
        }
        return ((SetExpression) setA).difference((SetExpression) setB);
    }

    public static Expression intersection(final Expression setA, final Expression setB) {
        if (isNotASetExpression(setA) || isNotASetExpression(setB)) {
            throw new GenericException("intersection only supported for set expressions");
        }
        return ((SetExpression) setA).intersection((SetExpression) setB);
    }

    public static Expression includeUnicodeScript(final Expression set, final UnicodeScript script, final boolean negated) {
        if (isNotASetExpression(set)) {
            throw new GenericException("includeUnicodeScript only supported for set expressions");
        }
        return ((SetExpression) set).withUnicodeClass(script, negated);
    }

    // Set Construction

    /**
     * Creates a ranged regex charclass. i.e. [A-Z]
     *
     * @param from staring char inclusive (surrogates or bmp)
     * @param to   ending char inclusive (surrogates or bmp)
     * @return set expression
     */
    public static Expression rangedSet(final String from, final String to) {
        if (from == null || to == null)
            throw new SetElementException("set range elements cannot be null");
        final SetExpression set = new SetExpression(false);
        set.addRange(Common.toCodepoint(from), Common.toCodepoint(to));
        return set;
    }

    /**
     * Creates a ranged regex charclass. i.e. [A-Z]
     *
     * @param codepointA staring codepoint inclusive
     * @param codepointB ending codepoint inclusive
     * @return set expression
     */
    public static Expression rangedSet(final int codepointA, final int codepointB) {
        final SetExpression set = new SetExpression(false);
        set.addRange(codepointA, codepointB);
        return set;
    }

    /**
     * Creates a simple regex charclass i.e. [135] will be optimized
     * if it's a valid range. for example: if you pass a,b,c,d,f it
     * will create [a-df]. but if you pass elements like a,z then it
     * will only create a set for those two element without ranges [az]
     *
     * @param characters characters (surrogates or bmp)
     * @return set expression
     */
    public static Expression simpleSet(final String... characters) {
        final SetExpression set = new SetExpression(false);
        for (final String c : Objects.requireNonNull(characters)) {
            if (c.length() > 2) {
                // only accepts valid bmp or astral symbols
                throw new SetElementException("expected bmp or astral codepoint");
            }
            set.addChar(Common.toCodepoint(c));
        }
        return set;
    }

    /**
     * @param codepoints codepoints
     * @return set expression
     */
    public static Expression simpleSet(final int... codepoints) {
        final SetExpression set = new SetExpression(false);
        for (final int c : Objects.requireNonNull(codepoints))
            set.addChar(c);
        return set;
    }

    /**
     * Constructs an empty set. This is useful when you only want
     * to include unicode scripts into a set expression.
     *
     * @return empty set expression
     */
    public static Expression emptySet() {
        return new SetExpression(false);
    }

    // Pre-defined character classes and escape sequences.

    /**
     * Posix character classes. This class also include the
     * predefined set of escape sequences.
     */
    public static class Posix {

        /**
         * Constructs an upper-case alphabetic charclass.
         * [A-Z] this uses {@link SetExpression}. Equivalent to
         * \p{Lower} in java
         *
         * @return lowercase charclass
         */
        public static Expression lowercase() {
            return rangedSet("a", "z");
        }

        /**
         * Constructs an upper-case alphabetic charclass.
         * [A-Z] this uses {@link SetExpression}. Equivalent to
         * \p{Upper} in java
         *
         * @return uppercase charclass
         */
        public static Expression uppercase() {
            return rangedSet("A", "Z");
        }

        /**
         * Constructs the ascii character set 0-127
         *
         * @return ascii charset
         */
        public static Expression ascii() {
            return rangedSet(0x00, 0x7F);
        }

        /**
         * Constructs the extended ascii character set 0-255
         *
         * @return ascii charset
         */
        public static Expression ascii2() {
            return rangedSet(0x00, 0xFF);
        }

        /**
         * Constructs an alphabetic charclass containing
         * [a-zA-Z] this uses {@link SetExpression}
         *
         * @return alphabetic charclass
         */
        public static Expression alphabetic() {
            return union(lowercase(), uppercase());
        }

        /**
         * Constructs an numeric charclass [0-9]
         * This is equivalent to \d in any regex flavor.
         *
         * @return numeric charclass
         */
        public static Expression digit() {
            return rangedSet("0", "9");
        }

        /**
         * Constructs an negated numeric charclass [^0-9]
         * This is equivalent to \D in any regex flavor.
         *
         * @return numeric charclass
         */
        public static Expression notDigit() {
            return negated(digit());
        }

        /**
         * Constructs an alphanumeric charclass [a-zA-Z0-9]
         *
         * @return alphanumeric charclass
         */
        public static Expression alphanumeric() {
            return union(alphabetic(), digit());
        }

        /**
         * Constructs an punctuation charclass using one of these
         * characters <code>!"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\]^_`{|}~</code>
         *
         * @return punctuation charclass
         */
        public static Expression punctuation() {
            final String elements = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            return simpleSet(elements.split("")/*split into char*/);
        }

        /**
         * Constructs an visible character class using {@code alphanumeric}
         * and {@code punctuation}.
         *
         * @return graphical charclass
         */
        public static Expression graphical() {
            return union(alphanumeric(), punctuation());
        }

        /**
         * Constructs an printable character class using {@code graphical}
         * and a space character.
         *
         * @return printable charclass
         */
        public static Expression printable() {
            return union(graphical(), simpleSet(0x20/*space*/));
        }

        /**
         * Constructs an blank space charclass. This simple
         * class includes space and horizontal tab.
         *
         * @return blank-space charclass
         */
        public static Expression blank() {
            return simpleSet(0x09/*h-tab*/, 0x20/*space*/);
        }

        /**
         * Constructs an hexadecimal character class
         *
         * @return hex charclass
         */
        public static Expression hexDigit() {
            return union(
                    rangedSet("A", "F"),
                    union(digit(), rangedSet("a", "f"))
            );
        }

        /**
         * Constructs an blank space characters charclass. This simple
         * class includes [ \t\n\x0B\f\r] . This is equivalent to
         * \s in most regex flavors.
         *
         * @return white space charclass
         */
        public static Expression whitespace() {
            // following codepoints as [ \t\n\v\f\r] 0x0B == \v
            return simpleSet(0x20, 0x9, 0xA, 0xB, 0xC, 0xD);
        }

        /**
         * Constructs an negated whitespace characters charclass.
         * This simple class includes [^ \t\n\v\f\r] . This is
         * equivalent to \S in some regex flavors.
         *
         * @return negated whitespace charclass
         */
        public static Expression notWhitespace() {
            // following codepoints as [^ \t\n\v\f\r] 0x0B == \v
            // in some languages, including java.
            return negated(whitespace());
        }

        /**
         * Constructs an word char class equivalent to \w
         * which includes [0-9A-Za-z_]
         *
         * @return word charclass
         */
        public static Expression word() {
            return union(alphanumeric(), simpleSet("_"));
        }

        /**
         * Constructs an negated word char class equivalent to \W
         * which includes [^0-9A-Za-z_]
         *
         * @return negated word charclass
         */
        public static Expression notWord() {
            return negated(word());
        }

        /**
         * Constructs an ISO control character class. Equivalent
         * to [[:cntrl:]] in RE2
         *
         * @return control charclass
         */
        public static Expression control() {
            return union(rangedSet(0x0, 0x1F), simpleSet(0x7f));
        }

    }

    /**
     * Escape sequences. These classes can be applied inside
     * set expressions or outside set expressions.
     */
    public static class EscapeSequences {

        public static Expression space() {
            return simpleSet(" ");
        }

        public static Expression backslash() {
            return simpleSet("\\"); // \
        }

        public static Expression doubleQuotes() {
            return simpleSet("\""); // "
        }

        public static Expression singleQuote() {
            return simpleSet("'"); // '
        }

        public static Expression backtick() {
            return simpleSet("`"); // `
        }

        public static Expression bell() {
            return simpleSet(0x07); // \a
        }

        public static Expression horizontalTab() {
            // \h 	A horizontal whitespace character: [ \t\xA0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]
            // \H 	A non-horizontal whitespace character: [^\h]
            return simpleSet(0x09); // \t
        }

        public static Expression linebreak() {
            return simpleSet(0x0A); // \n
        }

        public static Expression verticalTab() {
            // Re consider:
            // \v 	A vertical whitespace character: [\n\x0B\f\r\x85\u2028\u2029]
            // \V 	A non-vertical whitespace character: [^\v]
            return simpleSet(0x0B);
        }

        public static Expression formfeed() {
            return simpleSet(0x0C); // \f
        }

        public static Expression carriageReturn() {
            return simpleSet(0x0D); // \r
        }

    }

}
