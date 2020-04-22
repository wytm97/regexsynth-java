package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.CharClasses.EscapeSequences.*;
import static dev.yasint.regexsynth.ast.CharClasses.Posix.*;
import static dev.yasint.regexsynth.ast.CharClasses.anything;
import static dev.yasint.regexsynth.ast.CharClasses.rangedSet;
import static dev.yasint.regexsynth.ast.Quantifiers.exactly;
import static org.junit.Assert.assertEquals;

public final class CharClassesTest {

    @Test
    public void itShouldAppendMatchAnyCharacterAtPosition() {
        final Pattern expression = new RegexSynth(
                exactly(5, anything())
        ).compile();
        assertEquals(expression.pattern(), "(?:.){5}");
    }

    @Test
    public void itShouldCreateCorrectPOSIXLowerCaseCharClass() {
        final SetExpression set = lowercase();
        assertEquals(set.toRegex().toString(), "[a-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXUpperCaseCharClass() {
        final SetExpression set = uppercase();
        assertEquals(set.toRegex().toString(), "[A-Z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXAlphabeticCharClass() {
        final SetExpression set = alphabetic();
        assertEquals(set.toRegex().toString(), "[A-Za-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXDigitCharClass() {
        final SetExpression set = digit();
        assertEquals(set.toRegex().toString(), "[0-9]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXNotDigitCharClass() {
        final SetExpression set = notDigit();
        assertEquals(set.toRegex().toString(), "[^0-9]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXAlphanumericCharClass() {
        final SetExpression set = alphanumeric();
        assertEquals(set.toRegex().toString(), "[0-9A-Za-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXPunctCharClass() {
        final SetExpression set = punctuation();
        assertEquals(set.toRegex().toString(), "[!-\\/:-@[-\\`{-~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXGraphCharClass() {
        final SetExpression set = graphical();
        assertEquals(set.toRegex().toString(), "[!-~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXPrintableCharClass() {
        final SetExpression set = printable();
        assertEquals(set.toRegex().toString(), "[ -~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXBlankCharClass() {
        final SetExpression set = blank();
        assertEquals(set.toRegex().toString(), "[\\x09 ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXHexDigitCharClass() {
        final SetExpression set = hexDigit();
        assertEquals(set.toRegex().toString(), "[0-9A-Fa-f]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWhitespaceCharClass() {
        final SetExpression set = whitespace();
        assertEquals(set.toRegex().toString(), "[\\x09-\\x0D ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXNonWhitespaceCharClass() {
        final SetExpression set = notWhitespace();
        assertEquals(set.toRegex().toString(), "[^\\x09-\\x0D ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWordCharClass() {
        final SetExpression set = word();
        assertEquals(set.toRegex().toString(), "[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWorNotWordCharClass() {
        final SetExpression set = notWord();
        assertEquals(set.toRegex().toString(), "[^0-9A-Z_a-z]");
    }

    @Test
    public void itShouldReturnCorrectEscapeSequence() {
        final SetExpression backslash = backslash();
        final SetExpression doubleQuotes = doubleQuotes();
        final SetExpression singleQuote = singleQuote();
        final SetExpression backtick = backtick();
        final SetExpression bell = bell();
        final SetExpression horizontalTab = horizontalTab();
        final SetExpression linebreak = linebreak();
        final SetExpression verticalTab = verticalTab();
        final SetExpression formfeed = formfeed();
        final SetExpression carriageReturn = carriageReturn();
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
        final SetExpression ascii = ascii();
        assertEquals(ascii.toRegex().toString(), "[\\x00-\\x7F]");
    }

    @Test
    public void itShouldCreateAllExtendedAsciiCharClassRange() {
        final SetExpression ascii = ascii2();
        assertEquals(ascii.toRegex().toString(), "[\\x00-ÿ]");
    }

    @Test
    public void itShouldCreateARangedCharClassWhenGivenTwoCodepoints() {
        final String from = "\uD83C\uDF11"; // 🌑
        final String to = "\uD83C\uDF1D"; // 🌝
        final SetExpression regexSet = rangedSet(from, to);
        assertEquals(regexSet.toRegex().toString(), "[\\x{1f311}-\\x{1f31d}]");
    }

}
