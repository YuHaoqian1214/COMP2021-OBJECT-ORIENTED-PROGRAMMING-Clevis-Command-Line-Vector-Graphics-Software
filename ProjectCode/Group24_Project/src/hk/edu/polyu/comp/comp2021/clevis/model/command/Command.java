package hk.edu.polyu.comp.comp2021.clevis.model.command;

/**
 * Enumeration representing all valid commands in the Clevis application.
 * Each command specifies its name and required number of arguments.
 */
public enum Command {
    /** Command to create a rectangle (requires 5 arguments: name, x, y, width, height) */
    RECTANGLE("rectangle", 5),
    /** Command to create a line (requires 5 arguments: name, x1, y1, x2, y2) */
    LINE("line", 5),
    /** Command to create a circle (requires 4 arguments: name, x, y, radius) */
    CIRCLE("circle", 4),
    /** Command to create a square (requires 4 arguments: name, x, y, side length) */
    SQUARE("square", 4),
    /** Command to group shapes (requires at least 2 arguments: group name, component names...) */
    GROUP("group", 2),
    /** Command to ungroup a group (requires 1 argument: group name) */
    UNGROUP("ungroup", 1),
    /** Command to delete a shape (requires 1 argument: shape name) */
    DELETE("delete", 1),
    /** Command to show bounding box of a shape (requires 1 argument: shape name) */
    BOUNDINGBOX("boundingbox", 1),
    /** Command to move a shape (requires 3 arguments: name, dx, dy) */
    MOVE("move", 3),
    /** Command to find shape at coordinates (requires 2 arguments: x, y) */
    SHAPEAT("shapeAt", 2),
    /** Command to check intersection between two shapes (requires 2 arguments: shape names) */
    INTERSECT("intersect", 2),
    /** Command to list details of a shape (requires 1 argument: shape name) */
    LIST("list", 1),
    /** Command to list all shapes (requires 0 arguments) */
    LISTALL("listAll", 0),
    /** Command to undo last operation (requires 0 arguments) */
    UNDO("undo", 0),
    /** Command to redo last undone operation (requires 0 arguments) */
    REDO("redo", 0),
    /** Command to exit the application (requires 0 arguments) */
    QUIT("quit", 0);

    private final String commandName;  // The string identifier of the command
    private final int Args;            // The required number of arguments for the command

    /**
     * Constructs a Command with specified name and argument count.
     * @param commandName The string identifier of the command
     * @param Args The number of required arguments
     */
    private Command(String commandName, int Args) {
        this.commandName = commandName;
        this.Args = Args;
    }

    /**
     * Gets the command's string identifier.
     * @return The command name
     */
    public String getCommandName() {
        return this.commandName;
    }

    /**
     * Gets the required number of arguments for the command.
     * @return The argument count
     */
    public int getArgs() {
        return this.Args;
    }

    /**
     * Retrieves the Command enum constant matching a given string (case-insensitive).
     * @param CommandStr The string to match
     * @return The corresponding Command, or null if no match
     */
    public static Command fromString(String CommandStr) {
        for(Command cmd : values()) {
            if(cmd.getCommandName().equalsIgnoreCase(CommandStr)) {
                return cmd;
            }
        }
        return null;
    }

    /**
     * Checks if the provided argument count is valid for this command.
     * For GROUP, allows argument count >= required; others require exact match.
     * @param argCount The number of arguments to check
     * @return True if valid, false otherwise
     */
    public boolean checkArgs(int argCount) {
        if(this == GROUP) {
            return argCount >= Args;
        }
        else {
            return argCount == Args;
        }
    }
}