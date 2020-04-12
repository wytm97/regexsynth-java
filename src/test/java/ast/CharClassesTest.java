package ast;

import com.google.re2j.Pattern;
import core.RegexSynth;
import org.junit.Test;

import static ast.CharClasses.EscapeSequences.*;
import static ast.CharClasses.Posix.*;
import static ast.CharClasses.anything;
import static ast.CharClasses.rangedSet;
import static ast.Quantifiers.exactly;
import static core.RegexSynth.regexp;
import static org.junit.Assert.assertEquals;

public final class CharClassesTest {

    @Test
    public void itShouldAppendMatchAnyCharacterAtPosition() {
        final Pattern expression = RegexSynth.compile(
                regexp(exactly(5, anything()))
        );
        assertEquals(expression.pattern(), ".{5}");
    }

    @Test
    public void itShouldCreateCorrectPOSIXLowerCaseCharClass() {
        final RegexSet set = lowercaseChar();
        assertEquals(set.toRegex().toString(), "[a-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXUpperCaseCharClass() {
        final RegexSet set = uppercaseChar();
        assertEquals(set.toRegex().toString(), "[A-Z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXAlphabeticCharClass() {
        final RegexSet set = alphabeticChar();
        assertEquals(set.toRegex().toString(), "[A-Za-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXDigitCharClass() {
        final RegexSet set = digit();
        assertEquals(set.toRegex().toString(), "[0-9]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXNotDigitCharClass() {
        final RegexSet set = notDigit();
        assertEquals(set.toRegex().toString(), "[^0-9]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXAlphanumericCharClass() {
        final RegexSet set = alphanumericChar();
        assertEquals(set.toRegex().toString(), "[0-9A-Za-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXPunctCharClass() {
        final RegexSet set = punctuationChar();
        assertEquals(set.toRegex().toString(), "[!-\\/:-@[-`{-~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXGraphCharClass() {
        final RegexSet set = graphicalChar();
        assertEquals(set.toRegex().toString(), "[!-~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXPrintableCharClass() {
        final RegexSet set = printableChar();
        assertEquals(set.toRegex().toString(), "[ -~]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXBlankCharClass() {
        final RegexSet set = blankChar();
        assertEquals(set.toRegex().toString(), "[\\x09 ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXHexDigitCharClass() {
        final RegexSet set = hexDigit();
        assertEquals(set.toRegex().toString(), "[0-9A-Fa-f]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWhitespaceCharClass() {
        final RegexSet set = whitespaceChar();
        assertEquals(set.toRegex().toString(), "[\\x09-\\x0D ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXNonWhitespaceCharClass() {
        final RegexSet set = notWhitespace();
        assertEquals(set.toRegex().toString(), "[^\\x09-\\x0D ]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWordCharClass() {
        final RegexSet set = word();
        assertEquals(set.toRegex().toString(), "[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldCreateCorrectPOSIXWorNotWordCharClass() {
        final RegexSet set = notWord();
        assertEquals(set.toRegex().toString(), "[^0-9A-Z_a-z]");
    }

    @Test
    public void itShouldReturnCorrectEscapeSequence() {
        final RegexSet backslash = backslash();
        final RegexSet bell = bell();
        final RegexSet horizontalTab = horizontalTab();
        final RegexSet linebreak = linebreak();
        final RegexSet verticalTab = verticalTab();
        final RegexSet formfeed = formfeed();
        final RegexSet carriageReturn = carriageReturn();
        assertEquals(backslash.toRegex().toString(), "\\\\");
        assertEquals(bell.toRegex().toString(), "\\x07");
        assertEquals(horizontalTab.toRegex().toString(), "\\x09");
        assertEquals(linebreak.toRegex().toString(), "\\x0A");
        assertEquals(verticalTab.toRegex().toString(), "\\x0B");
        assertEquals(formfeed.toRegex().toString(), "\\x0C");
        assertEquals(carriageReturn.toRegex().toString(), "\\x0D");
    }

    @Test
    public void itShouldCreateAllAsciiCharClassRange() {
        final RegexSet ascii = asciiChar();
        assertEquals(ascii.toRegex().toString(), "[\\x00-\\x7F]");
    }

    @Test
    public void itShouldCreateAllExtendedAsciiCharClassRange() {
        final RegexSet ascii = ascii2Char();
        assertEquals(ascii.toRegex().toString(), "[\\x00-ÿ]");
    }

    @Test
    public void itShouldCreateARangedCharClassWhenGivenTwoCodepoints() {
        final String from = "\uD83C\uDF11"; // 🌑
        final String to = "\uD83C\uDF1D"; // 🌝
        final RegexSet regexSet = rangedSet(from, to);
        assertEquals(regexSet.toRegex().toString(), "[\\x{1f311}-\\x{1f31d}]");
    }

}
