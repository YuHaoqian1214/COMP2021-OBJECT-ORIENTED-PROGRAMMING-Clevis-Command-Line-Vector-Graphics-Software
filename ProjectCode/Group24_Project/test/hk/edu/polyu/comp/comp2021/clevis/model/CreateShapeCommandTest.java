package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.CreateShapeCommand;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CreateShapeCommandTest {

    @Test
    @DisplayName("CreateShapeCommand: execute only effective once, undo/redo works (no Mockito)")
    void testExecuteUndoRedo() {
        Rectangle rect = new Rectangle("R_CREATE", 0, 0, 2, 2);


        Clevis clevis = new Clevis();

        CreateShapeCommand cmd = new CreateShapeCommand(clevis, rect);


        cmd.execute();
        assertEquals(1, clevis.getShapeList().stream().mapToLong(List::size).sum());
        assertTrue(clevis.getShapeMap().containsKey("R_CREATE"));
        assertSame(rect, clevis.getShapeList().stream().flatMap(List::stream).findFirst().orElse(null));


        cmd.execute();
        assertEquals(1, clevis.getShapeList().stream().mapToLong(List::size).sum());
        assertTrue(clevis.getShapeMap().containsKey("R_CREATE"));


        cmd.undo();
        assertEquals(0, clevis.getShapeList().stream().mapToLong(List::size).sum());
        assertFalse(clevis.getShapeMap().containsKey("R_CREATE"));


        cmd.redo();
        assertEquals(1, clevis.getShapeList().stream().mapToLong(List::size).sum());
        assertTrue(clevis.getShapeMap().containsKey("R_CREATE"));
    }
}