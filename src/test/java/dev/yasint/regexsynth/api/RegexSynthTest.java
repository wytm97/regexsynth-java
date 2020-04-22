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
import static dev.yasint.regexsynth.dsl.Quantifiers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        Pattern expression = new RegexSynth(
                exactLineMatch( // Enclosed in ^...$
                        integerRange(2012, Year.now().getValue()), // Year
                        literal("-"), // Delimiter
                        captureGroup(either(months)), // Month abbreviations - group 1
                        literal("-"), // Delimiter
                        captureGroup(leadingZero(integerRange(1, 31))) // Day - group 2
                )
        ).compile().getPattern();

        assertEquals(expression.pattern(), "^(?:2020|201[2-9])\\-((?:A(?:pr|ug)|Dec|Feb|" +
                "J(?:an|u[ln])|Ma[ry]|Nov|Oct|Sep))\\-((?:0?(?:3[01]|[12][0-9]|[1-9])))$");

    }

    @Test
    public void complexExpressionExampleTest() {

        // Matches dates in range 2010-1-1 to 2020-12-31,
        // has a space delimiter, and matches any string in the set
        // {SO, SSE, PE, PA, SS}, has a space delimiter, matches a number 
        // in range 58499 to 68599, has a space delimiter, one or more digit


        final String expr = "((?:2020|201[0-9])\\-(?:0?(?:1[0-2]|[1-9]))" +
                "\\-(?:0?(?:3[0-1]|[1-2][0-9]|[1-9]))) " +
                "((?:P[AE]|S(?:SE?|O))) ((?:68[0-5][0-9]{2}" +
                "|6[0-7][0-9]{3}|59[0-9]{3}|58[5-9][0-9]{2}|58499)) ([0-9]+)";

        final Expression DATE = captureGroup(
                integerRange(2010, 2020), literal("-"),
                leadingZero(integerRange(1, 12)), literal("-"),
                leadingZero(integerRange(1, 31))
        );

        final Expression DEPARTMENT_CODE = captureGroup(either("SO", "SS", "PE", "PA", "SSE"));
        final Expression ITEM_CODE = captureGroup(integerRange(58499, 68599));
        final Expression ITEM_STOCK_COUNT = captureGroup(oneOrMoreTimes(digit()));
        final Expression DELIMITER = space();

        final Pattern expression = new RegexSynth(
                DATE, DELIMITER, DEPARTMENT_CODE, DELIMITER,
                ITEM_CODE, DELIMITER, ITEM_STOCK_COUNT
        ).compile().getPattern();

    }

    @Test
    public void simpleExpressionExampleTest() {

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
        Pattern expression = new RegexSynth(
                exactLineMatch(
                        protocol, literal("://"), sub_domain, literal("."), tld,
                        port, optional(literal("/")), resource
                )
        ).compile().getPattern();

        assertEquals(expression.pattern(), "^(?P<protocol>(?:ftp|https?)):\\/\\/(?P<subDomain>(?:[\\-.0-9A-Za-z])+)" +
                "\\.(?P<tld>(?:[A-Za-z]){2,4})(?:(?P<port>:(?:[0-9])+))?(?:\\/)?(?P<resource>(?:.)*)$");

    }

    @Test
    public void itShouldCreateADoubleNumberMatchingExpression() {

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

    @Test
    public void itShouldCreateCorrectCommentExpression() {

        /*
         * Let's say we want to match comments like this and
         * Java-Doc (/**) comments. It's very easy and readable.
         */

        final Expression javaFuncSignature = captureGroup(
                // Match access specifiers including default
                optional(concat(either("public", "private", "protected"), space())),
                // Match synchronized methods if exist
                optional(concat(literal("synchronized"), space())),
                // Match static methods if exists
                optional(concat(literal("static"), space())),
                // Match strictfp methods if exists
                optional(concat(literal("strictfp"), space())),
                // Match the return type
                concat(either(literal("void"), oneOrMoreTimes(word())), space()),
                // Match the function name
                oneOrMoreTimes(word()),
                // Match the accepting parameters
                literal("("), zeroOrMoreTimes(word().union(space())), literal(")"),
                // After parameters a space following { and a \n
                space(), literal("{"), linebreak(),
                // Match the function body
                zeroOrMoreTimes(negated(simpleSet("}"))),
                // Match the closing brace
                literal("}")
        );

        final Pattern pattern = new RegexSynth(
                startOfLine(),
                either("/*", "/**"),
                zeroOrMoreTimes(anything()), // Anything in between
                literal("*/"),
                word()
        ).compile(RegexSynth.Flags.DOTALL).getPattern();

    }

}
