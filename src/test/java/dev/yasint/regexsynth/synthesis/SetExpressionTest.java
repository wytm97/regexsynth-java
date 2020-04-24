package dev.yasint.regexsynth.synthesis;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.api.RegexSynth;
import dev.yasint.regexsynth.unicode.UnicodeScript;
import org.junit.jupiter.api.Test;

import static dev.yasint.regexsynth.dsl.CharClasses.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class SetExpressionTest {

    @Test
    public void itShouldCreateANonNegatedCharacterClass() {
        final Expression simpleSet = simpleSet("A", "B", "D", "E", "C");
        assertEquals(simpleSet.toRegex().toString(), "[A-E]");
    }

    @Test
    public void itShouldCreateANegatedCharacterClass() {
        final Expression simpleSet = negated(simpleSet("a", "b", "c", "d", "Z"));
        assertEquals(simpleSet.toRegex().toString(), "[^Za-d]");
    }

    @Test
    public void itShouldCreateASimpleCharacterClassWithoutRanges() {
        final Expression simpleSet = simpleSet("a", "d", "f", "h", "Z");
        assertEquals(simpleSet.toRegex().toString(), "[Zadfh]");
    }

    @Test
    public void itShouldDoASetUnionOperationOnTwoSets() {
        final Expression set = union(
                rangedSet("A", "Z"),
                simpleSet("a", "d", "f", "h", "Z")
        );
        assertEquals(set.toRegex().toString(), "[A-Zadfh]");
    }

    @Test
    public void itShouldDoASetIntersectionOperationOnTwoSets() {
        assertEquals(
                intersection(
                        union(rangedSet("A", "Z"), rangedSet("a", "z")),
                        simpleSet("d", "e", "f")
                ).toRegex().toString(),
                "[d-f]"
        );
    }

    @Test
    public void itShouldDoADifferenceOperationOnTwoSets() {
        final Expression setA = union(rangedSet("A", "Z"), rangedSet("a", "z"));
        final Expression setB = union(rangedSet("M", "P"), rangedSet("m", "p"));
        assertEquals(difference(setA, setB).toRegex().toString(), "[A-LQ-Za-lq-z]");
    }

    @Test
    public void itShouldDoASetUnionOperationOnInlineRegex() {
        Pattern expression = new RegexSynth(
                union(rangedSet("1", "3"), rangedSet("4", "6"))
        ).compile().getPattern();
        assertEquals(expression.pattern(), "[1-6]");
    }

    @Test
    public void itShouldDoASetIntersectionOperationOnInlineRegex() {
        Pattern expression = new RegexSynth(
                intersection(rangedSet("1", "3"), rangedSet("4", "6"))
        ).compile().getPattern();
        assertEquals(expression.pattern(), "");
    }

    @Test
    public void itShouldDoASetDifferenceOperationOnInlineRegex() {
        Pattern expression = new RegexSynth(
                difference(rangedSet("1", "3"), simpleSet("2", "4", "5", "6"))
        ).compile().getPattern();
        assertEquals(expression.pattern(), "[13]");
    }

    @Test
    public void itShouldAppendANonNegatedUnicodeClassesToASetExpression() {
        final Pattern expression = new RegexSynth(
                includeUnicodeScript(simpleSet("-", "."), UnicodeScript.SINHALA, false)
        ).compile().getPattern();
        System.out.println(expression.pattern());
        assertEquals(expression.pattern(), "[\\-.\\p{Sinhala}]");
    }

    @Test
    public void itShouldAppendANegatedUnicodeClassesToASetExpression() {
        final Pattern expression = new RegexSynth(
                includeUnicodeScript(simpleSet("-", "."), UnicodeScript.SINHALA, true)
        ).compile().getPattern();
        System.out.println(expression.pattern());
        assertEquals(expression.pattern(), "[\\-.\\P{Sinhala}]");
    }

}
