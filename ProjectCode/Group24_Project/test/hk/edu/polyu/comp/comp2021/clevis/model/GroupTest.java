package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Line;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class GroupTest {

    @Test
    @DisplayName("Group: boundingBox/ move / coversPoint / type and string")
    void testGroupBehaviors() {
        Rectangle r = new Rectangle("R1", 1.0, 2.0, 3.0, 4.0);
        Line l = new Line("L1", -1.0, 0.0, 2.0, 0.0);
        Group g = new Group("G1", Arrays.asList(r, l));

        double[] box = g.getBoundingBox();

        assertArrayEquals(new double[]{-1.0, 0.0, 5.0, 6.0}, box);


        g.move(1.0, -2.0);
        assertEquals(2.0, r.getX());
        assertEquals(0.0, r.getY());
        assertEquals(0.0, l.getX1());
        assertEquals(-2.0, l.getY1());
        assertEquals(3.0, l.getX2());
        assertEquals(-2.0, l.getY2());


        assertTrue(g.coversPoint(2.5, -2.0));
        assertFalse(g.coversPoint(100, 100));

        assertEquals("Group", g.getType());
        String s = g.toString();
        assertTrue(s.startsWith("Group G1"));
        assertTrue(s.contains("R1"));
        assertTrue(s.contains("L1"));
    }
}