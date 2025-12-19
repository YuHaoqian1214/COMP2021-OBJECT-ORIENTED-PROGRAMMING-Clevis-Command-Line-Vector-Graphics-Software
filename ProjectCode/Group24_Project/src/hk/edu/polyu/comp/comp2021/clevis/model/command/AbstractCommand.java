package hk.edu.polyu.comp.comp2021.clevis.model.command;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis; /**
 * Abstract base class implementing UndoableCommand.
 * Provides common functionality for commands, including a reference to the Clevis instance and a command name.
 */
public abstract class AbstractCommand implements UndoableCommand {
    protected final Clevis clevis;  // The Clevis instance this command operates on
    protected final String name;    // The name/description of the command

    /**
     * Constructs an AbstractCommand with a target Clevis instance and command name.
     * @param clevis The Clevis instance to operate on
     * @param name The name of the command
     */
    public AbstractCommand(Clevis clevis, String name) {
        this.clevis = clevis;
        this.name = name;
    }

    /**
     * Returns the command's name.
     * @return The command name
     */
    @Override
    public String getName() {
        return name;
    }
}