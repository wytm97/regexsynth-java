package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.Anchors.*;
import static dev.yasint.regexsynth.ast.CharClasses.Posix.alphabetic;
import static dev.yasint.regexsynth.ast.CharClasses.Posix.word;
import static dev.yasint.regexsynth.ast.Groups.captureGroup;
import static dev.yasint.regexsynth.ast.Literals.literal;
import static dev.yasint.regexsynth.ast.Quantifiers.oneOrMoreTimes;
import static org.junit.Assert.assertEquals;

public final class AnchorsTest {

    @Test
    public void itShouldAppendAWordBoundaryAtPosition() {
        final Pattern expression = new RegexSynth(
                captureGroup(wordBoundary(), word())
        ).compile();
        assertEquals(expression.pattern(), "(\\b[0-9A-Z_a-z])");
    }

    @Test
    public void itShouldAppendANonWordBoundaryAtPosition() {
        final Pattern expression = new RegexSynth(
                nonWordBoundary(),
                word()
        ).compile();
        assertEquals(expression.pattern(), "\\B[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendAStartOfLineAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfLine(),
                word()
        ).compile();
        assertEquals(expression.pattern(), "^[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendEndOfLineAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfLine(),
                oneOrMoreTimes(word()),
                endOfLine(false)
        ).compile();
        assertEquals(expression.pattern(), "^(?:[0-9A-Z_a-z])+$");
    }

    @Test
    public void itShouldAppendEndOfLineAssertionWithOptionalCarriageReturnAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfLine(),
                oneOrMoreTimes(word()),
                endOfLine(true)
        ).compile();
        assertEquals(expression.pattern(), "^(?:[0-9A-Z_a-z])+\\x0D?$");
    }

    @Test
    public void itShouldAppendStartOfTextAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfText(),
                word()
        ).compile();
        assertEquals(expression.pattern(), "\\A[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendEndOfTextAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                word(),
                endOfText()
        ).compile();
        assertEquals(expression.pattern(), "[0-9A-Z_a-z]\\z");
    }

    @Test
    public void itShouldWrapTheExpressionInLineMatcher() {
        final Pattern expression = new RegexSynth(
                exactLineMatch(
                        wordBoundary(),
                        word()
                )
        ).compile();
        assertEquals(expression.pattern(), "^\\b[0-9A-Z_a-z]$");
    }

    @Test
    public void itShouldWrapTheExpressionInWordBoundary() {
        final Pattern expression = new RegexSynth(
                exactWordBoundary(
                        literal("p"),
                        oneOrMoreTimes(alphabetic()),
                        literal("p")
                )
        ).compile();
        assertEquals(expression.pattern(), "\\bp(?:[A-Za-z])+p\\b");
    }

}
