package dev.yasint.regexsynth.core;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.ast.RegexSet;
import org.junit.Test;

import java.time.Year;

import static dev.yasint.regexsynth.ast.Anchors.exactLineMatch;
import static dev.yasint.regexsynth.ast.CharClasses.Posix.*;
import static dev.yasint.regexsynth.ast.CharClasses.anything;
import static dev.yasint.regexsynth.ast.CharClasses.simpleSet;
import static dev.yasint.regexsynth.ast.Groups.captureGroup;
import static dev.yasint.regexsynth.ast.Groups.namedCaptureGroup;
import static dev.yasint.regexsynth.ast.Literals.literal;
import static dev.yasint.regexsynth.ast.Numeric.integerRange;
import static dev.yasint.regexsynth.ast.Numeric.leadingZero;
import static dev.yasint.regexsynth.ast.Operators.either;
import static dev.yasint.regexsynth.ast.Quantifiers.*;
import static dev.yasint.regexsynth.core.RegexSynth.regexp;
import static org.junit.Assert.assertEquals;

public final class RegexSynthTest {

    @Test
    public void itShouldCreateTheExpectedDateExpression() {

        // Let's say we want to match dates like this format: 2019-Mar-15
        //
        // Requirement 1: Months as Abbreviation -> (Jan, Feb, Mar, etc...)
        // Requirement 2: Match only between 2012-Jan-01 upto current year
        // Requirement 3: Extract month and day
        //
        // Note: dates can have a leading zero 01, 02, 03, etc...

        String[] months = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        String expression = regexp(
                exactLineMatch( // Enclosed in ^...$
                        integerRange(2012, Year.now().getValue()), // Year
                        literal("-"), // Delimiter
                        captureGroup(either(months)), // Month abbreviations - group 1
                        literal("-"), // Delimiter
                        captureGroup(leadingZero(integerRange(1, 31))) // Day - group 2
                )
        );

        RegexSet regexSet = simpleSet("/", ".").withUnicodeClass(UnicodeScript.ARABIC, false);
        System.out.println(regexSet.toRegex().toString());

        Pattern pattern = RegexSynth.compile(expression, RegexSynth.Flags.MULTILINE);

        System.out.println(pattern.pattern());

        assertEquals(pattern.pattern(), "^(?:2020|201[2-9])\\-((?:A(?:pr|ug)|Dec|Feb|" +
                "J(?:an|u[ln])|Ma[ry]|Nov|Oct|Sep))\\-((?:0?(?:3[0-1]|[1-2][0-9]|[1-9])))$");

    }

    @Test
    public void itShouldCreateCorrectURLExpression() {

        // Protocol matching expression
        Expression protocol = namedCaptureGroup("protocol",
                either("http", "https", "ftp")
        );

        // Sub-domain matching expression. i.e. www.google, dev-console.firebase
        Expression sub_domain = namedCaptureGroup("subDomain",
                oneOrMoreTimes(
                        alphanumeric().union(simpleSet("-", "."))
                )
        );

        // TLD matching expression
        Expression tld = namedCaptureGroup("tld",
                between(2, 4, alphabetic())
        );

        // port matching expression (optional ?)
        Expression port = optional(
                namedCaptureGroup("port",
                        literal(":"), oneOrMoreTimes(digit())
                )
        );

        // Resource matching expression
        Expression resource = namedCaptureGroup("resource",
                zeroOrMoreTimes(anything())
        );

        // Combine all isolated partial expressions
        String expression = regexp(
                exactLineMatch(
                        protocol, literal("://"), sub_domain, literal("."), tld,
                        port, optional(literal("/")), resource
                )
        );

        Pattern pattern = RegexSynth.compile(expression, RegexSynth.Flags.MULTILINE);
        assertEquals(pattern.pattern(), "^(?P<protocol>(?:ftp|https?)):\\/\\/(?P<subDomain>[\\-.0-9A-Za-z]+)" +
                "\\.(?P<tld>[A-Za-z]{2,4})(?P<port>:[0-9]+)?\\/?(?P<resource>.*)$");

    }

}
