package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.MoveShapeCommand;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MoveShapeCommandTest {

    @Test
    @DisplayName("execute/undo/redo: existing shape is correctly moved and restored (initialized with addShapeDirectly)")
    void testExecuteUndoRedoOnExistingShape() {
        Clevis clevis = new Clevis();
        Rectangle rect = new Rectangle("R", 10.0, 10.0, 5.0, 4.0);

        clevis.addShapeDirectly(rect);

        MoveShapeCommand cmd = new MoveShapeCommand(clevis, "R", 2.5, -1.5);
        cmd.execute();
        assertEquals(12.5, rect.getX());
        assertEquals(8.5, rect.getY());

        cmd.undo();
        assertEquals(10.0, rect.getX());
        assertEquals(10.0, rect.getY());

        cmd.redo();
        assertEquals(12.5, rect.getX());
        assertEquals(8.5, rect.getY());
    }

    @Test
    @DisplayName("execute/undo/redo: safely return when target name does not exist (no exception thrown)")
    void testExecuteUndoRedoOnMissingShape() {
        Clevis clevis = new Clevis();
        MoveShapeCommand cmd = new MoveShapeCommand(clevis, "NO_SUCH", 1.0, 1.0);
        assertDoesNotThrow(cmd::execute);
        assertDoesNotThrow(cmd::undo);
        assertDoesNotThrow(cmd::redo);
    }
}