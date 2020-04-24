package dev.yasint.regexsynth.examples;

import com.google.re2j.Pattern;
import dev.yasint.regexsynth.api.Expression;
import dev.yasint.regexsynth.api.RegexSynth;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static dev.yasint.regexsynth.dsl.Anchors.exactLineMatch;
import static dev.yasint.regexsynth.dsl.Anchors.exactWordBoundary;
import static dev.yasint.regexsynth.dsl.CharClasses.EscapeSequences.space;
import static dev.yasint.regexsynth.dsl.CharClasses.Posix.*;
import static dev.yasint.regexsynth.dsl.CharClasses.*;
import static dev.yasint.regexsynth.dsl.Groups.captureGroup;
import static dev.yasint.regexsynth.dsl.Groups.namedCaptureGroup;
import static dev.yasint.regexsynth.dsl.Literals.literal;
import static dev.yasint.regexsynth.dsl.Numeric.integerRange;
import static dev.yasint.regexsynth.dsl.Numeric.leadingZero;
import static dev.yasint.regexsynth.dsl.Operators.either;
import static dev.yasint.regexsynth.dsl.Repetition.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class ExampleTest {

    // For research demo only

    public static Expression higherOrderFunc(final Expression anotherFunc) {
        // We can achieve strict immutability behaviour by doing this
        return () -> anotherFunc.toRegex().append("world!");
    }

    @Test
    public void powerOfExpressionInterface() {
        // Wrap the expression a higher-order function
        final Expression e = higherOrderFunc(() -> new StringBuilder("Hello "));
        assertEquals(e.toRegex().toString(), "Hello world!"); // yields true
    }

    @Test
    public void complexExpressionSegregationExample() {

        // Matches dates in range 2010-1-1 to 2020-12-31,
        // has a space delimiter, and matches any string in the set
        // {SO, SSE, PE, PA, SS}, has a space delimiter, matches a number
        // in range 58499 to 68599, has a space delimiter, one or more digit

        // Example of how to segregate complex regular expressions
        // into more simpler format. So it's easy to debug and read.

        final Expression DATE = captureGroup(
                integerRange(2010, 2020), literal("-"),
                leadingZero(integerRange(1, 12)), literal("-"),
                leadingZero(integerRange(1, 31))
        );
        final Expression DEPT_CODE = captureGroup(either("SO", "SS", "PE", "PA", "SSE"));
        final Expression ITEM_CODE = captureGroup(integerRange(58499, 68599));
        final Expression ITEM_S_COUNT = captureGroup(oneOrMoreTimes(digit()));
        final Expression DELIMITER = space();

        final Pattern pattern = new RegexSynth(
                DATE, DELIMITER, DEPT_CODE, DELIMITER,
                ITEM_CODE, DELIMITER, ITEM_S_COUNT
        ).compile().getPattern();

        assertEquals(pattern.pattern(), "((?:2020|201[0-9])\\-(?:0?(?:1[0-2]|[1-9]))\\-(?:0?(?:3[01]|[12]" +
                "[0-9]|[1-9]))) ((?:P[AE]|S(?:SE?|O))) ((?:68[0-5][0-9]{2}|6[0-7][0-9]{3}|59[0-9]{3}|58[5-9]" +
                "[0-9]{2}|58499)) ((?:[0-9])+)");

    }

    @Test
    public void simpleExpressionSegregationExample() {

        // department-code stock-count some-date

        final Expression DEPARTMENT_CODE = captureGroup(either("K", "KS", "KLE", "KLL"));
        final Expression ITEM_STOCK_COUNT = captureGroup(exactly(3, digit()));
        final Expression DATE = captureGroup(
                exactly(4, digit()), literal("-"),
                exactly(2, digit()), literal("-"),
                exactly(2, digit())
        );
        final Expression DELIMITER = literal("**");

        // final expression
        final Pattern expression = new RegexSynth(
                DEPARTMENT_CODE, DELIMITER,
                ITEM_STOCK_COUNT, DELIMITER,
                DATE
        ).compile().getPattern();

        // Matches any string in the set {K, S, KS, KLE, KLL}, followed by two asterisks
        // and 0 or 9 digit 3 times, followed by two asterisks, and matches date formats like 2020-11-31
        final String expr = "(K(?:(?:L[EL]|S))?)\\*\\*([0-9]{3})\\*\\*([0-9]{4}\\-[0-9]{2}\\-[0-9]{2})";

    }

    // Documentation examples for other developers

    @Test
    public void dateMatchingExample() {

        // Let's say we want to match dates like this format: 2019-Mar-15
        //
        // Requirement 1: Months as Abbreviation -> (Jan, Feb, Mar, etc...)
        // Requirement 2: Match only between 2012-Jan-01 upto current year
        // Requirement 3: Extract month and day
        //
        // Note: dates can have a leading zero 01, 02, 03, etc...

        final String[] months = new String[]{
                "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        };

        final Pattern expression = new RegexSynth(
                exactLineMatch( // Enclosed in ^...$
                        integerRange(2012, Year.now().getValue()), // Year
                        literal("-"), // Delimiter
                        captureGroup(either(months)), // Month abbreviations - group 1
                        literal("-"), // Delimiter
                        captureGroup(leadingZero(integerRange(1, 31))) // Day - group 2
                )
        ).compile(
                // You can pass optional modifiers here...
        ).getPattern();

        assertEquals(expression.pattern(), "^(?:2020|201[2-9])\\-((?:A(?:pr|ug)|Dec|Feb|" +
                "J(?:an|u[ln])|Ma[ry]|Nov|Oct|Sep))\\-((?:0?(?:3[01]|[12][0-9]|[1-9])))$");

    }

    @Test
    public void urlMatchingExample() {

        Expression protocol = namedCaptureGroup("protocol", either("http", "https", "ftp"));
        Expression sub_domain = namedCaptureGroup("subDomain", oneOrMoreTimes(union(alphanumeric(), simpleSet("-", "."))));
        Expression tld = namedCaptureGroup("tld", between(2, 4, alphabetic()));
        Expression port = optional(namedCaptureGroup("port", literal(":"), integerRange(1, 65535)));
        Expression resource = namedCaptureGroup("resource", zeroOrMoreTimes(anything()));

        // Combine all isolated partial expressions
        final Pattern expression = new RegexSynth(
                exactLineMatch(
                        protocol, literal("://"), sub_domain, literal("."), tld,
                        port, optional(literal("/")), resource
                )
        ).compile().getPattern();

        assertEquals(expression.pattern(), "^(?P<protocol>(?:ftp|https?)):\\/\\/(?P<subDomain>(?:[\\-.0-9A-Za-z])+)" +
                "\\.(?P<tld>(?:[A-Za-z]){2,4})(?:(?P<port>:(?:6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]" +
                "{3}|[1-5][0-9]{4}|[1-9][0-9]{3}|[1-9][0-9]{2}|[1-9][0-9]|[1-9])))?(?:\\/)?(?P<resource>(?:.)*)$");

    }

    @Test
    public void doubleNumberMatchingExample() {

        // let's say we need to match all the double numbers
        // from 0.000 to 1000.999 with three fraction digits

        Pattern pattern = new RegexSynth(
                exactWordBoundary(
                        integerRange(0, 1000),
                        literal("."),
                        between(1, 3, digit())
                )
        ).compile().getPattern();

        System.out.println(pattern.pattern());

    }

}
