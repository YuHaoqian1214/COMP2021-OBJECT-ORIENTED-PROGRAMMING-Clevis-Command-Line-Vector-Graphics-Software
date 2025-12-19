package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/** Used to test protected methods of Shape */
class ShapeProtectedMethodsTest {

    static class BoxShape extends Shape {
        private final double[] box; // {x, y, w, h}
        BoxShape(String name, double x, double y, double w, double h) {
            super(name);
            this.box = new double[]{x, y, w, h};
        }
        @Override
        public double[] getBoundingBox() { return box; }
        @Override
        public void move(double dx, double dy) { box[0] += dx; box[1] += dy; }
        @Override
        public boolean coversPoint(double px, double py) { return false; }
        @Override
        public String getType() { return "Box"; }
        @Override
        public String toString() { return "BoxShape:" + getName(); }
        double callDistance(double px, double py, double x1, double y1, double x2, double y2) {
            return distanceToLineSegment(px, py, x1, y1, x2, y2);
        }
        boolean callIntersects(Shape other) { return intersects(other); }
    }

    @Test
    @DisplayName("getName comes from Shape base class")
    void testGetNameFromBase() {
        BoxShape s = new BoxShape("S1", 0, 0, 1, 1);
        assertEquals("S1", s.getName());
    }

    @Test
    @DisplayName("intersects: intersecting and non-intersecting cases")
    void testIntersects() {
        BoxShape a = new BoxShape("A", 0, 0, 10, 10);
        BoxShape b = new BoxShape("B", 9, 9, 5, 5);
        BoxShape c = new BoxShape("C", 20, 20, 3, 3);
        assertTrue(a.callIntersects(b));
        assertFalse(a.callIntersects(c));
    }

    @Test
    @DisplayName("distanceToLineSegment: zero-length segment branch")
    void testDistanceZeroLengthSegment() {
        BoxShape s = new BoxShape("D", 0, 0, 1, 1);
        double d = s.callDistance(3, 4, 1, 1, 1, 1);
        assertEquals(Math.sqrt(13), d, 1e-9);
    }

    @Test
    @DisplayName("distanceToLineSegment: regular branch (projection between 0..1)")
    void testDistanceRegularSegment() {
        BoxShape s = new BoxShape("E", 0, 0, 1, 1);
        double d = s.callDistance(5, 2, 0, 0, 10, 0);
        assertEquals(2.0, d, 1e-9);
    }
}