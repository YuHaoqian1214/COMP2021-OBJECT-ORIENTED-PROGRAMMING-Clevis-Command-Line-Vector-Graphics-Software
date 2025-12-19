package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.Command;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class CommandTest {

    @Test
    @DisplayName("fromString case insensitive + unknown returns null")
    void testFromString() {
        assertEquals(Command.RECTANGLE, Command.fromString("rectangle"));
        assertEquals(Command.CIRCLE, Command.fromString("CIRCLE"));
        assertNull(Command.fromString("unknown"));
    }

    @Test
    @DisplayName("checkArgs: GROUP uses >=; others use ==")
    void testCheckArgs() {
        assertTrue(Command.GROUP.checkArgs(2));
        assertTrue(Command.GROUP.checkArgs(5));
        assertFalse(Command.GROUP.checkArgs(1));
        assertTrue(Command.RECTANGLE.checkArgs(5));
        assertFalse(Command.RECTANGLE.checkArgs(4));
        assertTrue(Command.UNDO.checkArgs(0));
        assertFalse(Command.UNDO.checkArgs(1));
    }

    @Test
    @DisplayName("Enum getter")
    void testEnumGetters() {
        assertEquals("rectangle", Command.RECTANGLE.getCommandName());
        assertEquals(5, Command.RECTANGLE.getArgs());
        assertEquals("quit", Command.QUIT.getCommandName());
        assertEquals(0, Command.QUIT.getArgs());
    }
}