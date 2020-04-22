package dev.yasint.regexsynth.ast;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.core.RegexSynth;
import org.junit.Test;

import static dev.yasint.regexsynth.ast.CharClasses.Posix.*;
import static dev.yasint.regexsynth.ast.Groups.namedCaptureGroup;
import static dev.yasint.regexsynth.ast.Groups.nonCaptureGroup;
import static org.junit.Assert.assertEquals;

public final class GroupsTest {

    @Test
    public void itShouldCreateANonCapturingGroup() {
        final Pattern pattern = new RegexSynth(
                nonCaptureGroup(
                        digit()
                )
        ).compile();
        assertEquals(pattern.pattern(), "(?:[0-9])");
    }

    @Test
    public void itShouldCreateACapturingGroup() {
        final Pattern pattern = new RegexSynth(
                nonCaptureGroup(
                        digit().union(punctuation())
                )
        ).compile();
        assertEquals(pattern.pattern(), "(?:[!-@[-\\`{-~])");
    }

    @Test
    public void itShouldCreateANamedCaptureGroup() {
        final Pattern pattern = new RegexSynth(
                namedCaptureGroup("someName",
                        word().union(punctuation())
                )
        ).compile();
        assertEquals(pattern.pattern(), "(?P<someName>[!-~])");
    }

    @Test(expected = RuntimeException.class)
    public void itShouldThrowAnExceptionIfTheNamedCaptureGroupNameIsInvalid() {
        namedCaptureGroup("- 902 someName",
                word().union(punctuation())
        ).toRegex();
    }

}
