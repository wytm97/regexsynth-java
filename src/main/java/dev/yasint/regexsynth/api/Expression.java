package dev.yasint.regexsynth.api;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * An abstract representation of a expression. This can be either
 * a complete expression or a 'partial' expression.
 *
 * The {@code toRegex} lambda function returns a string representation
 * of a regular expression. (It can be any of the regex constructs)
 */
@FunctionalInterface
public interface Expression {

    /**
     * Synthesizes a given specification into a regular expression.
     * The implementing class can control what should be in the
     * regex by simply wrapping it in a object or a functional template.
     *
     * Example 1:
     * regexConstruct(param1, param2) => Expression
     *
     * Example 2:
     * public class RegexConstruct implements Expression { ... }
     *
     * @return regex equivalent
     * @since 1.0.0
     */
    StringBuilder toRegex();

    /**
     * Debug a expression at chained point. It passes whatever
     * constructed upto this node in the abstract syntax tree.
     *
     * @param callback consumer callback function
     * @return the current expression
     */
    default Expression debug(final Consumer<StringBuilder> callback) {
        Objects.requireNonNull(callback).accept(toRegex());
        return this;
    }

}
