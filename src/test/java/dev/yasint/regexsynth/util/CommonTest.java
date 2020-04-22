package dev.yasint.regexsynth.util;

import dev.yasint.regexsynth.exceptions.InvalidGroupNameException;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.util.Common.asRegexGroupName;
import static dev.yasint.regexsynth.util.Common.asRegexLiteral;
import static org.junit.jupiter.api.Assertions.*;

public final class CommonTest {

    @Test
    public void itShouldEscapeAllSpecialConstructs() {
        assertEquals(asRegexLiteral("<"), "\\<");
        assertEquals(asRegexLiteral("("), "\\(");
        assertEquals(asRegexLiteral("["), "\\[");
        assertEquals(asRegexLiteral("{"), "\\{");
        assertEquals(asRegexLiteral("\\"), "\\\\");
        assertEquals(asRegexLiteral("^"), "\\^");
        assertEquals(asRegexLiteral("-"), "\\-");
        assertEquals(asRegexLiteral("="), "\\=");
        assertEquals(asRegexLiteral("$"), "\\$");
        assertEquals(asRegexLiteral("!"), "\\!");
        assertEquals(asRegexLiteral("|"), "\\|");
        assertEquals(asRegexLiteral("]"), "\\]");
        assertEquals(asRegexLiteral("}"), "\\}");
        assertEquals(asRegexLiteral(")"), "\\)");
        assertEquals(asRegexLiteral("?"), "\\?");
        assertEquals(asRegexLiteral("*"), "\\*");
        assertEquals(asRegexLiteral("+"), "\\+");
        assertEquals(asRegexLiteral("."), "\\.");
        assertEquals(asRegexLiteral(">"), "\\>");
        assertEquals(asRegexLiteral("/"), "\\/");
    }

    @Test
    public void itShouldThrowExceptionsForInvalidGroupNames() {
        assertThrows(
                InvalidGroupNameException.class,
                () -> asRegexGroupName("--wowVeryWrong")
        );
        assertThrows(
                InvalidGroupNameException.class,
                () -> asRegexGroupName("1wowVeryWrong")
        );
        assertThrows(
                InvalidGroupNameException.class,
                () -> asRegexGroupName("+wowVeryWrong")
        );
    }

}
