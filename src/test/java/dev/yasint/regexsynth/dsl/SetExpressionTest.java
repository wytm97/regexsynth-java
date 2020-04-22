package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import dev.yasint.regexsynth.unicode.UnicodeScript;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.CharClasses.*;
import static org.junit.Assert.assertEquals;

public final class SetExpressionTest {

    @Test
    public void itShouldCreateANonNegatedCharacterClass() {
        final SetExpression simpleSet = simpleSet("A", "B", "D", "E", "C");
        assertEquals(simpleSet.toRegex().toString(), "[A-E]");
    }

    @Test
    public void itShouldCreateANegatedCharacterClass() {
        final SetExpression simpleSet = negated(simpleSet("a", "b", "c", "d", "Z"));
        assertEquals(simpleSet.toRegex().toString(), "[^Za-d]");
    }

    @Test
    public void itShouldCreateASimpleCharacterClassWithoutRanges() {
        final SetExpression simpleSet = simpleSet("a", "d", "f", "h", "Z");
        assertEquals(simpleSet.toRegex().toString(), "[Zadfh]");
    }

    @Test
    public void itShouldDoASetUnionOperationOnTwoSets() {
        final SetExpression rangedSet = rangedSet("A", "Z");
        final SetExpression simpleSet = simpleSet("a", "d", "f", "h", "Z");
        rangedSet.union(simpleSet); // Will mutate the rangedSet
        assertEquals(rangedSet.toRegex().toString(), "[A-Zadfh]");
    }

    @Test
    public void itShouldDoASetIntersectionOperationOnTwoSets() {
        final SetExpression setA = rangedSet("A", "Z").union(rangedSet("a", "z"));
        final SetExpression setB = simpleSet("d", "e", "f");
        setA.intersection(setB);
        assertEquals(setA.toRegex().toString(), "[d-f]");
    }

    @Test
    public void itShouldDoADifferenceOperationOnTwoSets() {
        final SetExpression setA = rangedSet("A", "Z").union(rangedSet("a", "z"));
        final SetExpression setB = rangedSet("M", "P").union(rangedSet("m", "p"));
        setA.difference(setB);
        assertEquals(setA.toRegex().toString(), "[A-LQ-Za-lq-z]");
    }

    @Test
    public void itShouldDoASetUnionOperationOnInlineRegex() {
        Pattern expression = new RegexSynth(
                rangedSet("1", "3").union(rangedSet("4", "6"))
        ).compile();
        assertEquals(expression.pattern(), "[1-6]");
    }

    @Test
    public void itShouldDoASetIntersectionOperationOnInlineRegex() {
        Pattern expression = new RegexSynth(
                rangedSet("1", "3").intersection(rangedSet("4", "6"))
        ).compile();
        assertEquals(expression.pattern(), "");
    }

    @Test
    public void itShouldDoASetDifferenceOperationOnInlineRegex() {
        Pattern expression = new RegexSynth(
                rangedSet("1", "3").difference(simpleSet("2", "4", "5", "6"))
        ).compile();
        assertEquals(expression.pattern(), "[13]");
    }

    @Test
    public void itShouldAppendANonNegatedUnicodeClassesToASetExpression() {
        final Pattern expression = new RegexSynth(
                simpleSet("-", ".").withUnicodeClass(UnicodeScript.SINHALA, false)
        ).compile();
        System.out.println(expression.pattern());
        assertEquals(expression.pattern(), "[\\-.\\p{Sinhala}]");
    }

    @Test
    public void itShouldAppendANegatedUnicodeClassesToASetExpression() {
        final Pattern expression = new RegexSynth(
                simpleSet("-", ".").withUnicodeClass(UnicodeScript.SINHALA, true)
        ).compile();
        System.out.println(expression.pattern());
        assertEquals(expression.pattern(), "[\\-.\\P{Sinhala}]");
    }

}
