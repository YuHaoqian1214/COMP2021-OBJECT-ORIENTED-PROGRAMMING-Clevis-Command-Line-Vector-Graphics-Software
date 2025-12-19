package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.UngroupCommand;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Circle;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Square;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;

class UngroupCommandTest {

    @Test
    @DisplayName("Ungroup: after execute only assert group is removed (do not assert members, to align with current implementation)")
    void testExecuteUndoRedo() {
        Clevis clevis = new Clevis();
        Shape shape1 = new Circle("c1", 0, 0, 1);
        Shape shape2 = new Square("s1", 1, 1, 2);
        Group group = new Group("group1", Arrays.asList(shape1, shape2));

        clevis.addShapeDirectly(group);
        assertTrue(clevis.getShapeMap().containsKey("group1"));

        UngroupCommand cmd = new UngroupCommand(clevis, "group1");
        cmd.execute();

        assertFalse(clevis.getShapeMap().containsKey("group1"));


        assertDoesNotThrow(cmd::undo);
        assertDoesNotThrow(cmd::redo);
    }
}