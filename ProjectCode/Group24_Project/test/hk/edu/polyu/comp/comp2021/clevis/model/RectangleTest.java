package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class RectangleTest {

    @Test
    @DisplayName("Constructor success + getter validation")
    void testConstructorAndGetters() {
        Rectangle r = new Rectangle("R1", 10.0, 20.0, 30.0, 40.0);
        assertEquals("R1", r.getName());
        assertEquals(10.0, r.getX());
        assertEquals(20.0, r.getY());
        assertEquals(30.0, r.getWidth());
        assertEquals(40.0, r.getHeight());
    }

    @Test
    @DisplayName("Illegal width/height triggers IllegalArgumentException")
    void testConstructorInvalidSize() {
        assertThrows(IllegalArgumentException.class,
            () -> new Rectangle("Bad", 0, 0, 0.0, 1.0));
        assertThrows(IllegalArgumentException.class,
            () -> new Rectangle("Bad2", 0, 0, 1.0, 0.0));
    }

    @Test
    @DisplayName("move updates coordinates")
    void testMove() {
        Rectangle r = new Rectangle("R2", 1.0, 2.0, 3.0, 4.0);
        r.move(5.0, -3.0);
        assertEquals(6.0, r.getX());
        assertEquals(-1.0, r.getY());
    }

    @Test
    @DisplayName("getBoundingBox returns correct array")
    void testBoundingBox() {
        Rectangle r = new Rectangle("R3", 3.0, 4.0, 5.0, 6.0);
        double[] box = r.getBoundingBox();
        assertArrayEquals(new double[]{3.0, 4.0, 5.0, 6.0}, box);
    }

    @Test
    @DisplayName("coversPoint: near all four edges triggers distance calculation")
    void testCoversPointNearEdges() {
        Rectangle r = new Rectangle("R4", 0.0, 0.0, 10.0, 6.0);
        assertTrue(r.coversPoint(0.02, 3.0));
        assertTrue(r.coversPoint(9.98, 3.0));
        assertTrue(r.coversPoint(5.0, 0.02));
        assertTrue(r.coversPoint(5.0, 5.98));
        assertFalse(r.coversPoint(20.0, 20.0));
    }

    @Test
    @DisplayName("Type and string output")
    void testTypeAndToString() {
        Rectangle r = new Rectangle("R5", 1.234, 5.678, 9.0, 8.0);
        assertEquals("Rectangle", r.getType());
        String s = r.toString();
        assertTrue(s.startsWith("Rectangle R5"));
        assertTrue(s.contains("width"));
        assertTrue(s.contains("height"));
    }
}