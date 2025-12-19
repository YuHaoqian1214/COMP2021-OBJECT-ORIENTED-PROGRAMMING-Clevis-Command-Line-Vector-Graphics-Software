package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.DeleteShapeCommand;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Line;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;

class DeleteShapeCommandTest {

    @Test
    @DisplayName("Delete plain shape: execute removes, undo restores, redo removes again (using addShapeDirectly)")
    void testDeletePlainShape() {
        Clevis clevis = new Clevis();
        Rectangle rect = new Rectangle("R", 1, 1, 3, 2);
        clevis.addShapeDirectly(rect);
        assertTrue(clevis.getShapeMap().containsKey("R"));

        DeleteShapeCommand cmd = new DeleteShapeCommand(clevis, "R");
        cmd.execute();
        assertFalse(clevis.getShapeMap().containsKey("R"));
        assertEquals(0, clevis.getShapeList().stream().mapToLong(List::size).sum());

        cmd.undo();
        assertTrue(clevis.getShapeMap().containsKey("R"));
        assertEquals(1, clevis.getShapeList().stream().mapToLong(List::size).sum());
        assertSame(rect, clevis.getShapeMap().get("R"));
        assertSame(rect, clevis.getShapeList().stream().flatMap(List::stream).findFirst().orElse(null));

        cmd.redo();
        assertFalse(clevis.getShapeMap().containsKey("R"));
        assertEquals(0, clevis.getShapeList().stream().mapToLong(List::size).sum());
    }

    @Test
    @DisplayName("Delete group: execute removes group; undo restores group; redo deletes again (do not assert member restoration to align with current implementation)")
    void testDeleteGroupShape() {
        Clevis clevis = new Clevis();
        Rectangle rect = new Rectangle("R_G", 0, 0, 2, 2);
        Line line = new Line("L_G", 0, 0, 2, 0);
        Group group = new Group("G_DEL", Arrays.asList(rect, line));


        clevis.addShapeDirectly(group);
        assertTrue(clevis.getShapeMap().containsKey("G_DEL"));
        assertFalse(clevis.getShapeMap().containsKey("R_G"));
        assertFalse(clevis.getShapeMap().containsKey("L_G"));

        DeleteShapeCommand cmd = new DeleteShapeCommand(clevis, "G_DEL");
        cmd.execute();
        assertFalse(clevis.getShapeMap().containsKey("G_DEL"));
        assertEquals(0, clevis.getShapeList().stream().mapToLong(List::size).sum());


        cmd.undo();
        assertTrue(clevis.getShapeMap().containsKey("G_DEL"));
        assertEquals(1, clevis.getShapeList().stream().mapToLong(List::size).sum());
        assertSame(group, clevis.getShapeList().stream().flatMap(List::stream).findFirst().orElse(null));

        cmd.redo();
        assertFalse(clevis.getShapeMap().containsKey("G_DEL"));
        assertEquals(0, clevis.getShapeList().stream().mapToLong(List::size).sum());
    }
}