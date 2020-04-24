package dev.yasint.regexsynth.dsl;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.RegexSynth;
import dev.yasint.regexsynth.unicode.UnicodeScript;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.Literals.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class LiteralsTest {

    @Test
    public void itShouldEscapeAllSpecialCharacters() {
        Pattern pattern = new RegexSynth(
                literal("https://swtch.com/~rsc/regexp&id=1")
        ).compile().getPattern();
        assertEquals(pattern.pattern(), "https:\\/\\/swtch\\.com\\/~rsc\\/regexp&id\\=1");
    }

    @Test
    public void itShouldCreateStrictQuoteString() {
        Pattern pattern = new RegexSynth(
                quotedLiteral("https://swtch.com/~rsc/regexp&id=1")
        ).compile().getPattern();
        assertEquals(pattern.pattern(), "\\Qhttps://swtch.com/~rsc/regexp&id=1\\E");
    }

    @Test
    public void itShouldCreateANonNegatedUnicodeScriptBlock() {
        Pattern pattern = new RegexSynth(
                unicodeScriptLiteral(UnicodeScript.SINHALA, false)
        ).compile().getPattern();
        assertEquals(pattern.pattern(), "\\p{Sinhala}");
    }

    @Test
    public void itShouldCreateANegatedUnicodeScriptBlock() {
        Pattern pattern = new RegexSynth(
                unicodeScriptLiteral(UnicodeScript.ARMENIAN, true)
        ).compile().getPattern();
        assertEquals(pattern.pattern(), "\\P{Armenian}");
    }

}
