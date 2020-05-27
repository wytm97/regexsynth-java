package dev.yasint.regexsynth.synthesis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class TrieExpressionTest {

    @Test
    public void shouldMinimizeString() {
        TrieExpression trieExpression = new TrieExpression();
        trieExpression.insert("Apple");
        trieExpression.insert("Application");
        trieExpression.insert("Appeal");
        assertEquals(trieExpression.toRegex().toString(), "App(?:eal|l(?:ication|e))");
    }

}
