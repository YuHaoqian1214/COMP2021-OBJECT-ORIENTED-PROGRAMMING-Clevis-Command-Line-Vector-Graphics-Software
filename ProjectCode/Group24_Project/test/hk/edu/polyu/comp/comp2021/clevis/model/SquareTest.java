
package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.shape.Square;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SquareTest {

    @Test
    void testSquareProperties() {
        Square square = new Square("sq1", 1.0, 2.0, 3.0);
        assertEquals(3.0, square.getSide());
        assertEquals("Square", square.getType());
        assertTrue(square.toString().contains("Square sq1"));
    }
}