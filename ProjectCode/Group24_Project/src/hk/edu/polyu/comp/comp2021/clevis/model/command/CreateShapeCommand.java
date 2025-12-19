package hk.edu.polyu.comp.comp2021.clevis.model.command;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;

/**
 * Command implementation for creating and adding a shape to the Clevis instance.
 * Extends AbstractCommand and supports undo/redo functionality.
 */
public class CreateShapeCommand extends AbstractCommand {
    private final Shape shape;       // The shape to be created
    private boolean executed = false; // Flag to track if the command has been executed

    /**
     * Constructs a CreateShapeCommand with target Clevis instance and the shape to create.
     * @param clevis The Clevis instance to add the shape to
     * @param shape The shape to create and add
     */
    public CreateShapeCommand(Clevis clevis, Shape shape) {
        super(clevis, "Create " + shape.getType() + " " + shape.getName());
        this.shape = shape;
    }

    /**
     * Executes the create command: adds the shape to Clevis if not already executed.
     */
    @Override
    public void execute() {
        if (!executed) {
            clevis.addShapeDirectly(shape);
            executed = true;
        }
    }

    /**
     * Undoes the create command: removes the shape from Clevis if it was executed.
     */
    @Override
    public void undo() {
        if (executed) {
            clevis.removeShapeDirectly(shape.getName());
            executed = false;
        }
    }

    /**
     * Redoes the create command: re-adds the shape to Clevis if not executed.
     */
    @Override
    public void redo() {
        if (!executed) {
            clevis.addShapeDirectly(shape);
            executed = true;
        }
    }
}