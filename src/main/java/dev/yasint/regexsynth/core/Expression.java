package dev.yasint.regexsynth.core;

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

    StringBuilder toRegex();

    default Expression debug(final Consumer<StringBuilder> callback) {
        Objects.requireNonNull(callback).accept(toRegex());
        return this;
    }

}
