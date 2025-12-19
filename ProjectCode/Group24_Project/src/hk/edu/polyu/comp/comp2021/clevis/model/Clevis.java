package hk.edu.polyu.comp.comp2021.clevis.model;

import hk.edu.polyu.comp.comp2021.clevis.model.command.*;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.*;
import hk.edu.polyu.comp.comp2021.clevis.model.util.DoubleFormatter;
import hk.edu.polyu.comp.comp2021.clevis.model.util.GroupProtection;
import java.io.IOException;
import java.util.*;

/**
 * The main model class for the Clevis application, responsible for managing shapes,
 * handling user commands, maintaining command history for undo/redo, and logging.
 * It coordinates the creation, modification, and removal of shapes (including groups),
 * and processes user input to execute corresponding operations.
 */
public class Clevis {
    /**
     * A nested list structure to store shapes, with inner lists having a maximum capacity
     * defined by {@link #INNER_LIST_CAPACITY}. This structure helps in efficiently managing
     * a large number of shapes.
     */
    private final List<List<Shape>> shapeList;

    /**
     * A map that associates shape names (as keys) with their corresponding {@link Shape} objects.
     * Facilitates quick lookup of shapes by their unique names.
     */
    private final Map<String, Shape> shapeMap;

    /**
     * The logger instance used to record user commands into HTML and TXT log files.
     */
    private Logger logger;

    /**
     * A flag indicating whether the application is running. Controls the main command loop.
     */
    private boolean isRunning;

    /**
     * A stack that stores {@link UndoableCommand} instances representing the history of executed commands.
     * Used to support the undo operation.
     */
    private final Stack<UndoableCommand> undoStack;

    /**
     * A stack that stores {@link UndoableCommand} instances that have been undone.
     * Used to support the redo operation.
     */
    private final Stack<UndoableCommand> redoStack;

    /**
     * The maximum number of commands that can be stored in the undo history.
     * If exceeded, the oldest command is removed.
     */
    private static final int MAX_HISTORY = 10000;

    /**
     * The maximum capacity of each inner list in {@link #shapeList}.
     * When an inner list reaches this capacity, a new inner list is created.
     */
    private static final int INNER_LIST_CAPACITY = 1024;

    /**
     * Constructs a new Clevis instance, initializing all data structures for managing shapes,
     * command history, and starting the application in a running state.
     */
    public Clevis() {
        this.shapeList = new ArrayList<>();
        this.shapeMap = new HashMap<>();
        this.isRunning = true;
        this.undoStack = new Stack<>();
        this.redoStack = new Stack<>();
        this.shapeList.add(new ArrayList<>(INNER_LIST_CAPACITY));
    }

    /**
     * Retrieves an available inner list from {@link #shapeList} that has not yet reached
     * {@link #INNER_LIST_CAPACITY}. If all existing inner lists are full, a new inner list
     * is created and added to {@link #shapeList}.
     *
     * @return an available (non-full) inner list for storing shapes
     */
    private List<Shape> getAvailableInnerList() {
        for (List<Shape> innerList : shapeList) {
            if (innerList.size() < INNER_LIST_CAPACITY) {
                return innerList;
            }
        }
        List<Shape> newInnerList = new ArrayList<>(INNER_LIST_CAPACITY);
        shapeList.add(newInnerList);
        return newInnerList;
    }

    /**
     * Removes a specific shape from the nested list structure ({@link #shapeList}).
     * If the inner list containing the shape becomes empty after removal, the inner list
     * is also removed from {@link #shapeList}.
     *
     * @param shape the shape to be removed
     * @return true if the shape was successfully removed; false otherwise
     */
    public boolean removeShapeFromNestedList(Shape shape) {
        for (int i = shapeList.size() - 1; i >= 0; i--) {
            List<Shape> innerList = shapeList.get(i);
            if (innerList.remove(shape)) {
                if (innerList.isEmpty()) {
                    shapeList.remove(i);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Flattens the nested {@link #shapeList} into a single list containing all shapes.
     * Useful for operations requiring a linear collection of all shapes.
     *
     * @return a flattened list of all shapes managed by this Clevis instance
     */
    private List<Shape> getFlattenedShapeList() {
        List<Shape> flattened = new ArrayList<>();
        for (List<Shape> innerList : shapeList) {
            flattened.addAll(innerList);
        }
        return flattened;
    }

    /**
     * Directly adds a shape to the managed data structures ({@link #shapeList} and {@link #shapeMap})
     * without creating a command (bypasses undo/redo tracking). Throws an exception if a shape with
     * the same name already exists.
     *
     * @param shape the shape to be added
     * @throws IllegalArgumentException if a shape with the same name is already present
     */
    public void addShapeDirectly(Shape shape) {
        String name = shape.getName();
        if (shapeMap.containsKey(name)) {
            throw new IllegalArgumentException("Shape name '" + name + "' already exists!");
        }
        List<Shape> targetInnerList = getAvailableInnerList();
        targetInnerList.add(shape);
        shapeMap.put(name, shape);
    }

    /**
     * Directly removes a shape (and its components, if it is a group) from the managed data structures
     * without creating a command (bypasses undo/redo tracking). Does nothing if the shape does not exist.
     *
     * @param name the name of the shape to be removed
     */
    public void removeShapeDirectly(String name) {
        Shape shape = shapeMap.get(name);
        if (shape == null) return;

        removeShapeFromNestedList(shape);
        if (shape instanceof Group group) {
            for (Shape component : group.getComponents()) {
                removeShapeFromNestedList(component);
                shapeMap.remove(component.getName());
            }
        }
        shapeMap.remove(name);
    }

    /**
     * Starts the Clevis application. Validates command-line arguments, initializes the logger,
     * and starts the main command loop for processing user input.
     *
     * @param args command-line arguments, expected in the format: [-html log.html -txt log.txt]
     */
    public void start(String[] args) {
        if(!validateArgs(args)) {
            System.out.println("Usage error! Correct format: java Application -html log.html -txt log.txt");
            return;
        }

        String htmlPath = args[1];
        String txtPath = args[3];

        try(Logger logger = new Logger(htmlPath, txtPath)) {
            this.logger = logger;
            System.out.println("Clevis started successfully! Enter commands (type 'quit' to exit):");
            startCommandLoop();
        }
        catch(IOException e) {
            System.out.println("Error: Failed to initialize log files! Reason: " + e.getMessage());
        }
    }

    /**
     * Validates the command-line arguments for correct format and file extensions.
     * Expects exactly 4 arguments: [-html path.html -txt path.txt].
     *
     * @param args the command-line arguments to validate
     * @return true if the arguments are valid; false otherwise (with error messages printed)
     */
    protected boolean validateArgs(String[] args) {
        if(args.length != 4 || !args[0].equals("-html") || !args[2].equals("-txt")) {
            return false;
        }

        if(!args[1].toLowerCase().endsWith(".html")) {
            System.out.println("Error: HTML log file must have .html extension");
            return false;
        }

        if(!args[3].toLowerCase().endsWith(".txt")) {
            System.out.println("Error: TXT log file must have .txt extension");
            return false;
        }

        return true;
    }

    /**
     * Starts the main command loop, which continuously reads user input, logs the command,
     * and processes it until the application is stopped (via the 'quit' command).
     */
    private void startCommandLoop() {
        Scanner scanner = new Scanner(System.in);

        while(isRunning) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if(logger != null) {
                try {
                    logger.logCommand(input);
                }
                catch(IOException e) {
                    throw new RuntimeException(e);
                }
            }

            processCommand(input);
        }

        scanner.close();
    }

    /**
     * Processes a user input command by parsing it, validating the command and its arguments,
     * and dispatching to the appropriate handler method. Handles unknown commands and invalid
     * argument counts by printing error messages.
     *
     * @param input the user input string representing the command
     */
    public void processCommand(String input) {
        if(input.isEmpty()) {
            System.out.println("Empty command! Please try again.");
            return;
        }

        String[] parts = input.split("\\s+");
        String commandStr = parts[0];
        Command command = Command.fromString(commandStr);

        if(command == null) {
            System.out.println("Error: Unknown command '" + commandStr + "'");
            return;
        }

        if(!command.checkArgs(parts.length - 1)) {
            System.out.println("Error: Invalid number of arguments for command '" + commandStr + "'");
            return;
        }

        try{
            switch(command) {
                case RECTANGLE:
                    handleRectangleCommand(parts);
                    break;
                case LINE:
                    handleLineCommand(parts);
                    break;
                case CIRCLE:
                    handleCircleCommand(parts);
                    break;
                case SQUARE:
                    handleSquareCommand(parts);
                    break;
                case GROUP:
                    handleGroupCommand(parts);
                    break;
                case UNGROUP:
                    handleUngroupCommand(parts);
                    break;
                case DELETE:
                    handleDeleteCommand(parts);
                    break;
                case BOUNDINGBOX:
                    handleBoundingBoxCommand(parts);
                    break;
                case MOVE:
                    handleMoveCommand(parts);
                    break;
                case SHAPEAT:
                    handleShapeAtCommand(parts);
                    break;
                case INTERSECT:
                    handleIntersectCommand(parts);
                    break;
                case LIST:
                    handleListCommand(parts);
                    break;
                case LISTALL:
                    handleListAllCommand(parts);
                    break;
                case UNDO:
                    handleUndoCommand(parts);
                    break;
                case REDO:
                    handleRedoCommand(parts);
                    break;
                case QUIT:
                    isRunning = false;
                    System.out.println("Exiting Clevis...");
                    break;
            }
        }
        catch(Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Handles the 'rectangle' command by parsing arguments, creating a {@link Rectangle} instance,
     * wrapping it in a {@link CreateShapeCommand}, and executing the command.
     *
     * @param parts the split command parts, containing [rectangle, name, x, y, width, height]
     */
    private void handleRectangleCommand(String[] parts) {
        String name = parts[1];
        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        double width = Double.parseDouble(parts[4]);
        double height = Double.parseDouble(parts[5]);

        Rectangle rect = new Rectangle(name, x, y, width, height);
        CreateShapeCommand cmd = new CreateShapeCommand(this, rect);
        executeCommand(cmd);
        System.out.println("Created rectangle " + name);
    }

    /**
     * Handles the 'line' command by parsing arguments, creating a {@link Line} instance,
     * wrapping it in a {@link CreateShapeCommand}, and executing the command.
     *
     * @param parts the split command parts, containing [line, name, x1, y1, x2, y2]
     */
    private void handleLineCommand(String[] parts) {
        String name = parts[1];
        double x1 = Double.parseDouble(parts[2]);
        double y1 = Double.parseDouble(parts[3]);
        double x2 = Double.parseDouble(parts[4]);
        double y2 = Double.parseDouble(parts[5]);

        Line line = new Line(name, x1, y1, x2, y2);
        CreateShapeCommand cmd = new CreateShapeCommand(this, line);
        executeCommand(cmd);
        System.out.println("Created line " + name);
    }

    /**
     * Handles the 'circle' command by parsing arguments, creating a {@link Circle} instance,
     * wrapping it in a {@link CreateShapeCommand}, and executing the command.
     *
     * @param parts the split command parts, containing [circle, name, x, y, radius]
     */
    private void handleCircleCommand(String[] parts) {
        String name = parts[1];
        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        double radius = Double.parseDouble(parts[4]);

        Circle circ = new Circle(name, x, y, radius);
        CreateShapeCommand cmd = new CreateShapeCommand(this, circ);
        executeCommand(cmd);
        System.out.println("Created circle " + name);
    }

    /**
     * Handles the 'square' command by parsing arguments, creating a {@link Square} instance,
     * wrapping it in a {@link CreateShapeCommand}, and executing the command.
     *
     * @param parts the split command parts, containing [square, name, x, y, side]
     */
    private void handleSquareCommand(String[] parts) {
        String name = parts[1];
        double x = Double.parseDouble(parts[2]);
        double y = Double.parseDouble(parts[3]);
        double side = Double.parseDouble(parts[4]);

        Square squa = new Square(name, x, y, side);
        CreateShapeCommand cmd = new CreateShapeCommand(this, squa);
        executeCommand(cmd);
        System.out.println("Created square " + name);
    }

    /**
     * Handles the 'group' command by parsing the group name and component names,
     * creating a {@link GroupCommand}, and executing it to form a new group.
     *
     * @param parts the split command parts, containing [group, groupName, component1, component2, ...]
     */
    private void handleGroupCommand(String[] parts) {
        String groupName = parts[1];
        List<String> componentNames = new ArrayList<>();

        for(int i = 2; i < parts.length; i++) {
            componentNames.add(parts[i]);
        }

        GroupCommand cmd = new GroupCommand(this, groupName, componentNames);
        executeCommand(cmd);
        System.out.println("Created group " + groupName);
    }

    /**
     * Handles the 'ungroup' command by parsing the group name, creating an {@link UngroupCommand},
     * and executing it to ungroup the specified group.
     *
     * @param parts the split command parts, containing [ungroup, groupName]
     */
    private void handleUngroupCommand(String[] parts) {
        String groupName = parts[1];
        UngroupCommand cmd = new UngroupCommand(this, groupName);
        executeCommand(cmd);
        System.out.println("Ungrouped " + groupName);
    }

    /**
     * Handles the 'delete' command by validating the shape is not in a group, creating a
     * {@link DeleteShapeCommand}, and executing it to remove the shape.
     *
     * @param parts the split command parts, containing [delete, shapeName]
     */
    private void handleDeleteCommand(String[] parts) {
        String name = parts[1];

        GroupProtection.validateShapeNotInGroupForOperations(name, getFlattenedShapeList(), shapeMap, "delete");

        DeleteShapeCommand cmd = new DeleteShapeCommand(this, name);
        executeCommand(cmd);
        System.out.println("Deleted " + name);
    }

    /**
     * Handles the 'move' command by validating the shape is not in a group, parsing displacement values,
     * creating a {@link MoveShapeCommand}, and executing it to move the shape.
     *
     * @param parts the split command parts, containing [move, shapeName, dx, dy]
     */
    private void handleMoveCommand(String[] parts) {
        String name = parts[1];

        GroupProtection.validateShapeNotInGroupForOperations(name, getFlattenedShapeList(), shapeMap, "move");

        double dx = Double.parseDouble(parts[2]);
        double dy = Double.parseDouble(parts[3]);

        MoveShapeCommand cmd = new MoveShapeCommand(this, name, dx, dy);
        executeCommand(cmd);
        System.out.println("Moved " + name);
    }

    /**
     * Handles the 'boundingbox' command by validating the shape is not in a group, retrieving the shape,
     * and printing its bounding box coordinates.
     *
     * @param parts the split command parts, containing [boundingbox, shapeName]
     * @throws IllegalArgumentException if the shape does not exist
     */
    private void handleBoundingBoxCommand(String[] parts) {
        String name = parts[1];

        GroupProtection.validateShapeNotInGroupForOperations(name, getFlattenedShapeList(), shapeMap, "boundingbox");

        Shape shape = shapeMap.get(name);
        if(shape == null) {
            throw new IllegalArgumentException("Shape '" + name + "' not found");
        }

        double[] bbox = shape.getBoundingBox();
        System.out.printf("%s %s %s %s%n",
                DoubleFormatter.format(bbox[0]), DoubleFormatter.format(bbox[1]),
                DoubleFormatter.format(bbox[2]), DoubleFormatter.format(bbox[3]));
    }

    /**
     * Handles the 'shapeat' command by checking which shape (if any) covers the specified (x, y) point,
     * prioritizing top-most shapes (last in the nested list structure). Prints the name of the top-most shape.
     *
     * @param parts the split command parts, containing [shapeat, x, y]
     */
    private void handleShapeAtCommand(String[] parts) {
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        Shape topShape = null;

        for (int i = shapeList.size() - 1; i >= 0; i--) {
            List<Shape> innerList = shapeList.get(i);
            for (int j = innerList.size() - 1; j >= 0; j--) {
                Shape shape = innerList.get(j);
                if (shape.coversPoint(x, y)) {
                    topShape = shape;
                    break;
                }
            }
            if (topShape != null) break;
        }

        if (topShape != null) {
            System.out.println(topShape.getName());
        } else {
            System.out.println("No shape found at (" + DoubleFormatter.format(x) + ", " + DoubleFormatter.format(y) + ")");
        }
    }

    /**
     * Handles the 'intersect' command by validating two shapes are not in groups, checking if they intersect,
     * and printing the result.
     *
     * @param parts the split command parts, containing [intersect, shapeName1, shapeName2]
     * @throws IllegalArgumentException if either shape does not exist
     */
    private void handleIntersectCommand(String[] parts) {
        String name1 = parts[1];
        String name2 = parts[2];

        GroupProtection.validateShapeNotInGroupForOperations(name1, getFlattenedShapeList(), shapeMap, "intersect");
        GroupProtection.validateShapeNotInGroupForOperations(name2, getFlattenedShapeList(), shapeMap, "intersect");

        Shape shape1 = shapeMap.get(name1);
        Shape shape2 = shapeMap.get(name2);

        if(shape1 == null) {
            throw new IllegalArgumentException("Shape '" + name1 + "' not found");
        }
        if(shape2 == null) {
            throw new IllegalArgumentException("Shape '" + name2 + "' not found");
        }

        boolean intersects = shape1.intersects(shape2);
        System.out.println(intersects ? "The two shapes intersect" : "The two shapes do not intersect");
    }

    /**
     * Handles the 'list' command by validating the shape is not in a group, retrieving the shape,
     * and printing its string representation.
     *
     * @param parts the split command parts, containing [list, shapeName]
     * @throws IllegalArgumentException if the shape does not exist
     */
    private void handleListCommand(String[] parts) {
        String name = parts[1];

        GroupProtection.validateShapeNotInGroupForOperations(name, getFlattenedShapeList(), shapeMap, "list");

        Shape shape = shapeMap.get(name);
        if(shape == null) {
            throw new IllegalArgumentException("Shape '" + name + "' not found");
        }

        System.out.println(shape.toString());
    }

    /**
     * Handles the 'listall' command by printing all shapes in reverse order of their addition (top-most first),
     * with nested indentation for group components.
     *
     * @param parts the split command parts (unused beyond the 'listall' command)
     */
    private void handleListAllCommand(String[] parts) {
        for (int i = shapeList.size() - 1; i >= 0; i--) {
            List<Shape> innerList = shapeList.get(i);
            for (int j = innerList.size() - 1; j >= 0; j--) {
                printShape(innerList.get(j), 0);
            }
        }
    }

    /**
     * Handles the 'undo' command by popping the most recent command from {@link #undoStack},
     * undoing it, and pushing it to {@link #redoStack}. Prints a message if there is nothing to undo.
     *
     * @param parts the split command parts (unused beyond the 'undo' command)
     */
    private void handleUndoCommand(String[] parts) {
        if(undoStack.isEmpty()) {
            System.out.println("Nothing to undo");
            return;
        }

        UndoableCommand cmd = undoStack.pop();
        cmd.undo();
        redoStack.push(cmd);
        System.out.println("Undo: " + cmd.getName());
    }

    /**
     * Handles the 'redo' command by popping the most recent undone command from {@link #redoStack},
     * redoing it, and pushing it to {@link #undoStack}. Prints a message if there is nothing to redo.
     *
     * @param parts the split command parts (unused beyond the 'redo' command)
     */
    private void handleRedoCommand(String[] parts) {
        if(redoStack.isEmpty()) {
            System.out.println("Nothing to redo");
            return;
        }

        UndoableCommand cmd = redoStack.pop();
        cmd.redo();
        undoStack.push(cmd);
        System.out.println("Redo: " + cmd.getName());
    }

    /**
     * Executes an {@link UndoableCommand}, adds it to {@link #undoStack}, clears {@link #redoStack},
     * and trims {@link #undoStack} if it exceeds {@link #MAX_HISTORY}.
     *
     * @param cmd the command to execute
     */
    private void executeCommand(UndoableCommand cmd) {
        cmd.execute();
        undoStack.push(cmd);
        redoStack.clear();

        if(undoStack.size() > MAX_HISTORY) {
            undoStack.remove(0);
        }
    }

    /**
     * Recursively prints a shape and its components (if it is a group) with indentation
     * to indicate nested levels.
     *
     * @param shape the shape to print
     * @param indentLevel the number of indentation levels (each level is "  ")
     */
    private void printShape(Shape shape, int indentLevel) {
        String indent = "  ".repeat(indentLevel);
        System.out.println(indent + shape.toString());

        if(shape instanceof Group group) {
            for(Shape component : group.getComponents()) {
                printShape(component, indentLevel + 1);
            }
        }
    }

    /**
     * Adds a shape to the managed data structures ({@link #shapeList} and {@link #shapeMap})
     * as part of a command (supports undo/redo). Throws an exception if a shape with the same
     * name already exists.
     *
     * @param shape the shape to be added
     * @throws IllegalArgumentException if a shape with the same name is already present
     */
    protected void addShape(Shape shape) {
        String name = shape.getName();
        if (shapeMap.containsKey(name)) {
            throw new IllegalArgumentException("Shape name '" + name + "' already exists!");
        }

        List<Shape> targetInnerList = getAvailableInnerList();
        targetInnerList.add(shape);
        shapeMap.put(name, shape);
    }

    /**
     * Retrieves an unmodifiable copy of the nested shape list structure to prevent external modification.
     * Each inner list is also unmodifiable.
     *
     * @return an unmodifiable view of {@link #shapeList}
     */
    protected List<List<Shape>> getShapeList() {
        List<List<Shape>> copy = new ArrayList<>();
        for (List<Shape> innerList : shapeList) {
            copy.add(Collections.unmodifiableList(new ArrayList<>(innerList)));
        }
        return Collections.unmodifiableList(copy);
    }

    /**
     * Retrieves a copy of the shape map to prevent external modification of the internal map.
     *
     * @return a new {@link HashMap} containing the same entries as {@link #shapeMap}
     */
    public Map<String, Shape> getShapeMap() {
        return new HashMap<>(shapeMap);
    }

    /**
     * Returns the number of commands in the undo stack.
     *
     * @return the size of {@link #undoStack}
     */
    protected int getUndoStackSize() {
        return undoStack.size();
    }

    /**
     * Returns the number of commands in the redo stack.
     *
     * @return the size of {@link #redoStack}
     */
    protected int getRedoStackSize() {
        return redoStack.size();
    }

    /**
     * Retrieves the logger instance used by this Clevis instance.
     *
     * @return the {@link Logger} instance, or null if not initialized
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Indicates whether the application is currently running.
     *
     * @return true if the application is running; false otherwise
     */
    protected boolean isRunning() {
        return isRunning;
    }
}