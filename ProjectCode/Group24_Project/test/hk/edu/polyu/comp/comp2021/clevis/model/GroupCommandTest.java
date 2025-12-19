package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.GroupCommand;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Line;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GroupCommandTest {

    @Test
    @DisplayName("GroupCommand: execute/undo/redo (only assert group add/remove, avoid strong dependency on member side effects)")
    void testExecuteUndoRedo() {
        Clevis clevis = new Clevis();
        Rectangle rect = new Rectangle("R1", 0, 0, 10, 5);
        Line line = new Line("L1", 2, 2, 8, 3);
        clevis.addShapeDirectly(rect);
        clevis.addShapeDirectly(line);

        List<String> names = Arrays.asList("R1", "L1");
        GroupCommand cmd = new GroupCommand(clevis, "G1", names);


        cmd.execute();
        assertTrue(clevis.getShapeMap().containsKey("G1"));


        cmd.undo();
        assertFalse(clevis.getShapeMap().containsKey("G1"));


        cmd.redo();
        assertTrue(clevis.getShapeMap().containsKey("G1"));
    }
}