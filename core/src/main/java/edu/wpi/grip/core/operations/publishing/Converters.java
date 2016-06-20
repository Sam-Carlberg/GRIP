package edu.wpi.grip.core.operations.publishing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.python.google.common.collect.ImmutableMap;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Handles converters for converting data to a publishable form.
 */
public final class Converters {

    // Store converters in insertion order.
    private static final Map<Pair<Class<?>, String>, Converter<?>> converters = new LinkedHashMap<>();

    /**
     * Converter that uses accessor methods to get the data to publish.
     * Methods named with the {@code getX} JavaBeans pattern will be converted to the form
     * {@code x = y}, e.g.
     * <pre><code>
     *     public Foo getFoo() {
     *         return myFoo;
     *     }
     * </code></pre>
     * will be converted to {@code foo = [value of myFoo]}. Note that this will fail if {@code Foo}
     * is not a publishable type (i.e. String, primitive, boxed primitive, or an array of any of
     * those types) and does not have an associated converter.
     */
    public static final Converter<Object> reflectiveByMethod = obj -> {
        ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
        for (Method m : obj.getClass().getDeclaredMethods()) {
            int mod = m.getModifiers();
            if (!Modifier.isPublic(mod) || Modifier.isStatic(mod) || Modifier.isAbstract(mod)) {
                // Not a public concrete instance method
                continue;
            }
            if (m.getParameterCount() != 0) {
                // Not an accessor method
                continue;
            }
            String name = m.getName();
            if (name.matches("^(get)[A-Z].*$")) { // It's a 'getX' accessor method
                name = name.replaceFirst("^(get)", "");
                name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
            }
            try {
                Object value = m.invoke(obj);
                if (needsConversion(value) && needsConversion(value.getClass())) {
                    builder.put(name, convert(value, name, false));
                } else {
                    builder.put(name, value);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new NotConvertibleException("Cannot get output of " + m.getName(), e);
            }
        }
        return builder.build();
    };

    static {
        // Add defaults for collections and maps
        setDefaultConverter(Collection.class, c -> {
            ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
            int i = 0;
            for (Object o : c) {
                if (!isConvertible(o)) {
                    throw new NotConvertibleException("Not convertible: " + o);
                }
                builder.put("Value #".concat(String.valueOf(i++)), needsConversion(o) ? convert(o) : o);
            }
            return builder.build();
        });
        setDefaultConverter(Map.class, m -> {
            ImmutableMap.Builder<String, Object> builder = ImmutableMap.builder();
            m.forEach((k, v) -> {
                if (!(k instanceof String)) {
                    throw new NotConvertibleException(
                        "Map key is not a String: " + k + " (" + k.getClass().getName() + ")");
                }
                if (!isConvertible(v)) {
                    throw new NotConvertibleException("Not convertible: " + v);
                }
                builder.put((String) k, needsConversion(v) ? convert(v) : v);
            });
            return builder.build();
        });
    }

    /**
     * Sets the converter for data with the given type and name. This will overwrite any previous
     * call with the same data type and name.
     *
     * @param dataType  the type of the data
     * @param name      the name of the data. Only data by this name will be converted with the given converter.
     * @param converter the converter to use to convert data by the given type and name
     * @param <T>       the type of the data to convert
     */
    public static <T> void setNamedConverter(Class<? extends T> dataType, String name, Converter<? super T> converter) {
        checkNotNull(dataType);
        checkNotNull(name);
        checkNotNull(converter);
        converters.put(Pair.of(dataType, name), converter);
    }

    /**
     * Sets the converter for data with the given type. If {@link #convert(Object, String) convert} is called and
     * encounters a nested convertible type that has a name that wasn't added with {@link #setNamedConverter(Class, String, Converter)},
     * it will use the converter supplied by this method. If a converter wasn't supplied with either of these methods,
     * a {@link NotConvertibleException} will be thrown.
     *
     * @param dataType  the type of the data to convert
     * @param converter the default converter to use for the given data type
     * @param <T>       the type of the data to convert
     */
    public static <T> void setDefaultConverter(Class<? extends T> dataType, Converter<? super T> converter) {
        checkNotNull(dataType);
        checkNotNull(converter);
        converters.put(Pair.of(dataType, null), converter);
    }

    @SuppressWarnings("unchecked")
    private static <T> Converter<? super T> getNamedConverter(Class<? extends T> type, String name) {
        Converter<? super T> converter = (Converter<? super T>) converters.get(Pair.of(type, name));
        if (converter == null) {
            // No converter for that exact type and name combination, try a default one (if set)
            if (hasDefaultConverter(type)) {
                converter = getDefaultConverter(type);
            } else {
                throw new NotConvertibleException(String.format("No converter found for %s by name %s", type, name));
            }
        }
        return converter;
    }

    @SuppressWarnings("unchecked")
    private static <T> Converter<? super T> getDefaultConverter(Class<? extends T> type) {
        Converter<? super T> converter = (Converter<? super T>) converters.get(Pair.of(type, null));
        if (converter == null) {
            // Use a converter for a superclass or superinterface
            Pair<Class<?>, String> typePair = converters.keySet().stream()
                .filter(p -> p.getLeft().isAssignableFrom(type))
                .findFirst()
                .orElseThrow(() -> new NotConvertibleException("No converter found for " + type));
            converter = (Converter<? super T>) converters.get(typePair);
        }
        return converter;
    }

    private static boolean hasNamedConverter(Class<?> type, String name) {
        boolean hasExplicit = converters.containsKey(Pair.of(type, name));
        if (hasExplicit) {
            // There's a converter for that exact type and name
            return true;
        }
        // Try to find a converter for a superclass or superinterface of $type
        return converters.keySet().stream()
            .filter(p -> name == null || name.equals(p.getRight()))
            .map(Pair::getLeft)
            .anyMatch(c -> c.isAssignableFrom(type));
    }

    private static boolean hasDefaultConverter(Class<?> type) {
        return hasNamedConverter(type, null);
    }

    /**
     * Converts the given data to a {@code Map} that may be published.
     *
     * @param data the data to convert
     * @param name the name of the data
     * @return a {@code Map} of value names to values as defined by the converters for the given data type
     * as well as for all nested data types encountered while converting
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> convert(Object data, String name) {
        checkNotNull(data);
        checkNotNull(name);
        return convert(data, name, true);
    }

    /**
     * Converts the given data to a {@code Map} that may be published. A default converter must be added for the
     * data's type before calling this.
     *
     * @param data the data to convert
     * @return a {@code Map} of value names to values as defined by the converters for the given data type
     * as well as for all nested data types encountered while converting
     */
    public static Map<String, Object> convert(Object data) {
        checkNotNull(data);
        return convert(data, null, true);
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<String, Object> convert(T data, String name, boolean first) {
        if (!needsConversion(data.getClass())) {
            throw new NotConvertibleException(String.format("%s (%s) should not be converted", data, data.getClass()));
        }
        if ((!isConvertible(data, name) && !isConvertible(data)) && (!first && needsConversion(data.getClass()))) {
            throw new NotConvertibleException(String.format("%s by name '%s' is not convertible", data, name));
        }
        Map<String, ?> converted = getNamedConverter((Class<T>) data.getClass(), name).convert(data);
        Map<String, Object> out = new LinkedHashMap<>(converted); // make a copy; converted map may be immutable
        out.entrySet().stream()
            .filter(e -> needsConversion(e.getValue().getClass())) // don't try converting things that don't need it
            .forEach(e -> e.setValue(convert(e.getValue(), e.getKey(), false)));
        return out;
    }

    private static boolean needsConversion(Class<?> dataType) {
        if (String.class.equals(dataType) ||
            Number.class.isAssignableFrom(dataType) ||
            Boolean.class.equals(dataType) ||
            dataType.isPrimitive()) {
            // Strings and (boxed) primitives don't need to be converted
            return false;
        }
        if (dataType.isArray() && !needsConversion(dataType.getComponentType())) {
            // Arrays of strings and (boxed) primitives don't need to be converted
            return false;
        }
        // Needs to be converted
        return true;
    }

    private static boolean needsConversion(Object data) {
        if (data instanceof Map) {
            // Can only convert Map<String, SomeConvertibleType>
            Map<?, ?> map = (Map<?, ?>) data;
            if (map.isEmpty()) {
                return true;
            }
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (!(e.getKey() instanceof String)) {
                    throw new NotConvertibleException("Map entry has a non-String key " + e.getKey());
                }
                if (!isConvertible(e.getValue(), (String) e.getKey())) {
                    throw new NotConvertibleException("Map entry has a non-convertible value: " + e.getValue());
                }
            }
            return true;
        }
        if (data instanceof Collection) {
            // Can only convert Collection<SomeConvertibleType>
            Collection<?> collection = (Collection<?>) data;
            if (collection.isEmpty()) {
                return true;
            }
            for (Object o : collection) {
                if (!isConvertible(o)) {
                    throw new NotConvertibleException("Collection has a non-convertible element: " + o);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Checks if the given data type can be converted
     *
     * @param dataType the type of data to check
     * @return true if the given data type is explicitly convertible, or if a supertype of it is
     * convertible
     */
    public static boolean isConvertible(Class<?> dataType) {
        checkNotNull(dataType);
        return converters.entrySet().stream()
            .map(e -> e.getKey().getLeft())
            .anyMatch(dataType::equals) ||
            converters.entrySet().stream()
                .map(e -> e.getKey().getLeft())
                .anyMatch(c -> c.isAssignableFrom(dataType));
    }

    private static boolean isConvertible(Object data) {
        if (data == null) {
            return false;
        }
        Class<?> dataType = data.getClass();
        if (!needsConversion(dataType)) {
            return true;
        }
        if (hasDefaultConverter(dataType)) {
            return true;
        }
        return false;
    }

    private static boolean isConvertible(Object data, String name) {
        if (data == null) {
            return false;
        }
        if (hasNamedConverter(data.getClass(), name)) {
            return true;
        }
        return isConvertible(data);
    }

}
