package dev.yasint.regexsynth.synthesis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TrieExpressionTest {

    @Test
    public void shouldHandleUnicodeCharactersProperly() {

        final String s1 = "🌚💀 Zen";
        final String s2 = "🌚💀 Zero";

        TrieExpression trieExpression = new TrieExpression();
        trieExpression.insert(s1);
        trieExpression.insert(s2);

        assertEquals(
                trieExpression.toRegex().toString(),
                "\uD83C\uDF1A\uD83D\uDC80 Ze(?:ro|n)"
        );

    }

}
