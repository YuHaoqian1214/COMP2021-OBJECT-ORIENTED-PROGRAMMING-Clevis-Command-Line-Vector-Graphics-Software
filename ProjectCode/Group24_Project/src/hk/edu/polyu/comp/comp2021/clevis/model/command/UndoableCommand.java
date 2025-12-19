package hk.edu.polyu.comp.comp2021.clevis.model.command;

/**
 * Interface representing a command that can be undone and redone.
 * Defines the core methods for command execution, undo, redo, and identification.
 */
public interface UndoableCommand {
    /**
     * Executes the command.
     */
    void execute();

    /**
     * Undoes the effects of the command.
     */
    void undo();

    /**
     * Redoes the command after it has been undone.
     */
    void redo();

    /**
     * Gets the name or description of the command.
     * @return The command's name
     */
    String getName();
}