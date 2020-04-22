package dev.yasint.regexsynth.core;

public final class RegexConstructs {

    /* Special Characters */
    public static final Character BACKSLASH = '\\';
    public static final Character PAREN_OPEN = '(';
    public static final Character PAREN_CLOSE = ')';
    public static final Character LESS_THAN = '<';
    public static final Character GREATER_THAN = '>';
    public static final Character OPEN_CURLY_BRACE = '{';
    public static final Character CLOSE_CURLY_BRACE = '}';
    public static final Character OPEN_SQUARE_BRACKET = '[';
    public static final Character CLOSE_SQUARE_BRACKET = ']';
    public static final Character CARAT = '^';
    public static final Character DOLLAR = '$';
    public static final Character HYPHEN = '-';
    public static final Character PLUS = '+';
    public static final Character ASTERISK = '*'; // Kleene Star
    public static final Character QUESTION_MARK = '?';
    public static final Character COLON = ':';
    public static final Character COMMA = ',';
    public static final Character PERIOD = '.'; // Matches Any

    /* Anchor classes */
    public static final String WORD_BOUNDARY = BACKSLASH + "b";
    public static final String NON_WORD_BOUNDARY = BACKSLASH + "B";
    public static final String BEGINNING_OF_TEXT = BACKSLASH + "A";
    public static final String END_OF_TEXT = BACKSLASH + "z";

    /* Quotes */
    public static final String QUOTE_START = BACKSLASH + "Q";
    public static final String QUOTE_END = BACKSLASH + "E";

    /* Logical operators */
    public static final String ALTERNATION = "|";

    /* Groups */
    public static final String NAMED_CAPTURE_GROUP_PREFIX = "P";

}
