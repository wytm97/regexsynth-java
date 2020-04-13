# **What is RegexSynth?**

RegexSynth is a minimal library that aims to ***construct***, ***synthesize***, and improve ***comprehension*** of complex regular expressions. Also, one of the main goal of RegexSynth is to ***generalize*** some common set features of regular expressions across programming languages. This is achieved using the [Google's RE2](https://github.com/google/re2) regular expressions engine; where we can rely on a robust set of features. This specific implementation uses [RE2J](https://github.com/google/re2j) Google's official port for Java.

However, this compromises some features we already have in programming languages. Mainly two features such as, `Lookaround Assertions` & `Back-References`. These are great features but are inherently unreliable [[1]](https://dl.acm.org/doi/10.1145/3338906.3342509) [[2]](https://dl.acm.org/doi/10.1145/2071368.2071372) [[3]](https://dl.acm.org/doi/10.5555/2022896.2022911) [[4]](https://arxiv.org/pdf/1405.5599.pdf). RegexSynth helps you to write more precise, safe, and maintainable regular expressions in your projects. Whether you are a *veteran* or a *beginner* regex user, you will find it valuable in your software projects.

# **Installation**

If you're using `Maven`, you can use the following snippet in your `pom.xml` file to get RegexSynth:

```xml
<dependency>
  <groupId>dev.yasint</groupId>
  <artifactId>regexsynth</artifactId>
  <version>1.0.2</version>
</dependency>
```

If you're using `Gradle`, you can use the following snippet in your `build.gradle` file to get RegexSynth:

```groovy
implementation 'dev.yasint:regexsynth:1.0.2' // Groovy DSL
```

```kotlin
implementation("dev.yasint:regexsynth:1.0.2") // Kotlin DSL
```

You can use the same artifact details in any build system compatible with the Maven Central repositories (e.g. sbt, Ivy, leiningen, bazel, purl, badge, buildr, grape). Visit the [repo on Maven Central](https://search.maven.org/artifact/dev.yasint/regexsynth) .

# **The Problem?**

Let's say you have been assgined to a new project and been asked to figure out why the following regex isn't working as expected. At first glance you might not see it. After debugging this expression using a tool like [Regex101](https://regex101.com/) you will find out what's wrong with it.

```reStructuredText
^(?:2020|201[2-9])\-((?:(?:A(?:pr|ug)|Dec|Feb|J(?:a|u[ln])|Ma[ry]|Nov|Oct|Sep)))\-((?:0?(?:3[0-1]|[1-2][0-9]|[2-9])))$
```

# **Declarative Syntax**

RegexSynth provides a declarative syntax for regular expressions creation. You specify what you want and it builds the target regular expression with the correct syntax. This is not new, because there are many regex builder *libraries* and *tools* out there that can construct regular expressions without explicitly specifying the syntax. For example, [VerbalExpressions](https://github.com/VerbalExpressions), [JS-Regex](https://github.com/wyantb/js-regex), [SimpleRegex](https://github.com/SimpleRegex) etc.

However, one of the major problem of these libraries is that they fail to structure the expression as it intended. The structure of a regular expression matters alot. If not handled correclty it can be a added complexity in your code. This is where RegexSynth comes into play. RegexSynth allows you to **destructure** your expression into partial expressions and then combine them to create a complete regular expression.

###### Example #1 (Compact Expression)

```java
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

```

```reStructuredText
^(?:2020|201[2-9])\-((?:A(?:pr|ug)|Dec|Feb|J(?:an|u[ln])|Ma[ry]|Nov|Oct|Sep))\-((?:0?(?:3[0-1]|[1-2][0-9]|[1-9])))$
```



###### Example #2 (Partial Expression Segregation)

```java

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
        between(2, 4, alphabetic()) // 2-4 chars
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

// Combine all segregated partial expressions
String expression = regexp(
        exactLineMatch(
                protocol, literal("://"), sub_domain, literal("."), tld,
                port, optional(literal("/")), resource
        )
);

// Compile the expression. Will return a com.google.re2j.Pattern instance.
Pattern pattern = RegexSynth.compile(expression, RegexSynth.Flags.MULTILINE);
```

```reStructuredText
^(?P<protocol>(?:ftp|https?)):\/\/(?P<subDomain>[\-.0-9A-Za-z]+)\.(?P<tld>[A-Za-z]{2,4})(?P<port>:[0-9]+)?\/?(?P<resource>.*)$
```

# **Architecture**

RegexSynth has a simple yet an elegant architecture where all the [regular expression constructs](https://github.com/google/re2/wiki/Syntax) share the same functional interface `Expression`. It allows us to declare and combine any type of expression and wrap expressions on top of expressions. This way it produces an explicit function driven [Abstract Syntax Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree) (AST) for the regex, which is then transpiled to a valid regular expression.

```java
@FunctionalInterface
public interface Expression {

    StringBuilder toRegex();
    
    default Expression debug(final Consumer<StringBuilder> callback) {
        Objects.requireNonNull(callback).accept(toRegex());
        return this;
    }
  
}
```

It is possible to do [contextual analysis](https://en.wikipedia.org/wiki/Semantic_analysis_(compilers)) directly inside defined [lambda functions](https://en.wikipedia.org/wiki/Anonymous_function) to validate the structure of the created regular expression. For example, we can use object decomposition like `interface BoundaryMatcher extends Expression { }` to *typecheck* or *errorcheck* syntax similar to `\b\b\b` (less likely or invalid syntax) but, it is beyond this project's current scope and research (Maybe in a future release we will do this) and, it is a more object oriented approach.

# **Reusing Expressions**

RegexSynth allows you to extend the functionality to create reusable expressions. For example, `RegexSet`and `IntegerRange`are reusable components inside regexsynth. Likewise you can define your own expressions and re-use them accross multiple patterns. This way we can write more `clean` and `maintainable` regular expression codes.

```java
import java.time.Year;
import dev.yasint.regexsynth.core.Expression;
// import other nececessary lambda function literals

public class ISODateFormat implements Expression {
  
    private int startYear;
    private int endYear;
  
    public ISODateFormat(int startYear, int endYear) {
        this.startYear = startYear;
        this.endYear = endYear;
    }
  
    @Override
    public StringBuilder toRegex() {
        final Expression expression = nonCaptureGroup(
                integerRange(startYear, endYear),
                literal("-"),
                captureGroup(leadingZero(integerRange(1, 12))),
                literal("-"),
                captureGroup(leadingZero(integerRange(1, 31)))
        );
        return expression.toRegex();
    }
  
}

// Reuse the regex in multiple expressions.
public static void main(String[] args) {
    final String expression = regexp(
            new ISODateFormat(2010, Year.now().getValue()),
            literal(":"),
            ...
    );
}
```

# **Future Work**

Trie to Acyclic tree 

# **Documentation**

You can find the library documentation on this Wiki.



# **Other Implmentations**





















# Documentation

## Boundary Matchers (Anchors)

|       Description       | Regex Construct |        RegexSynth Function         |
| :---------------------: | :-------------: | :--------------------------------: |
| The beginning of a line |       `^`       |          `startOfLine()`           |
|    The end of a line    |       `$`       |        `endOfLine(Boolean)`        |
|  At beginning of text   |      `\A`       |          `startOfText()`           |
|     At end of text      |      `\z`       |           `endOfText()`            |
|     A word boundary     |      `\b`       |          `wordBoundary()`          |
|   A non-word boundary   |      `\B`       |        `nonWordBoundary()`         |
|       Line match        |     `^...$`     |  `exactLineMatch(Expression...)`   |
|   Exact word boundary   |    `\b...\b`    | `exactWordBoundary(Expression...)` |



## Character Classes

#### Regular Expression Sets `[...]`

|                         Description                          | Regex Construct |                     RegexSynth Function                      |
| :----------------------------------------------------------: | :-------------: | :----------------------------------------------------------: |
|                `a`, `d` or `f` (simple class)                |     `[adf]`     |                  `simpleSet("a", "d", "f")`                  |
|            `a`, `d` or `f` (negated simple class)            |    `[^adf]`     |             `negated(simpleSet("a", "d", "f"))`              |
| `a` through `z` or `A` through `Z`, inclusive (ranged class) |   `[A-Za-z]`    |       `rangedSet("a", "z").union(rangedSet("A", "Z"))`       |
| `a` through `d` or `m` through `p`, inclusive (ranged class) |   `[a-dm-p]`    |       `rangedSet("a", "d").union(rangedSet("m", "p"))`       |
|       `a` to `z` with `d`, `e`, or `f` (intersection)        |     `[d-f]`     | `rangedSet("a", "z").intersection(simpleSet("d", "e", "f"))` |
|        `a` to `z`, except for `b` to `c` (difference)        |    `[ad-z]`     |    `rangedSet("a", "z").difference(rangedSet("b", "c"))`     |

RegexSynth supports `union`, `intersection`, and `difference` on regular expression sets as shown above. You can use either one of those functions to create a set expression and it will return a instance of a `RegexSet` to do above set operations.

###### Creating Set Range Expressions: -

```java
rangedSet(String from, String to); // Create from string literals (bmp or astral)
rangedSet(int codepointA, int codepointB); // or from unicode codepoints
```

###### Creating Simple Set Expressions: -

```java
simpleSet(String... characters); // Create from string literals (bmp or astral) 
simpleSet(int... codepoints); // or from unicode codepoints
```

###### Negating Set Expressions: -

```java
negated(RegexSet) // Negates a given set expression
```

###### Unicode Scripts Inside Set Expressions: -

```java
// Include a unicode script class into a set expression by providing
// the script name and negated property. (false for non-negated)
// also, you can chain withUnicodeClass(..) method multiple times
simpleSet(".").withUnicodeClass(UnicodeScript.ARABIC, false); // [.\p{Arabic}]
```

#### Single Characters & Literals

|                         Description                          |        Regex Construct         |          RegexSynth Function           |
| :----------------------------------------------------------: | :----------------------------: | :------------------------------------: |
| Any character, possibly including newline (if `DOTALL` flag is on) |              `.`               |              `anything()`              |
|      Literal character (matches any charater literally)      |          `http:\/\/`           |           `literal(String)`            |
|              Quoted literals (strict literals)               |           `\Q...\E`            |        `quotedLiteral(String)`         |
|                    Unicode script blocks                     | `\p{Sinhala}` or `\P{Sinhala}` | `unicodeClass(UnicodeScript, Boolean)` |

#### Escape Sequences

By default RegexSynth creates a set expression for below listed sequences. This is because these sequences are valid constructs inside a regular expression set. If you want to include one of these sequences into a set expression simply do a `union` operation with the source set. However, if you only use this as a single element, the resulting expression won't create a set, instead it will simply append the regex construct.

|    Character    | Regex Construct | RegexSynth Function |
| :-------------: | :-------------: | :-----------------: |
|    Backslash    |      `\\`       |    `backslash()`    |
|  Double quotes  |      `\"`       |  `doubleQuotes()`   |
|  Single quote   |      `\'`       |   `singleQuote()`   |
|    Backtick     |     `` ` ``     |    `backtick()`     |
|      Bell       | `\007` or `\a`  |      `bell()`       |
|    Form feed    | `\014` or `\f`  |    `formfeed()`     |
| Horizontal tab  | `\001` or `\t`  |  `horizontalTab()`  |
|  Vertical tab   | `\013` or `\v`  |   `verticalTab()`   |
|    Linebreak    | `\012` or `\n`  |    `linebreak()`    |
| Carriage return | `\015` or `\r`  | `carriageReturn()`  |

#### POSIX Character Classes

RegexSynth provides all the standard POSIX charclasses to use in your expressions. But more importantly all of the listed classes uses `RegexSet` as the default implementation and when you combine below classes it can optimize the set expression.

|          Class Description          |        Regex Construct        | RegexSynth Function |
| :---------------------------------: | :---------------------------: | :-----------------: |
|   A lowecase alphabetic character   |            `[a-z]`            |    `lowercase()`    |
|  An uppercase alphabetic character  |            `[A-Z]`            |    `uppercase()`    |
|     ASCII charset `0` to `127`      |         `[\x00-\x7F]`         |      `ascii()`      |
| ASCII extended charset `0` to `255` |         `[\x00-\xFF]`         |     `ascii2()`      |
|       An alphabetic character       |          `[A-Za-z]`           |   `alphabetic()`    |
|         **A decimal digit**         |        `[0-9]` or `\d`        |      `digit()`      |
|       **Not a decimal digit**       |       `[^0-9]` or `\D`        |    `notDigit()`     |
|      An alphanumeric character      |         `[0-9A-Za-z]`         |  `alphanumeric()`   |
|       A punctuation character       |     ``[!-\\/:-@[-`{-~]``      |   `punctuation()`   |
|         A visible character         | ``[!-\\/:-@[-`{-~0-9A-Za-z]`` |    `graphical()`    |
|        A printable character        |          `[!-~\x20]`          |    `printable()`    |
| A space or a tab (blank characters) |            `[ \t]`            |      `blank()`      |
|         A hexadecimal digit         |         `[0-9a-fA-F]`         |    `hexDigit()`     |
|     **A whitespace character**      |   `[ \t\n\x0B\f\r]` or `\s`   |   `whiteSpace()`    |
|   **Not a whitespace character**    |  `[^ \t\n\x0B\f\r]` or `\S`   |  `notWhiteSpace()`  |
|        **A word character**         |    `[0-9A-Za-z_]` or `\w`     |      `word()`       |
|      **Not a word character**       |    `[^0-9A-Za-z_]` or `\W`    |     `notWord()`     |



## Composite Operators

RegexSynth has dedicated composite functions for regex `concatenation` and `alternation`. As a implementation detail string alternation has optimizations where it uses a [trie](https://en.wikipedia.org/wiki/Trie) to detect common prefixes. Alternation results are wrapped in a non capturing group avoid breaking the surrounding expression.

|        Operator Description        | Regex Construct |                     RegexSynth Function                      |
| :--------------------------------: | :-------------: | :----------------------------------------------------------: |
| Concatenation, `a` followed by `b` |      `ab`       | `concat(Expression a, Expression b)` or `concat(Expression...)` |
|      Alternation, `a` or `b`       |      `a|b`      | `either(Expression a, Expression b)` or `either(Expression...)`or `either(String...)` or `either(Set<String>)` |



## Quantifiers (Repetitions)

| Description | Regex Construct | RegexSynth Function |
| :---------: | :-------------: | :-----------------: |
|             |                 |                     |
|             |                 |                     |
|             |                 |                     |
|             |                 |                     |
|             |                 |                     |





#### Group Constructs

Capture-groups (enclosed in `(...)` or `(?<name>...)`) allows you to extract results of a successful match. And Non-capture-groups (enclosed in `(?:...)`) allows you to group but not including them in the results.

###### Capture Group `(...)`



