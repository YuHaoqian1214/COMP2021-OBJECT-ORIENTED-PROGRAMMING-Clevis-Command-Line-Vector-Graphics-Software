package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.shape.Line;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class LineTest {

    @Test
    @DisplayName("Constructor and getter")
    void testConstructorAndGetters() {
        Line l = new Line("L1", 1.0, 2.0, 7.0, 6.0);
        assertEquals("L1", l.getName());
        assertEquals(1.0, l.getX1());
        assertEquals(2.0, l.getY1());
        assertEquals(7.0, l.getX2());
        assertEquals(6.0, l.getY2());
    }

    @Test
    @DisplayName("move and getBoundingBox")
    void testMoveAndBoundingBox() {
        Line l = new Line("L2", -1.0, 3.0, 4.0, -2.0);
        double[] box = l.getBoundingBox();
        assertArrayEquals(new double[]{-1.0, -2.0, 5.0, 5.0}, box);
        l.move(2.5, -1.5);
        assertEquals(1.5, l.getX1());
        assertEquals(1.5, l.getY1());
        assertEquals(6.5, l.getX2());
        assertEquals(-3.5, l.getY2());
        double[] box2 = l.getBoundingBox();
        assertArrayEquals(new double[]{1.5, -3.5, 5.0, 5.0}, box2);
    }

    @Test
    @DisplayName("coversPoint and type/string")
    void testCoversPointTypeToString() {
        Line l = new Line("L3", 0.0, 0.0, 10.0, 0.0);
        assertTrue(l.coversPoint(5.0, 0.01));
        assertFalse(l.coversPoint(5.0, 2.0));
        assertEquals("Line", l.getType());
        String s = l.toString();
        assertTrue(s.startsWith("Line L3"));
        assertTrue(s.contains("from"));
        assertTrue(s.contains("to"));
    }
}