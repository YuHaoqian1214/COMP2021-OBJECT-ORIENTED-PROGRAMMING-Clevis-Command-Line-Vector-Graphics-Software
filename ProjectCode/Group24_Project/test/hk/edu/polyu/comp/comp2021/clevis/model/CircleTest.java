package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.shape.Circle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CircleTest {

    @Test
    @DisplayName("Constructor and getter/BoundingBox/Move")
    void testConstructorAndBasics() {
        Circle c = new Circle("C1", 10.0, 20.0, 5.0);
        assertEquals("C1", c.getName());
        assertEquals(10.0, c.getX());
        assertEquals(20.0, c.getY());
        assertEquals(5.0, c.getRadius());
        assertArrayEquals(new double[]{5.0, 15.0, 10.0, 10.0}, c.getBoundingBox());
        c.move(-2.0, 3.0);
        assertEquals(8.0, c.getX());
        assertEquals(23.0, c.getY());
        assertArrayEquals(new double[]{3.0, 18.0, 10.0, 10.0}, c.getBoundingBox());
    }

    @Test
    @DisplayName("Illegal radius throws exception + coversPoint/type/string")
    void testRadiusValidationAndOtherMethods() {
        assertThrows(IllegalArgumentException.class, () -> new Circle("BAD", 0, 0, 0.0));
        Circle c = new Circle("C2", 0.0, 0.0, 10.0);
        assertTrue(c.coversPoint(10.0, 0.02));
        assertFalse(c.coversPoint(0.0, 0.0));
        assertEquals("Circle", c.getType());
        String s = c.toString();
        assertTrue(s.startsWith("Circle C2"));
        assertTrue(s.contains("radius"));
    }
}