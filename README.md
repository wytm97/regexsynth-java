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

RegexSynth has a simple yet an elegant architecture where all the [regular expression constructs](https://github.com/google/re2/wiki/Syntax) share the same functional interface `Expression`. It allows us to declare and combine any type of expression and wrap expressions on top of expressions. This produces an explicit function driven [Abstract Syntax Tree](https://en.wikipedia.org/wiki/Abstract_syntax_tree) (AST) for the regex, which is then transpiled to a RE2 interpretable regular expression.

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

It is possible to do [contextual analysis](https://en.wikipedia.org/wiki/Semantic_analysis_(compilers)) directly inside defined [lambda functions](https://en.wikipedia.org/wiki/Anonymous_function) to validate the structure of the created regular expression. For example, we can use object decomposition like `interface BoundaryMatcher extends Expression { }` to check syntactical errors similar to `\b\b\b` (less likely or invalid syntax) but, it is beyond this project's current scope and research (Maybe in a future release we will do this) and, it is a more object oriented approach.

# **Reusing Expressions**

RegexSynth allows you to define custom expressions to create reusable expressions. For example, `RegexSet ` and `IntegerRange` are reusable components inside regexsynth. Likewise you can define your own expressions and reuse them accross multiple patterns. This way we can write more `clean` and `maintainable` regular expression codes.

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

// TODO

# **Documentation**

You can find the library documentation on this github's [Wiki](https://github.com/wytm97/regexsynth-java/wiki/Documentation).

# **Other Implmentations**

// TODO



















