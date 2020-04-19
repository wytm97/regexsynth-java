package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Utility;

import java.util.Objects;

import static dev.yasint.regexsynth.core.Constructs.PERIOD;

/**
 * Contains all the set constructs and character classes.
 * RegexSynth supports all the POSIX character classes.
 *
 * @since 1.0.0
 */
public final class CharClasses {

    /**
     * Matches any character, possibly including newline \n if
     * the 's' {@link dev.yasint.regexsynth.core.RegexSynth.Flags}
     * DOTALL flag is turned on.
     *
     * @return match anything
     */
    public static RegexSet anything() {
        return simpleSet(PERIOD);
    }

    /**
     * Simply converts a given set to a negated character class.
     * <code>[^acd]</code>
     *
     * @param set source set to convert
     * @return negated set expression
     */
    public static RegexSet negated(final RegexSet set) {
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
    public static RegexSet rangedSet(final String from, final String to) {
        if (from == null || to == null)
            throw new NullPointerException();
        final RegexSet set = new RegexSet(false);
        set.addRange(Utility.toCodepoint(from), Utility.toCodepoint(to));
        return set;
    }

    /**
     * Creates a ranged regex charclass. i.e. [A-Z]
     *
     * @param codepointA staring codepoint inclusive
     * @param codepointB ending codepoint inclusive
     * @return set expression
     */
    public static RegexSet rangedSet(final int codepointA, final int codepointB) {
        final RegexSet set = new RegexSet(false);
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
    public static RegexSet simpleSet(final String... characters) {
        final RegexSet set = new RegexSet(false);
        for (final String c : Objects.requireNonNull(characters)) {
            if (c.length() > 2) {
                // cannot pass long strings or texts to a set! it makes no sense.
                // and this only accepts valid bmp or astral symbols.
                throw new RuntimeException("expected bmp or astral codepoint");
            }
            set.addChar(Utility.toCodepoint(c));
        }
        return set;
    }

    /**
     * @param codepoints codepoints
     * @return set expression
     */
    public static RegexSet simpleSet(final int... codepoints) {
        final RegexSet set = new RegexSet(false);
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
         * [A-Z] this uses {@link RegexSet}. Equivalent to
         * \p{Lower} in java
         *
         * @return lowercase charclass
         */
        public static RegexSet lowercase() {
            return rangedSet("a", "z");
        }

        /**
         * Constructs an upper-case alphabetic charclass.
         * [A-Z] this uses {@link RegexSet}. Equivalent to
         * \p{Upper} in java
         *
         * @return uppercase charclass
         */
        public static RegexSet uppercase() {
            return rangedSet("A", "Z");
        }

        /**
         * Constructs the ascii character set 0-127
         *
         * @return ascii charset
         */
        public static RegexSet ascii() {
            return rangedSet(0x00, 0x7F);
        }

        /**
         * Constructs the extended ascii character set 0-255
         *
         * @return ascii charset
         */
        public static RegexSet ascii2() {
            return rangedSet(0x00, 0xFF);
        }

        /**
         * Constructs an alphabetic charclass containing
         * [a-zA-Z] this uses {@link RegexSet}
         *
         * @return alphabetic charclass
         */
        public static RegexSet alphabetic() {
            return lowercase()
                    .union(uppercase());
        }

        /**
         * Constructs an numeric charclass [0-9]
         * This is equivalent to \d in any regex flavor.
         *
         * @return numeric charclass
         */
        public static RegexSet digit() {
            return rangedSet("0", "9");
        }

        /**
         * Constructs an negated numeric charclass [^0-9]
         * This is equivalent to \D in any regex flavor.
         *
         * @return numeric charclass
         */
        public static RegexSet notDigit() {
            return negated(digit());
        }

        /**
         * Constructs an alphanumeric charclass [a-zA-Z0-9]
         *
         * @return alphanumeric charclass
         */
        public static RegexSet alphanumeric() {
            return alphabetic().union(digit());
        }

        /**
         * Constructs an punctuation charclass using one of these
         * characters <code>!"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\]^_`{|}~</code>
         *
         * @return punctuation charclass
         */
        public static RegexSet punctuation() {
            final String elements = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            return simpleSet(elements.split("")/*split into char*/);
        }

        /**
         * Constructs an visible character class using {@code alphanumeric}
         * and {@code punctuation}.
         *
         * @return graphical charclass
         */
        public static RegexSet graphical() {
            return alphanumeric().union(punctuation());
        }

        /**
         * Constructs an printable character class using {@code graphical}
         * and a space character.
         *
         * @return printable charclass
         */
        public static RegexSet printableChar() {
            return graphical().union(simpleSet(0x20/*space*/));
        }

        /**
         * Constructs an blank space charclass. This simple
         * class includes space and horizontal tab.
         *
         * @return blank-space charclass
         */
        public static RegexSet blank() {
            return simpleSet(0x09/*h-tab*/, 0x20/*space*/);
        }

        /**
         * Constructs an hexadecimal character class
         *
         * @return hex charclass
         */
        public static RegexSet hexDigit() {
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
        public static RegexSet whitespace() {
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
        public static RegexSet notWhitespace() {
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
        public static RegexSet word() {
            return alphanumeric().union(simpleSet("_"));
        }

        /**
         * Constructs an negated  word char class equivalent to \W
         * which includes [^0-9A-Za-z_]
         *
         * @return negated word charclass
         */
        public static RegexSet notWord() {
            return negated(word());
        }

    }

    public static class EscapeSequences {

        public static RegexSet space() {
            return simpleSet(" ");
        }

        public static RegexSet backslash() {
            return simpleSet("\\"); // \
        }

        public static RegexSet doubleQuotes() {
            return simpleSet("\""); // "
        }

        public static RegexSet singleQuote() {
            return simpleSet("'"); // '
        }

        public static RegexSet backtick() {
            return simpleSet("`"); // `
        }

        public static RegexSet bell() {
            return simpleSet(0x07); // \a
        }

        public static RegexSet horizontalTab() {
            // \h 	A horizontal whitespace character: [ \t\xA0\u1680\u180e\u2000-\u200a\u202f\u205f\u3000]
            // \H 	A non-horizontal whitespace character: [^\h]
            return simpleSet(0x09); // \t
        }

        public static RegexSet linebreak() {
            return simpleSet(0x0A); // \n
        }

        public static RegexSet verticalTab() {
            // Re consider:
            // \v 	A vertical whitespace character: [\n\x0B\f\r\x85\u2028\u2029]
            // \V 	A non-vertical whitespace character: [^\v]
            return simpleSet(0x0B);
        }

        public static RegexSet formfeed() {
            return simpleSet(0x0C); // \v
        }

        public static RegexSet carriageReturn() {
            return simpleSet(0x0D); // \r
        }

    }

}
