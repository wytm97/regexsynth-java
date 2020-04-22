package dev.yasint.regexsynth.dsl;

import dev.yasint.regexsynth.util.Common;
import dev.yasint.regexsynth.exceptions.SetElementException;

import java.util.Objects;

import static dev.yasint.regexsynth.api.RegexConstructs.PERIOD;

/**
 * Contains all the set constructs and character classes.
 * RegexSynth supports all the POSIX character classes.
 *
 * @since 1.0.0
 */
public final class CharClasses {

    /**
     * Matches any character, possibly including newline \n if
     * the 's' {@link dev.yasint.regexsynth.api.RegexSynth.Flags}
     * DOTALL flag is turned on.
     *
     * @return match anything
     */
    public static SetExpression anything() {
        return simpleSet(PERIOD);
    }

    /**
     * Simply converts a given set to a negated character class.
     * <code>[^acd]</code>
     *
     * @param set source set to convert
     * @return negated set expression
     */
    public static SetExpression negated(final SetExpression set) {
        set.negate();
        return set;
    }

    /**
     * Creates a ranged regex charclass. i.e. [A-Z]
     *
     * @param from staring char inclusive (surrogates or bmp)
     * @param to   ending char inclusive (surrogates or bmp)
     * @return set expression
     */
    public static SetExpression rangedSet(final String from, final String to) {
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
    public static SetExpression rangedSet(final int codepointA, final int codepointB) {
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
    public static SetExpression simpleSet(final String... characters) {
        final SetExpression set = new SetExpression(false);
        for (final String c : Objects.requireNonNull(characters)) {
            if (c.length() > 2) {
                // cannot pass long strings or texts to a set! it makes no sense.
                // and this only accepts valid bmp or astral symbols.
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
    public static SetExpression simpleSet(final int... codepoints) {
        final SetExpression set = new SetExpression(false);
        for (final int c : Objects.requireNonNull(codepoints))
            set.addChar(c);
        return set;
    }

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
        public static SetExpression lowercase() {
            return rangedSet("a", "z");
        }

        /**
         * Constructs an upper-case alphabetic charclass.
         * [A-Z] this uses {@link SetExpression}. Equivalent to
         * \p{Upper} in java
         *
         * @return uppercase charclass
         */
        public static SetExpression uppercase() {
            return rangedSet("A", "Z");
        }

        /**
         * Constructs the ascii character set 0-127
         *
         * @return ascii charset
         */
        public static SetExpression ascii() {
            return rangedSet(0x00, 0x7F);
        }

        /**
         * Constructs the extended ascii character set 0-255
         *
         * @return ascii charset
         */
        public static SetExpression ascii2() {
            return rangedSet(0x00, 0xFF);
        }

        /**
         * Constructs an alphabetic charclass containing
         * [a-zA-Z] this uses {@link SetExpression}
         *
         * @return alphabetic charclass
         */
        public static SetExpression alphabetic() {
            return lowercase()
                    .union(uppercase());
        }

        /**
         * Constructs an numeric charclass [0-9]
         * This is equivalent to \d in any regex flavor.
         *
         * @return numeric charclass
         */
        public static SetExpression digit() {
            return rangedSet("0", "9");
        }

        /**
         * Constructs an negated numeric charclass [^0-9]
         * This is equivalent to \D in any regex flavor.
         *
         * @return numeric charclass
         */
        public static SetExpression notDigit() {
            return negated(digit());
        }

        /**
         * Constructs an alphanumeric charclass [a-zA-Z0-9]
         *
         * @return alphanumeric charclass
         */
        public static SetExpression alphanumeric() {
            return alphabetic().union(digit());
        }

        /**
         * Constructs an punctuation charclass using one of these
         * characters <code>!"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\]^_`{|}~</code>
         *
         * @return punctuation charclass
         */
        public static SetExpression punctuation() {
            final String elements = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            return simpleSet(elements.split("")/*split into char*/);
        }

        /**
         * Constructs an visible character class using {@code alphanumeric}
         * and {@code punctuation}.
         *
         * @return graphical charclass
         */
        public static SetExpression graphical() {
            return alphanumeric().union(punctuation());
        }

        /**
         * Constructs an printable character class using {@code graphical}
         * and a space character.
         *
         * @return printable charclass
         */
        public static SetExpression printable() {
            return graphical().union(simpleSet(0x20/*space*/));
        }

        /**
         * Constructs an blank space charclass. This simple
         * class includes space and horizontal tab.
         *
         * @return blank-space charclass
         */
        public static SetExpression blank() {
            return simpleSet(0x09/*h-tab*/, 0x20/*space*/);
        }

        /**
         * Constructs an hexadecimal character class
         *
         * @return hex charclass
         */
        public static SetExpression hexDigit() {
            return digit()
                    .union(rangedSet("a", "f"))
                    .union(rangedSet("A", "F"));
        }

        /**
         * Constructs an blank space characters charclass. This simple
         * class includes [ \t\n\x0B\f\r] . This is equivalent to
         * \s in most regex flavors.
         *
         * @return white space charclass
         */
        public static SetExpression whitespace() {
            // following codepoints as [ \t\n\v\f\r] 0x0B == \v
            return simpleSet(0x20, 0x9, 0xA, 0xB, 0xC, 0xD);
        }

        /**
         * Constructs an negated white space characters charclass.
         * This simple class includes [^ \t\n\v\f\r] . This is
         * equivalent to \S in some regex flavors.
         *
         * @return negated whitespace charclass
         */
        public static SetExpression notWhitespace() {
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
        public static SetExpression word() {
            return alphanumeric().union(simpleSet("_"));
        }

        /**
         * Constructs an negated  word char class equivalent to \W
         * which includes [^0-9A-Za-z_]
         *
         * @return negated word charclass
         */
        public static SetExpression notWord() {
            return negated(word());
        }

        public static SetExpression control() {
            return null;
        }

    }

    /**
     * Escape sequences. These classes can be applied inside
     * set expressions or outside set expressions.
     */
    public static class EscapeSequences {

        public static SetExpression space() {
            return simpleSet(" ");
        }

        public static SetExpression backslash() {
            return simpleSet("\\"); // \
        }

        public static SetExpression doubleQuotes() {
            return simpleSet("\""); // "
        }

        public static SetExpression singleQuote() {
            return simpleSet("'"); // '
        }

        public static SetExpression backtick() {
            return simpleSet("`"); // `
        }

        public static SetExpression bell() {
            return simpleSet(0x07); // \a
        }

        public static SetExpression horizontalTab() {
            // \h 	A horizontal whitespace character: [ \t\xA0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]
            // \H 	A non-horizontal whitespace character: [^\h]
            return simpleSet(0x09); // \t
        }

        public static SetExpression linebreak() {
            return simpleSet(0x0A); // \n
        }

        public static SetExpression verticalTab() {
            // Re consider:
            // \v 	A vertical whitespace character: [\n\x0B\f\r\x85\u2028\u2029]
            // \V 	A non-vertical whitespace character: [^\v]
            return simpleSet(0x0B);
        }

        public static SetExpression formfeed() {
            return simpleSet(0x0C); // \f
        }

        public static SetExpression carriageReturn() {
            return simpleSet(0x0D); // \r
        }

    }

}
