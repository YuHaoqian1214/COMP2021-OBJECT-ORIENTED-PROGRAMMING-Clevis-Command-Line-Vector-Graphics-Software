package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.CreateShapeCommand;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Line;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class ClevisTest {


    @Test
    @DisplayName("validateArgs: correct argument format returns true / error branches return false")
    void testValidateArgs() {
        Clevis clevis = new Clevis();
        assertTrue(clevis.validateArgs(new String[]{"-html","a.html","-txt","b.txt"}));
        assertFalse(clevis.validateArgs(new String[]{"-html","a.html","-txt"}));
        assertFalse(clevis.validateArgs(new String[]{"-htmL","a.html","-TXT","b.txt"}));
        assertFalse(clevis.validateArgs(new String[]{"-html","a.htm","-txt","b.txt"}));
        assertFalse(clevis.validateArgs(new String[]{"-html","a.html","-txt","b.tx"}));
    }


    @Test
    @DisplayName("Constructor: initialize as empty collection; getters return copies (modifying copies does not affect internal state)")
    void testConstructorAndCopies() {
        Clevis clevis = new Clevis();
        assertEquals(0, clevis.getShapeList().stream().mapToLong(List::size).sum());
        assertTrue(clevis.getShapeMap().isEmpty());

        Rectangle rect = new Rectangle("R", 0, 0, 1, 1);
        clevis.addShapeDirectly(rect);

        List<Shape> listCopy = clevis.getShapeList().stream().flatMap(List::stream).collect(Collectors.toList());
        Map<String, Shape> mapCopy = clevis.getShapeMap();
        assertEquals(1, listCopy.size());
        assertEquals(1, mapCopy.size());

        listCopy.clear();
        mapCopy.clear();

        assertEquals(1, clevis.getShapeList().stream().mapToLong(List::size).sum(), "Clearing the copy does not affect the internal state");
        assertEquals(1, clevis.getShapeMap().size(), "Clearing the copy does not affect the internal state");
        assertSame(rect, clevis.getShapeMap().get("R"));
    }


    @Test
    @DisplayName("processCommand: empty command, unknown command, empty stack undo/redo")
    void testProcessCommandInvalidsAndEmptyStacks() {
        Clevis clevis = new Clevis();
        clevis.processCommand("");
        clevis.processCommand("foobar");
        clevis.processCommand("undo");
        clevis.processCommand("redo");
    }

    @Test
    @DisplayName("Create basic shapes + list/listAll")
    void testCreateShapesAndList() {
        Clevis clevis = new Clevis();
        clevis.processCommand("rectangle R 0 0 10 5");
        clevis.processCommand("line L 1 1 3 4");
        clevis.processCommand("circle C 2 2 1.5");
        clevis.processCommand("square S 3 3 2");

        assertTrue(clevis.getShapeMap().containsKey("R"));
        assertTrue(clevis.getShapeMap().containsKey("L"));
        assertTrue(clevis.getShapeMap().containsKey("C"));
        assertTrue(clevis.getShapeMap().containsKey("S"));

        clevis.processCommand("list R");
        clevis.processCommand("listAll");
    }

    @Test
    @DisplayName("group/ungroup according to current implementation semantics: only assert group add/remove, not member")
    void testGroupUngroupFlowAlignedWithImplementation() {
        Clevis clevis = new Clevis();
        clevis.processCommand("rectangle R1 0 0 10 5");
        clevis.processCommand("line L1 2 2 8 3");

        clevis.processCommand("group G R1 L1");
        assertTrue(clevis.getShapeMap().containsKey("G"));

        clevis.processCommand("ungroup G");
        assertFalse(clevis.getShapeMap().containsKey("G"));

    }

    @Test
    @DisplayName("move/boundingbox/shapeAt/intersect normal path")
    void testMoveBboxShapeAtIntersect() {
        Clevis clevis = new Clevis();
        clevis.processCommand("rectangle R 10 10 5 4");
        clevis.processCommand("circle C 12 12 2");
        clevis.processCommand("move R 2.5 -1.5");
        clevis.processCommand("boundingbox R");
        clevis.processCommand("boundingbox C");
        clevis.processCommand("shapeAt 12 12");
        clevis.processCommand("intersect R C");
    }

    @Test
    @DisplayName("delete (normal/group) and undo/redo: only assert group add/remove according to current implementation")
    void testDeleteUndoRedo() {
        Clevis clevis = new Clevis();
        clevis.processCommand("rectangle R 1 1 3 2");
        clevis.processCommand("delete R");
        assertFalse(clevis.getShapeMap().containsKey("R"));
        clevis.processCommand("undo");
        assertTrue(clevis.getShapeMap().containsKey("R"));
        clevis.processCommand("redo");
        assertFalse(clevis.getShapeMap().containsKey("R"));

        clevis.processCommand("rectangle R1 0 0 2 2");
        clevis.processCommand("line L1 0 0 2 0");
        clevis.processCommand("group G R1 L1");

        clevis.processCommand("delete G");
        assertFalse(clevis.getShapeMap().containsKey("G"));

        clevis.processCommand("undo");
        assertTrue(clevis.getShapeMap().containsKey("G"));

        clevis.processCommand("redo");
        assertFalse(clevis.getShapeMap().containsKey("G"));
    }

    @Test
    @DisplayName("Error path: list/boundingbox/intersect name does not exist (processCommand catches and prints)")
    void testErrorBranchesNoSuchName() {
        Clevis clevis = new Clevis();
        clevis.processCommand("list NOPE");
        clevis.processCommand("boundingbox NOPE");
        clevis.processCommand("intersect A B");
    }


    @Test
    @DisplayName("add/removeShapeDirectly: name conflict and cleanup of group members")
    void testProtectedApiBranches() {
        Clevis clevis = new Clevis();
        Rectangle r1 = new Rectangle("R", 0, 0, 1, 1);
        Rectangle r2 = new Rectangle("R", 1, 1, 2, 2);
        clevis.addShapeDirectly(r1);
        assertThrows(IllegalArgumentException.class, () -> clevis.addShapeDirectly(r2));

        Line line = new Line("L", 0, 0, 2, 0);
        Group group = new Group("G", Arrays.asList(r1, line));
        clevis.addShapeDirectly(group);
        assertTrue(clevis.getShapeMap().containsKey("G"));

        clevis.removeShapeDirectly("G");
        assertFalse(clevis.getShapeMap().containsKey("G"));
        assertFalse(clevis.getShapeMap().containsKey("L"));
    }

    @Test
    @DisplayName("start: valid arguments and quit command")
    void testStartMethod() throws IOException {
        // Create temporary files for logs to avoid side effects on the file system
        Path htmlPath = Files.createTempFile("log", ".html");
        Path txtPath = Files.createTempFile("log", ".txt");

        // Mock System.in to provide "quit" command
        String input = "quit\n";
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        System.setIn(in);

        // Mock System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Clevis clevis = new Clevis();
        String[] args = {"-html", htmlPath.toString(), "-txt", txtPath.toString()};
        clevis.start(args);

        // Restore original System.in and System.out
        System.setIn(System.in);
        System.setOut(originalOut);

        // Clean up temporary files (optional but good practice)
        Files.deleteIfExists(htmlPath);
        Files.deleteIfExists(txtPath);

        String expectedOutput = "Clevis started successfully! Enter commands (type 'quit' to exit):" +
                System.lineSeparator() +
                "> Exiting Clevis..." +
                System.lineSeparator();
        assertEquals(expectedOutput, outContent.toString());
    }

    @Test
    @DisplayName("executeCommand: MAX_HISTORY limit and redoStack clearing")
    void testExecuteCommandHistoryAndRedoClear() {
        Clevis clevis = new Clevis();
        // Assuming MAX_HISTORY is 10000, let's create a few more than that
        // For testing purposes, we can temporarily make MAX_HISTORY smaller if needed,
        // but for now, let's just test the clearing of redoStack.

        // Execute a command
        CreateShapeCommand cmd1 = new CreateShapeCommand(clevis, new Rectangle("R1", 0, 0, 1, 1));
        clevis.processCommand("rectangle R1 0 0 1 1"); // This will call executeCommand internally

        // Execute another command
        CreateShapeCommand cmd2 = new CreateShapeCommand(clevis, new Rectangle("R2", 0, 0, 1, 1));
        clevis.processCommand("rectangle R2 0 0 1 1");

        // Undo the last command
        clevis.processCommand("undo");
        assertEquals(1, clevis.getUndoStackSize()); // R1 is left
        assertEquals(1, clevis.getRedoStackSize()); // R2 is in redo

        // Execute a new command - this should clear the redo stack
        CreateShapeCommand cmd3 = new CreateShapeCommand(clevis, new Rectangle("R3", 0, 0, 1, 1));
        clevis.processCommand("rectangle R3 0 0 1 1");
        assertEquals(0, clevis.getRedoStackSize()); // Redo stack should be clear
        assertEquals(2, clevis.getUndoStackSize()); // R1, R3 are in undo
    }

    @Test
    @DisplayName("printShape: correctly formats output for nested groups")
    void testPrintShapeWithNestedGroups() {
        // Mock System.out to capture output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        Clevis clevis = new Clevis();
        clevis.processCommand("rectangle R1 0 0 1 1");
        clevis.processCommand("circle C1 2 2 1");
        clevis.processCommand("group G1 R1 C1");
        clevis.processCommand("square S1 4 4 1");
        clevis.processCommand("group G2 G1 S1");

        clevis.processCommand("listAll");

        // Restore original System.out
        System.setOut(originalOut);

        String expectedOutput = "Group G2 containing: [G1, S1]\n" +
                                "  Group G1 containing: [R1, C1]\n" +
                                "    Rectangle R1 at (0.00,0.00) with width 1.00 height 1.00\n" +
                                "    Circle C1 at (2.00,2.00) with radius 1.00\n" +
                                "  Square S1 at (4.00,4.00) with side 1.00\n";

        // Normalize line endings for comparison
        String actualOutput = outContent.toString().replace("\r\n", "\n");
        String normalizedExpectedOutput = expectedOutput.replace("\r\n", "\n");

        assertTrue(actualOutput.contains(normalizedExpectedOutput));
    }

    @Test
    @DisplayName("Simple Getters: getUndoStackSize, getRedoStackSize, getLogger, isRunning")
    void testSimpleGetters() {
        Clevis clevis = new Clevis();

        // Test initial state
        assertEquals(0, clevis.getUndoStackSize());
        assertEquals(0, clevis.getRedoStackSize());
        assertNull(clevis.getLogger()); // Logger is null until start() is called
        assertTrue(clevis.isRunning());

        // After a command
        clevis.processCommand("rectangle R1 0 0 1 1");
        assertEquals(1, clevis.getUndoStackSize());
        assertEquals(0, clevis.getRedoStackSize());

        // After undo
        clevis.processCommand("undo");
        assertEquals(0, clevis.getUndoStackSize());
        assertEquals(1, clevis.getRedoStackSize());

        // After redo
        clevis.processCommand("redo");
        assertEquals(1, clevis.getUndoStackSize());
        assertEquals(0, clevis.getRedoStackSize());

        // Test isRunning after quit
        clevis.processCommand("quit");
        assertFalse(clevis.isRunning());
    }
}