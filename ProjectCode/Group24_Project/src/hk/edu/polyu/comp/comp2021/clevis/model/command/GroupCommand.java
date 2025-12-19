package hk.edu.polyu.comp.comp2021.clevis.model.command;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Command implementation for grouping multiple shapes into a single Group.
 * Extends AbstractCommand and implements undo/redo functionality.
 */
public class GroupCommand extends AbstractCommand {
    private final String groupName;         // Name of the group to be created
    private final List<String> componentNames;  // Names of shapes to include in the group
    private Group createdGroup;             // The Group instance created by this command
    private List<Shape> originalComponents; // Original shapes to restore during undo

    /**
     * Constructs a GroupCommand with target Clevis instance, group name, and component names.
     * @param clevis The Clevis instance managing the shapes
     * @param groupName The name for the new group
     * @param componentNames List of names of shapes to group
     */
    public GroupCommand(Clevis clevis, String groupName, List<String> componentNames) {
        super(clevis, "Group " + groupName);
        this.groupName = groupName;
        this.componentNames = new ArrayList<>(componentNames); // Defensive copy
    }

    /**
     * Executes the group command: collects components, removes them from the main list,
     * creates a new Group, and adds it to the Clevis instance.
     * @throws IllegalArgumentException If any component shape is not found
     */
    @Override
    public void execute() {
        originalComponents = new ArrayList<>();

        // Collect all component shapes and validate existence
        for (String name : componentNames) {
            Shape shape = clevis.getShapeMap().get(name);
            if (shape == null) {
                throw new IllegalArgumentException("Shape '" + name + "' not found for grouping");
            }
            originalComponents.add(shape);
        }

        // Remove components from main storage
        for (Shape component : originalComponents) {
            clevis.removeShapeFromNestedList(component);
            clevis.getShapeMap().remove(component.getName());
        }

        // Create and add the new group
        createdGroup = new Group(groupName, originalComponents);
        clevis.addShapeDirectly(createdGroup);
    }

    /**
     * Undoes the group command: removes the created group and restores original components.
     */
    @Override
    public void undo() {
        if (createdGroup == null || originalComponents.isEmpty()) return;

        // Remove the group from Clevis
        clevis.removeShapeDirectly(groupName);

        // Restore original components
        for (Shape component : originalComponents) {
            clevis.addShapeDirectly(component);
        }
    }

    /**
     * Redoes the group command by re-executing the original operation.
     */
    @Override
    public void redo() {
        execute();
    }
}