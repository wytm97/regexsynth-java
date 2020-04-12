package ast;

import com.google.re2j.Pattern;
import core.RegexSynth;
import core.UnicodeScript;
import org.junit.Test;

import static ast.Literals.*;
import static core.RegexSynth.regexp;
import static org.junit.Assert.assertEquals;

public final class LiteralsTest {

    @Test
    public void itShouldEscapeAllSpecialCharacters() {
        Pattern pattern = RegexSynth.compile(
                regexp(literal("https://swtch.com/~rsc/regexp&id=1"))
        );
        assertEquals(pattern.pattern(), "https:\\/\\/swtch\\.com\\/~rsc\\/regexp&id\\=1");
    }

    @Test
    public void itShouldCreateStrictQuoteString() {
        Pattern pattern = RegexSynth.compile(
                regexp(quotedLiteral("https://swtch.com/~rsc/regexp&id=1"))
        );
        assertEquals(pattern.pattern(), "\\Qhttps://swtch.com/~rsc/regexp&id=1\\E");
    }

    @Test
    public void itShouldCreateANonNegatedUnicodeScriptBlock() {
        Pattern pattern = RegexSynth.compile(
                regexp(unicodeClass(UnicodeScript.SINHALA, false))
        );
        assertEquals(pattern.pattern(), "\\p{Sinhala}");
    }

    @Test
    public void itShouldCreateANegatedUnicodeScriptBlock() {
        Pattern pattern = RegexSynth.compile(
                regexp(unicodeClass(UnicodeScript.ARMENIAN, true))
        );
        assertEquals(pattern.pattern(), "\\P{Armenian}");
    }

}
