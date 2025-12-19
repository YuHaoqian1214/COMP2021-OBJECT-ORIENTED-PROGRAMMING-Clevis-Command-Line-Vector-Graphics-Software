
package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.AbstractCommand;
import hk.edu.polyu.comp.comp2021.clevis.model.command.UndoableCommand;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UndoableCommandTest {

    class TestCommand extends AbstractCommand {
        public TestCommand(Clevis clevis, String name) {
            super(clevis, name);
        }

        @Override
        public void execute() {}

        @Override
        public void undo() {}

        @Override
        public void redo() {}
    }

    @Test
    void testGetName() {
        Clevis clevis = new Clevis();
        TestCommand cmd = new TestCommand(clevis, "Test");
        assertEquals("Test", cmd.getName());
    }

    @Test
    void testInterfaceMethods() {
        Clevis clevis = new Clevis();
        UndoableCommand cmd = new TestCommand(clevis, "Test");
        cmd.execute();
        cmd.undo();
        cmd.redo();
        assertEquals("Test", cmd.getName());
    }
}