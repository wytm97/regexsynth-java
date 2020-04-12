package ast;

import com.google.re2j.Pattern;
import core.RegexSynth;
import org.junit.Test;

import static ast.Anchors.*;
import static ast.CharClasses.Posix.alphabeticChar;
import static ast.CharClasses.Posix.word;
import static ast.Groups.captureGroup;
import static ast.Literals.literal;
import static ast.Quantifiers.oneOrMoreTimes;
import static org.junit.Assert.assertEquals;

public final class AnchorsTest {

    @Test
    public void itShouldAppendAWordBoundaryAtPosition() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        captureGroup(wordBoundary(), word())
                )
        );
        assertEquals(expression.pattern(), "(\\b[0-9A-Z_a-z])");
    }

    @Test
    public void itShouldAppendANonWordBoundaryAtPosition() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        nonWordBoundary(),
                        word()
                )
        );
        assertEquals(expression.pattern(), "\\B[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendAStartOfLineAssertionAtPosition() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        startOfLine(),
                        word()
                )
        );
        assertEquals(expression.pattern(), "^[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendEndOfLineAssertionAtPosition() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        startOfLine(),
                        oneOrMoreTimes(word()),
                        endOfLine(false)
                )
        );
        assertEquals(expression.pattern(), "^[0-9A-Z_a-z]+$");
    }

    @Test
    public void itShouldAppendEndOfLineAssertionWithOptionalCarriageReturnAtPosition() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        startOfLine(),
                        oneOrMoreTimes(word()),
                        endOfLine(true)
                )
        );
        assertEquals(expression.pattern(), "^[0-9A-Z_a-z]+\\x0D?$");
    }

    @Test
    public void itShouldAppendStartOfTextAssertionAtPosition() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        startOfText(),
                        word()
                )
        );
        assertEquals(expression.pattern(), "\\A[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendEndOfTextAssertionAtPosition() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        word(),
                        endOfText()
                )
        );
        assertEquals(expression.pattern(), "[0-9A-Z_a-z]\\z");
    }

    @Test
    public void itShouldWrapTheExpressionInLineMatcher() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        exactLineMatch(
                                wordBoundary(),
                                word()
                        )
                )
        );
        assertEquals(expression.pattern(), "^\\b[0-9A-Z_a-z]$");
    }

    @Test
    public void itShouldWrapTheExpressionInWordBoundary() {
        final Pattern expression = RegexSynth.compile(
                RegexSynth.regexp(
                        exactWordBoundary(
                                literal("p"),
                                oneOrMoreTimes(alphabeticChar()),
                                literal("p")
                        )
                )
        );
        assertEquals(expression.pattern(), "\\bp[A-Za-z]+p\\b");
    }

}
