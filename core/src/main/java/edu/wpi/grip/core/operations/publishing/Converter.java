package edu.wpi.grip.core.operations.publishing;

import java.util.Map;

import javax.annotation.Nonnull;

/**
 * Class representing a function that converts data to a publishable representation.
 *
 * @param <T> the type of the data
 */
@FunctionalInterface
public interface Converter<T> {

    /**
     * Converts data to a map of values to their names. This can use fields, methods, or computation to get the
     * values and their names.
     *
     * @param data the data to convert
     * @return a map of values to their names
     */
    @Nonnull Map<String, ?> convert(@Nonnull T data);

    /**
     * Overrides this converter with another. Any mapping that either this or the other will create
     * will use the mapping from the other converter.
     * <p>
     * For example, if this converter has the output of {@code {"a"=1, "b"=2}} and the other
     * converter has the output of {@code {"a"=0, "c"=2}}, the resulting map will be
     * {@code {"a"=0, "b"=2, "c"=2}}
     *
     * @param another the converter to override this one with
     * @return a composite converter
     */
    default @Nonnull Converter<T> override(@Nonnull Converter<? super T> another) {
        return data -> {
            Map<String, ?> out = this.convert(data);
            out.putAll((Map) another.convert(data));
            return out;
        };
    }

}
