package edu.wpi.grip.core.operations.publishing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for methods in {@link Converters}
 */
public class ConvertersTest {

    private final Converters converters = new Converters();

    @SuppressWarnings("unused")
    private static final class TestJavaBean {
        private final String str;
        private final int i;
        private final double d;
        private final int[] intArray;

        private TestJavaBean(String str, int i, double d, int[] intArray) {
            this.str = str;
            this.i = i;
            this.d = d;
            this.intArray = intArray;
        }

        // Public instance accessors -- these SHOULD be called

        public String getStr() {
            return str;
        }

        public int getI() {
            return i;
        }

        public double getD() {
            return d;
        }

        public int[] getIntArray() {
            return intArray;
        }

        // These methods SHOULD NOT be called

        private Object privateMethod() {
            throw new AssertionError("Private method should not be called");
        }

        Object packagePrivateMethod() {
            throw new AssertionError("Package-private method should not be called");
        }

        protected Object protectedMethod() {
            throw new AssertionError("Protected method should not be called");
        }

        public static Object staticMethod() {
            throw new AssertionError("Static method should not be called");
        }

        public Object notAnAccessorMethod(Object arg) {
            throw new AssertionError("A non-accessor method should not be called");
        }

    }

    @Test(expected = NotConvertibleException.class)
    public void testBadReflective() {
        converters.setDefaultConverter(TestJavaBean.class, converters.reflectiveByMethod);
        TestJavaBean bad = new TestJavaBean(null, 0, 0, null); // null value should throw exception
        converters.convert(bad);
        fail("Call to convert() should have thrown an exception with null values");
    }

    @Test
    public void testValidReflective() {
        converters.setDefaultConverter(TestJavaBean.class, converters.reflectiveByMethod);
        TestJavaBean valid = new TestJavaBean("valid", 10, 20, new int[] {1, 2, 3});
        Map<String, Object> converted = converters.convert(valid);
        assertEquals("String value was not as expected", "valid", converted.get("str"));
        assertEquals("int value was not as expected", 10, converted.get("i"));
        assertEquals("double value was not as expected", 20.0, converted.get("d"));
        assertArrayEquals("int array value was not as expected",
            new int[] {1, 2, 3},
            (int[]) converted.get("intArray"));
    }

    @Test(expected = NotConvertibleException.class)
    public void testMapWithNonStringKeys() {
        Map<Object, Object> map = new HashMap<>();
        map.put(1234, 5678);
        converters.convert(map);
        fail("Call to convert() should have thrown an exception with non-String keys");
    }

    @Test(expected = NotConvertibleException.class)
    public void testMapWithNonConvertibleValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", new Object());
        converters.convert(map);
        fail("Call to convert() should have thrown an exception with non-convertible values");
    }

    @Test
    public void testValidMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        Map<String, Object> converted = converters.convert(map);
        assertEquals("Converted map was not as expected", "value", converted.get("key"));
    }

    @Test(expected = NotConvertibleException.class)
    public void testDoesNotNeedConversion() {
        String noConversionNeeded = "No conversion needed";
        converters.convert(noConversionNeeded);
        fail("String doesn't need conversion");
    }

    @Test
    public void testDefaults() {
        class ClassWithDefaultConverter {
            private final double x = 12.34;
            private final int y = 1234;
            private final String z = "z";
        }
        converters.setDefaultConverter(ClassWithDefaultConverter.class, o -> ImmutableMap.of("x", o.x, "y", o.y, "z", o.z));
        Map<String, Object> converted = converters.convert(new ClassWithDefaultConverter());
        assertEquals(12.34, converted.get("x"));
        assertEquals(1234, converted.get("y"));
        assertEquals("z", converted.get("z"));
    }

    @Test
    public void testNested() {
        class A {
            private final String value = "A";
        }
        class B {
            private final A nested = new A();
            private final String value = "B";
        }
        converters.setDefaultConverter(A.class, a -> ImmutableMap.of("value", a.value));
        converters.setNamedConverter(B.class, "B", b -> ImmutableMap.of("nested", b.nested, "value", b.value));
        Map<String, Object> converted = converters.convert(new B(), "B");
        System.out.println(converted);
        assertTrue("Nested convertible type was not converted", converted.get("nested") instanceof Map);
        assertEquals("A", ((Map) converted.get("nested")).get("value"));
        assertEquals(ImmutableMap.of("value", "A"), converted.get("nested"));
        assertEquals("B", converted.get("value"));
    }

    @Test(expected = NotConvertibleException.class)
    public void testNoConverter() {
        class NoConverter {}
        converters.convert(new NoConverter());
        fail("There shouldn't be a converter for " + NoConverter.class);
    }

    @Test(expected = NotConvertibleException.class)
    public void testNotConvertibleNestedMap() {
        class NotConvertible {}
        Map<String, Map<String, ?>> map = ImmutableMap.of("root", ImmutableMap.of("nested", new NotConvertible()));
        converters.convert(map);
        fail("Nested conversion should have failed");
    }

    @Test(expected = NotConvertibleException.class)
    public void testNotConvertibleNestedCollection() {
        class NotConvertible {}
        Map<String, Collection> map = new HashMap<>();
        map.put("key", Arrays.asList(new NotConvertible()));
        converters.convert(map);
    }

    @Test
    public void testConvertEmptyNestedCollection() {
        Map<String, ?> converted = converters.convert(ImmutableMap.of("key", new ArrayList<>()));
        assertTrue("Conversion of empty list was not empty", ((Map) converted.get("key")).isEmpty());
    }

    @Test
    public void testConvertibleNestedCollection() {
        Map<String, Object> converted = converters.convert(ImmutableMap.of("key", ImmutableList.of("value")));
        assertEquals("value", ((Map) converted.get("key")).get("Value #0"));
    }

    @Test
    public void testUse() {
        class Foo {
            private final String bar = "foo";
        }
        ConverterManager manager = converters -> converters.setDefaultConverter(Foo.class, foo -> ImmutableMap.of("bar", foo.bar));
        converters.use(manager);
        assertEquals("foo", converters.convert(new Foo()).get("bar"));
    }

    @Test
    public void testIsConvertible() {
        try {
            converters.isConvertible(null);
            fail("Should fail if given null");
        } catch (NullPointerException expected) {}
        abstract class ListSubClass implements List {}
        abstract class MapSubClass implements Map {}
        class MaybeConvertible {}
        assertTrue(converters.isConvertible(Collection.class)); // has explicit converter
        assertTrue(converters.isConvertible(ListSubClass.class)); // has implicit converter (Collection)
        assertTrue(converters.isConvertible(Map.class)); // has explicit converter
        assertTrue(converters.isConvertible(MapSubClass.class)); // has implicit converter (Map)
        assertFalse(converters.isConvertible(MaybeConvertible.class)); // no converter set
        converters.setDefaultConverter(MaybeConvertible.class, c -> new HashMap<>());
        assertTrue(converters.isConvertible(MaybeConvertible.class)); // converter has been set
    }

}
