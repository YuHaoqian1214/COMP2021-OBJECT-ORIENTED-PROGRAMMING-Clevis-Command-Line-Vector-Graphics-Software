package hk.edu.polyu.comp.comp2021.clevis.model.command;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;

/**
 * Command implementation for moving a shape by specified delta values.
 * Extends AbstractCommand and supports undo/redo functionality.
 */
public class MoveShapeCommand extends AbstractCommand {
    private final String shapeName;  // Name of the shape to move
    private final double dx;         // Amount to move in the X-direction
    private final double dy;         // Amount to move in the Y-direction

    /**
     * Constructs a MoveShapeCommand with target Clevis instance, shape name, and delta values.
     * @param clevis The Clevis instance managing the shape
     * @param shapeName The name of the shape to move
     * @param dx X-direction movement amount
     * @param dy Y-direction movement amount
     */
    public MoveShapeCommand(Clevis clevis, String shapeName, double dx, double dy) {
        super(clevis, "Move " + shapeName);
        this.shapeName = shapeName;
        this.dx = dx;
        this.dy = dy;
    }

    /**
     * Executes the move command: moves the shape by (dx, dy).
     * Does nothing if the shape does not exist.
     */
    @Override
    public void execute() {
        Shape shape = clevis.getShapeMap().get(shapeName);
        if (shape != null) {
            shape.move(dx, dy);
        }
    }

    /**
     * Undoes the move command: moves the shape back by (-dx, -dy).
     * Does nothing if the shape does not exist.
     */
    @Override
    public void undo() {
        Shape shape = clevis.getShapeMap().get(shapeName);
        if (shape != null) {
            shape.move(-dx, -dy);
        }
    }

    /**
     * Redoes the move command by re-executing the original movement.
     */
    @Override
    public void redo() {
        execute();
    }
}