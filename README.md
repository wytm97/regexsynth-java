# **What is RegexSynth?**

RegexSynth is a minimal framework that aims to ***construct***, ***synthesize***, and improve ***comprehension*** of complex regular expressions. Also, one of the main goal of RegexSynth is to ***generalize*** the common features of regular expressions across programming languages. This is achieved using the [Google's RE2](https://github.com/google/re2) regular expressions engine; where we can rely on a robust set of features. This specific implementation uses [RE2J](https://github.com/google/re2j) Google's official port for Java.

However, this compromises some features we already have in programming languages. Mainly two features such as, `Lookaround Assertions` & `Back-References`. These are great features but are inherently unreliable [[1]](https://dl.acm.org/doi/10.1145/2071368.2071372) [[2]](https://dl.acm.org/doi/10.5555/2022896.2022911). RegexSynth helps you to write more precise, safe, and maintainable regular expressions in your projects. Whether you are a *veteran* or a *beginner* regex user, you will find it valuable in your software projects.



# **Installation**

If you're using `Maven`, you can use the following snippet in your `pom.xml` to get RegexSynth: 



# **The Problem?**

Let's say you have been assgined to a new project and been asked to figure out why the following regex isn't working as expected. At first glance you might not see it. After debugging this expression using a tool like [Regex101](https://regex101.com/) you will find out what's wrong with it.

###### Example #1

```reStructuredText
^(?:2020|201[2-9])\-((?:(?:A(?:pr|ug)|Dec|Feb|J(?:a|u[ln])|Ma[ry]|Nov|Oct|Sep)))\-((?:0?(?:3[0-1]|[1-2][0-9]|[2-9])))$
```



# **Declarative Syntax**

RegexSynth provides a declarative syntax for regular expressions creation. You specify what you want and it builds the target regular expression with the correct syntax. This is not new, because there are many *libraries* and *tools* out there that can build regular expressions without actually specifying the syntax. For example, [VerbalExpressions](https://github.com/VerbalExpressions), [JS-Regex](https://github.com/wyantb/js-regex), [XRegExp](http://xregexp.com/) etc. 

However, one major problem in these libraries is that they fail to structure the expression as it intended. The structure of a regular expression matters alot. If not handled correclty it can be a added complexity in your code. This is where RegexSynth comes into play. You can **destructure** your expression into partial expressions and then combine them to create a complete regular expression.

###### Example #1 (Continued)

```java
// Let's say we want to match dates like this format: 2019-Mar-15
//
// Requirement 1: Months as Abbreviation (Jan, Feb, Mar, etc...)
// Requirement 2: Match only between 2012-Jan-01 upto current year
// Requirement 3: Extract month and day
//
// Note: dates can have a leading zero 01, 02, 03, etc...

final String[] months = new String[]{
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
};

final Pattern pattern = new RegexSynth(
        exactLineMatch( // Enclosed in ^...$
                integerRange(2012, Year.now().getValue()), // Year
                literal("-"), // Delimiter
                captureGroup(either(months)), // Month abbreviations - group 1
                literal("-"), // Delimiter
                captureGroup(leadingZero(integerRange(1, 31))) // Day - group 2
        )
).compile(RegexSynth.Flags.MULTILINE);
```

Above code snippet will generate the desired regular expression along with some optimizations. RegexSynth can synthesize regex *integer ranges*, *minimize strings* (detect common prefixes) and *set expressions*. So, by using RegexSynth you can create more **readable** and **understandable** regular expressions easily.



# **What's Supported?**

### Boundary Matchers (Anchors)

|       Description       | Regex Construct |       RegexSynth Function       |
| :---------------------: | :-------------: | :-----------------------------: |
| The beginning of a line |       `^`       |         `startOfLine()`         |
|    The end of a line    |       `$`       |          `endOfLine()`          |
|  At beginning of text   |      `\A`       |         `startOfText()`         |
|     At end of text      |      `\z`       |          `endOfText()`          |
|     A word boundary     |      `\b`       |        `wordBoundary()`         |
|   A non-word boundary   |      `\B`       |       `nonWordBoundary()`       |
|       Line match        |     `^...$`     | `exactLineMatch(Expression...)` |
|   Exact word boundary   |    `\b...\b`    | `exactWordBoundary(Expression)` |



### Character Classes

|                         Description                          | Regex Construct |                     RegexSynth Function                      |
| :----------------------------------------------------------: | :-------------: | :----------------------------------------------------------: |
|                `a`, `d` or `f` (simple class)                |     `[adf]`     |                  `simpleSet("a", "d", "f")`                  |
|            `a`, `d` or `f` (negated simple class)            |    `[^adf]`     |             `negated(simpleSet("a", "d", "f"))`              |
| `a` through `z` or `A` through `Z`, inclusive (ranged class) |   `[A-Za-z]`    | `rangedSet("a", "z").union(rangedSet("A", "Z"))` or `CharClasses.Posix.alphabetic()` |
| `a` through `d` or `m` through `p`, inclusive (ranged class) |   `[a-dm-p]`    |       `rangedSet("a", "d").union(rangedSet("m", "p"))`       |
|       `a` to `z` with `d`, `e`, or `f` (intersection)        |     `[d-f]`     | `rangedSet("a", "z").intersection(simpleSet("d", "e", "f"))` |
|        `a` to `z`, except for `b` to `c` (difference)        |    `[ad-z]`     |    `rangedSet("a", "z").difference(rangedSet("b", "c"))`     |



### POSIX Character Classes

|             Description             |        Regex Construct        | RegexSynth Function |
| :---------------------------------: | :---------------------------: | :-----------------: |
|   A lowecase alphabetic character   |            `[a-z]`            |    `lowercase()`    |
|  An uppercase alphabetic character  |            `[A-Z]`            |    `uppercase()`    |
|       An alphabetic character       |          `[A-Za-z]`           |   `alphabetic()`    |
|           A decimal digit           |            `[0-9]`            |      `digit()`      |
|      An alphanumeric character      |         `[0-9A-Za-z]`         |  `alphanumeric()`   |
|       A punctuation character       |     ``[!-\\/:-@[-`{-~]``      |   `punctuation()`   |
|         A visible character         | ``[!-\\/:-@[-`{-~0-9A-Za-z]`` |    `graphical()`    |
|        A printable character        |          `[!-~\x20]`          |    `printable()`    |
| A space or a tab (blank characters) |            `[ \t]`            |      `blank()`      |
|         A hexadecimal digit         |         `[0-9a-fA-F]`         |    `hexDigit()`     |
|       A whitespace character        |       `[ \t\n\x0B\f\r]`       |   `whiteSpace()`    |



### Group Constructs

Capture-groups (enclosed in `(...)` or `(?<name>...)`) allows you to extract results of a successful match. And Non-capture-groups (enclosed in `(?:...)`) allows you to group but not including them in the results.

###### Capture Group `(...)`



