package hk.edu.polyu.comp.comp2021.clevis.model.command;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Command implementation for deleting a shape (including groups) from the Clevis instance.
 * Extends AbstractCommand and supports undo/redo functionality.
 */
public class DeleteShapeCommand extends AbstractCommand {
    private final String shapeName;       // Name of the shape to delete
    private Shape deletedShape;           // The shape that was deleted (for undo)
    private List<Shape> deletedComponents; // Components of a deleted group (for undo)

    /**
     * Constructs a DeleteShapeCommand with target Clevis instance and shape name.
     * @param clevis The Clevis instance managing the shape
     * @param shapeName The name of the shape to delete
     */
    public DeleteShapeCommand(Clevis clevis, String shapeName) {
        super(clevis, "Delete " + shapeName);
        this.shapeName = shapeName;
    }

    /**
     * Executes the delete command: removes the shape from Clevis and stores it (and its components if a group) for undo.
     */
    @Override
    public void execute() {
        Shape shape = clevis.getShapeMap().get(shapeName);
        if (shape != null) {
            deletedShape = shape;
            // If deleting a group, store its components
            if (shape instanceof Group) {
                deletedComponents = new ArrayList<>(((Group) shape).getComponents());
            }
            clevis.removeShapeDirectly(shapeName);
        }
    }

    /**
     * Undoes the delete command: restores the deleted shape (and its components if a group).
     */
    @Override
    public void undo() {
        if (deletedShape != null) {
            clevis.addShapeDirectly(deletedShape);
        }
    }

    /**
     * Redoes the delete command by re-executing the original deletion.
     */
    @Override
    public void redo() {
        execute();
    }
}