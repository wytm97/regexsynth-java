package dev.yasint.regexsynth.ast;

import dev.yasint.regexsynth.core.Expression;
import dev.yasint.regexsynth.core.Utility;

import java.util.Objects;

import static dev.yasint.regexsynth.core.Constructs.PERIOD;

public final class CharClasses {

    /**
     * Matches any character, possibly including newline \n if
     * the 's' {@link dev.yasint.regexsynth.core.RegexSynth.Flags} DOTALL flag is turned on.
     *
     * @return match anything expression
     */
    public static Expression anything() {
        return () -> new StringBuilder(1).append(PERIOD);
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
        if (from == null || to == null) throw new NullPointerException();
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
     * Creates a simple regex charclass i.e. [135]
     *
     * @param characters characters (surrogates or bmp)
     * @return set expression
     */
    public static RegexSet simpleSet(final String... characters) {
        final RegexSet set = new RegexSet(false);
        for (final String c : Objects.requireNonNull(characters))
            set.addChar(Utility.toCodepoint(c));
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
        public static RegexSet lowercaseChar() {
            return rangedSet("a", "z");
        }

        /**
         * Constructs an upper-case alphabetic charclass.
         * [A-Z] this uses {@link RegexSet}. Equivalent to
         * \p{Upper} in java
         *
         * @return uppercase charclass
         */
        public static RegexSet uppercaseChar() {
            return rangedSet("A", "Z");
        }

        /**
         * Constructs the ascii character set 0-127
         *
         * @return ascii charset
         */
        public static RegexSet asciiChar() {
            return rangedSet(0x00, 0x7F);
        }

        /**
         * Constructs the extended ascii character set 0-255
         *
         * @return ascii charset
         */
        public static RegexSet ascii2Char() {
            return rangedSet(0x00, 0xFF);
        }

        /**
         * Constructs an alphabetic charclass containing
         * [a-zA-Z] this uses {@link RegexSet}
         *
         * @return alphabetic charclass
         */
        public static RegexSet alphabeticChar() {
            return lowercaseChar()
                    .union(uppercaseChar());
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
        public static RegexSet alphanumericChar() {
            return alphabeticChar().union(digit());
        }

        /**
         * Constructs an punctuation charclass using one of these
         * characters <code>!"#$%&amp;'()*+,-./:;&lt;=&gt;?@[\]^_`{|}~</code>
         *
         * @return punctuation charclass
         */
        public static RegexSet punctuationChar() {
            final String elements = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
            return simpleSet(elements.split("")/*split into char*/);
        }

        /**
         * Constructs an visible character class using {@code alphanumeric}
         * and {@code punctuation}.
         *
         * @return graphical charclass
         */
        public static RegexSet graphicalChar() {
            return alphanumericChar().union(punctuationChar());
        }

        /**
         * Constructs an printable character class using {@code graphical}
         * and a space character.
         *
         * @return printable charclass
         */
        public static RegexSet printableChar() {
            return graphicalChar().union(simpleSet(0x20/*space*/));
        }

        /**
         * Constructs an blank space charclass. This simple
         * class includes space and horizontal tab.
         *
         * @return blank-space charclass
         */
        public static RegexSet blankChar() {
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
        public static RegexSet whitespaceChar() {
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
            return negated(whitespaceChar());
        }

        /**
         * Constructs an word char class equivalent to \w
         * which includes [0-9A-Za-z_]
         *
         * @return word charclass
         */
        public static RegexSet word() {
            return alphanumericChar().union(simpleSet("_"));
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

        public static RegexSet backslash() {
            return simpleSet("\\"); // \
        }

        public static RegexSet bell() {
            return simpleSet(0x07); // \a
        }

        public static RegexSet horizontalTab() {
            return simpleSet(0x09); // \t
        }

        public static RegexSet linebreak() {
            return simpleSet(0x0A); // \n
        }

        public static RegexSet verticalTab() {
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
