package hk.edu.polyu.comp.comp2021.clevis.model.command;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Command implementation for ungrouping a Group into its individual components.
 * Extends AbstractCommand and supports undo/redo functionality.
 */
public class UngroupCommand extends AbstractCommand {
    private final String groupName;         // Name of the group to ungroup
    private Group ungroupedGroup;           // The Group instance being ungrouped
    private List<Shape> originalComponents; // Components of the group to restore during undo

    /**
     * Constructs an UngroupCommand with target Clevis instance and group name.
     * @param clevis The Clevis instance managing the group
     * @param groupName The name of the group to ungroup
     */
    public UngroupCommand(Clevis clevis, String groupName) {
        super(clevis, "Ungroup " + groupName);
        this.groupName = groupName;
    }

    /**
     * Executes the ungroup command: verifies the shape is a group, removes the group,
     * and adds its components back to the main shape list.
     * @throws IllegalArgumentException If the shape is not a Group
     */
    @Override
    public void execute() {
        Shape shape = clevis.getShapeMap().get(groupName);
        if (!(shape instanceof Group)) {
            throw new IllegalArgumentException("Shape '" + groupName + "' is not a Group");
        }

        ungroupedGroup = (Group) shape;
        originalComponents = new ArrayList<>(ungroupedGroup.getComponents());

        // Remove the group from Clevis
        clevis.removeShapeDirectly(groupName);

        // Add components back as individual shapes
        for (Shape component : originalComponents) {
            clevis.addShapeDirectly(component);
        }
    }

    /**
     * Undoes the ungroup command: removes the components and restores the original group.
     */
    @Override
    public void undo() {
        if (ungroupedGroup == null || originalComponents.isEmpty()) return;

        // Remove components from main storage
        for (Shape component : originalComponents) {
            clevis.removeShapeFromNestedList(component);
            clevis.getShapeMap().remove(component.getName());
        }

        // Restore the original group
        clevis.addShapeDirectly(ungroupedGroup);
    }

    /**
     * Redoes the ungroup command by re-executing the original operation.
     */
    @Override
    public void redo() {
        execute();
    }
}