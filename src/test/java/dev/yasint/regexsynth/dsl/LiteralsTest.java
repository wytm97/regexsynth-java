package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import dev.yasint.regexsynth.unicode.UnicodeScript;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.Literals.*;
import static org.junit.Assert.assertEquals;

public final class LiteralsTest {

    @Test
    public void itShouldEscapeAllSpecialCharacters() {
        Pattern pattern = new RegexSynth(
                literal("https://swtch.com/~rsc/regexp&id=1")
        ).compile();
        assertEquals(pattern.pattern(), "https:\\/\\/swtch\\.com\\/~rsc\\/regexp&id\\=1");
    }

    @Test
    public void itShouldCreateStrictQuoteString() {
        Pattern pattern = new RegexSynth(
                quotedLiteral("https://swtch.com/~rsc/regexp&id=1")
        ).compile();
        assertEquals(pattern.pattern(), "\\Qhttps://swtch.com/~rsc/regexp&id=1\\E");
    }

    @Test
    public void itShouldCreateANonNegatedUnicodeScriptBlock() {
        Pattern pattern = new RegexSynth(
                unicodeClass(UnicodeScript.SINHALA, false)
        ).compile();
        assertEquals(pattern.pattern(), "\\p{Sinhala}");
    }

    @Test
    public void itShouldCreateANegatedUnicodeScriptBlock() {
        Pattern pattern = new RegexSynth(
                unicodeClass(UnicodeScript.ARMENIAN, true)
        ).compile();
        assertEquals(pattern.pattern(), "\\P{Armenian}");
    }

}
