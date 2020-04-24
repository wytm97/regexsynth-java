package dev.yasint.regexsynth.dsl;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.api.RegexSynth;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.CharClasses.EscapeSequences.*;
import static dev.yasint.regexsynth.dsl.CharClasses.Posix.*;
import static dev.yasint.regexsynth.dsl.CharClasses.anything;
import static dev.yasint.regexsynth.dsl.CharClasses.rangedSet;
import static dev.yasint.regexsynth.dsl.Repetition.exactly;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class CharClassesTest {

    @Test
    public void itShouldAppendMatchAnyCharacterAtPosition() {
        final Pattern expression = new RegexSynth(
                exactly(5, anything())
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(?:.){5}");
    }

    @Test
    public void itShouldCreateCorrectPOSIXLowerCaseCharClass() {
        final Expression set = lowercase();
        assertEquals(set.toRegex().toString(), "[a-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXUpperCaseCharClass() {
        final Expression set = uppercase();
        assertEquals(set.toRegex().toString(), "[A-Z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXAlphabeticCharClass() {
        final Expression set = alphabetic();
        assertEquals(set.toRegex().toString(), "[A-Za-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXDigitCharClass() {
        final Expression set = digit();
        assertEquals(set.toRegex().toString(), "[0-9]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXNotDigitCharClass() {
        final Expression set = notDigit();
        assertEquals(set.toRegex().toString(), "[^0-9]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXAlphanumericCharClass() {
        final Expression set = alphanumeric();
        assertEquals(set.toRegex().toString(), "[0-9A-Za-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXPunctCharClass() {
        final Expression set = punctuation();
        assertEquals(set.toRegex().toString(), "[!-\\/:-@[-\\`{-~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXGraphCharClass() {
        final Expression set = graphical();
        assertEquals(set.toRegex().toString(), "[!-~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXPrintableCharClass() {
        final Expression set = printable();
        assertEquals(set.toRegex().toString(), "[ -~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXBlankCharClass() {
        final Expression set = blank();
        assertEquals(set.toRegex().toString(), "[\\x09 ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXHexDigitCharClass() {
        final Expression set = hexDigit();
        assertEquals(set.toRegex().toString(), "[0-9A-Fa-f]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWhitespaceCharClass() {
        final Expression set = whitespace();
        assertEquals(set.toRegex().toString(), "[\\x09-\\x0D ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXNonWhitespaceCharClass() {
        final Expression set = notWhitespace();
        assertEquals(set.toRegex().toString(), "[^\\x09-\\x0D ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWordCharClass() {
        final Expression set = word();
        assertEquals(set.toRegex().toString(), "[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWorNotWordCharClass() {
        final Expression set = notWord();
        assertEquals(set.toRegex().toString(), "[^0-9A-Z_a-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXControlCharClass() {
        final Expression set = control();
        assertEquals(set.toRegex().toString(), "[\\x00-\\x1F\\x7F]");
    }

    @Test
    public void itShouldReturnCorrectEscapeSequence() {
        final Expression backslash = backslash();
        final Expression doubleQuotes = doubleQuotes();
        final Expression singleQuote = singleQuote();
        final Expression backtick = backtick();
        final Expression bell = bell();
        final Expression horizontalTab = horizontalTab();
        final Expression linebreak = linebreak();
        final Expression verticalTab = verticalTab();
        final Expression formfeed = formfeed();
        final Expression carriageReturn = carriageReturn();
        assertEquals(backslash.toRegex().toString(), "\\\\");
        assertEquals(doubleQuotes.toRegex().toString(), "\\\"");
        assertEquals(singleQuote.toRegex().toString(), "\\'");
        assertEquals(backtick.toRegex().toString(), "\\`");
        assertEquals(bell.toRegex().toString(), "\\x07");
        assertEquals(horizontalTab.toRegex().toString(), "\\x09");
        assertEquals(linebreak.toRegex().toString(), "\\x0A");
        assertEquals(verticalTab.toRegex().toString(), "\\x0B");
        assertEquals(formfeed.toRegex().toString(), "\\x0C");
        assertEquals(carriageReturn.toRegex().toString(), "\\x0D");
    }

    @Test
    public void itShouldCreateAllAsciiCharClassRange() {
        final Expression ascii = ascii();
        assertEquals(ascii.toRegex().toString(), "[\\x00-\\x7F]");
    }

    @Test
    public void itShouldCreateAllExtendedAsciiCharClassRange() {
        final Expression ascii = ascii2();
        assertEquals(ascii.toRegex().toString(), "[\\x00-ÿ]");
    }

    @Test
    public void itShouldCreateARangedCharClassWhenGivenTwoCodepoints() {
        final String from = "\uD83C\uDF11"; // 🌑
        final String to = "\uD83C\uDF1D"; // 🌝
        final Expression regexSet = rangedSet(from, to);
        assertEquals(regexSet.toRegex().toString(), "[\\x{1f311}-\\x{1f31d}]");
    }

}
