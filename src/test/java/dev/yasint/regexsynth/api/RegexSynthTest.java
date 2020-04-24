package dev.yasint.regexsynth.api;

import com.google.re2j.Pattern;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static dev.yasint.regexsynth.dsl.Anchors.*;
import static dev.yasint.regexsynth.dsl.CharClasses.EscapeSequences.linebreak;
import static dev.yasint.regexsynth.dsl.CharClasses.EscapeSequences.space;
import static dev.yasint.regexsynth.dsl.CharClasses.Posix.*;
import static dev.yasint.regexsynth.dsl.CharClasses.*;
import static dev.yasint.regexsynth.dsl.Groups.captureGroup;
import static dev.yasint.regexsynth.dsl.Groups.namedCaptureGroup;
import static dev.yasint.regexsynth.dsl.Literals.literal;
import static dev.yasint.regexsynth.dsl.Numeric.integerRange;
import static dev.yasint.regexsynth.dsl.Numeric.leadingZero;
import static dev.yasint.regexsynth.dsl.Operators.concat;
import static dev.yasint.regexsynth.dsl.Operators.either;
import static dev.yasint.regexsynth.dsl.Repetition.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class RegexSynthTest {

}
