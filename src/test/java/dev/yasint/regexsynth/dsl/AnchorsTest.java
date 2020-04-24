package dev.yasint.regexsynth.dsl;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.RegexSynth;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.Anchors.*;
import static dev.yasint.regexsynth.dsl.CharClasses.Posix.alphabetic;
import static dev.yasint.regexsynth.dsl.CharClasses.Posix.word;
import static dev.yasint.regexsynth.dsl.Groups.captureGroup;
import static dev.yasint.regexsynth.dsl.Literals.literal;
import static dev.yasint.regexsynth.dsl.Repetition.oneOrMoreTimes;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class AnchorsTest {

    @Test
    public void itShouldAppendAWordBoundaryAtPosition() {
        final Pattern expression = new RegexSynth(
                captureGroup(wordBoundary()
                        .debug(System.out::println), word())
        ).compile().getPattern();
        assertEquals(expression.pattern(), "(\\b[0-9A-Z_a-z])");
    }

    @Test
    public void itShouldAppendANonWordBoundaryAtPosition() {
        final Pattern expression = new RegexSynth(
                nonWordBoundary(),
                word()
        ).compile().getPattern();
        assertEquals(expression.pattern(), "\\B[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendAStartOfLineAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfLine(),
                word()
        ).compile().getPattern();
        assertEquals(expression.pattern(), "^[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendEndOfLineAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfLine(),
                oneOrMoreTimes(word()),
                endOfLine(false)
        ).compile().getPattern();
        assertEquals(expression.pattern(), "^(?:[0-9A-Z_a-z])+$");
    }

    @Test
    public void itShouldAppendEndOfLineAssertionWithOptionalCarriageReturnAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfLine(),
                oneOrMoreTimes(word()),
                endOfLine(true)
        ).compile().getPattern();
        assertEquals(expression.pattern(), "^(?:[0-9A-Z_a-z])+\\x0D?$");
    }

    @Test
    public void itShouldAppendStartOfTextAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                startOfText(),
                word()
        ).compile().getPattern();
        assertEquals(expression.pattern(), "\\A[0-9A-Z_a-z]");
    }

    @Test
    public void itShouldAppendEndOfTextAssertionAtPosition() {
        final Pattern expression = new RegexSynth(
                word(),
                endOfText()
        ).compile().getPattern();
        assertEquals(expression.pattern(), "[0-9A-Z_a-z]\\z");
    }

    @Test
    public void itShouldWrapTheExpressionInLineMatcher() {
        final Pattern expression = new RegexSynth(
                exactLineMatch(
                        wordBoundary(),
                        word()
                )
        ).compile().getPattern();
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
        ).compile().getPattern();
        assertEquals(expression.pattern(), "\\bp(?:[A-Za-z])+p\\b");
    }

}
