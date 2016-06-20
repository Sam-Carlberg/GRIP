package edu.wpi.grip.core.operations.publishing;

import java.util.Map;

import org.junit.Test;
import org.python.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 */
public class ConverterTest {

    @Test
    public void testDefaults() {
        Converters.setDefaultConverter(ClassWithDefaultConverter.class, o -> ImmutableMap.of("x", o.x, "y", o.y, "z", o.z));
        Map<String, Object> converted = Converters.convert(new ClassWithDefaultConverter());
        assertEquals(12.34, converted.get("x"));
        assertEquals(1234, converted.get("y"));
        assertEquals("z", converted.get("z"));
    }

    private static final class ClassWithDefaultConverter {
        double x = 12.34;
        int y = 1234;
        String z = "z";
    }

    @Test
    public void testNested() {
        Converters.setDefaultConverter(A.class, a -> ImmutableMap.of("value", a.value));
        Converters.setNamedConverter(B.class, "B", b -> ImmutableMap.of("nested", b.nested, "value", b.value));
        Map<String, Object> converted = Converters.convert(new B(), "B");
        System.out.println(converted);
        assertTrue("Nested convertible type was not converted", converted.get("nested") instanceof Map);
        assertEquals("A", ((Map) converted.get("nested")).get("value"));
        assertEquals(ImmutableMap.of("value", "A"), converted.get("nested"));
        assertEquals("B", converted.get("value"));
    }

    public static final class A {
        String value = "A";
    }

    public static final class B {
        A nested = new A();
        String value = "B";
    }


}