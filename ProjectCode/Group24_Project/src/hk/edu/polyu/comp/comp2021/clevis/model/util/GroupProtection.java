package hk.edu.polyu.comp.comp2021.clevis.model.util;

import hk.edu.polyu.comp.comp2021.clevis.model.shape.Group;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;
import java.util.List;
import java.util.Map;

/**
 * Utility class providing methods to protect grouped shapes from direct manipulation.
 * Ensures shapes within groups are only modified through group operations.
 */
public final class GroupProtection {
    /**
     * Private constructor to prevent instantiation of this utility class.
     * @throws AssertionError Always thrown if attempted
     */
    private GroupProtection() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Checks if a shape is contained within any group (including nested groups).
     * @param shapeName The name of the shape to check
     * @param shapeList The list of all top-level shapes
     * @param shapeMap The map of top-level shapes by name
     * @return True if the shape is in any group, false otherwise
     */
    public static boolean isShapeInGroup(String shapeName, List<Shape> shapeList, Map<String, Shape> shapeMap) {
        // If shape is not in top-level map, it must be in a group
        if (!shapeMap.containsKey(shapeName)) {
            return true;
        }

        // Check all top-level groups for containment
        for (Shape shape : shapeList) {
            if (shape instanceof Group group) {
                if (isShapeInGroupRecursive(group, shapeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Recursively checks if a shape is contained within a group or its nested groups.
     * @param group The group to check
     * @param shapeName The name of the shape to find
     * @return True if the shape is found in the group hierarchy, false otherwise
     */
    private static boolean isShapeInGroupRecursive(Group group, String shapeName) {
        for (Shape component : group.getComponents()) {
            if (component.getName().equals(shapeName)) {
                return true;
            }
            // Recursively check nested groups
            if (component instanceof Group nestedGroup) {
                if (isShapeInGroupRecursive(nestedGroup, shapeName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Validates that a shape is not in any group before performing sensitive operations.
     * Throws an exception if the shape is in a group.
     * @param shapeName The name of the shape to validate
     * @param shapeList The list of all top-level shapes
     * @param shapeMap The map of top-level shapes by name
     * @param operationName The name of the operation being performed (e.g., "delete")
     * @throws IllegalArgumentException If the shape is contained in a group
     */
    public static void validateShapeNotInGroupForOperations(String shapeName, List<Shape> shapeList,
                                                            Map<String, Shape> shapeMap, String operationName) {
        if (isShapeInGroup(shapeName, shapeList, shapeMap)) {
            String containingGroup = getContainingGroupName(shapeName, shapeList, shapeMap);
            throw new IllegalArgumentException("Shape '" + shapeName + "' is in group '" +
                    containingGroup + "' and cannot be used directly in " +
                    operationName + " operation. Use the group name instead.");
        }
    }

    /**
     * Finds the name of the group containing the specified shape (including nested groups).
     * @param shapeName The name of the shape to find
     * @param shapeList The list of all top-level shapes
     * @param shapeMap The map of top-level shapes by name
     * @return The name of the containing group, or "a group" if not found
     */
    public static String getContainingGroupName(String shapeName, List<Shape> shapeList, Map<String, Shape> shapeMap) {
        for (Shape shape : shapeList) {
            if (shape instanceof Group group) {
                String groupName = findContainingGroupNameRecursive(group, shapeName);
                if (groupName != null) {
                    return groupName;
                }
            }
        }
        return "a group";
    }

    /**
     * Recursively finds the name of the group containing the specified shape.
     * @param group The group to search
     * @param shapeName The name of the shape to find
     * @return The name of the containing group, or null if not found
     */
    private static String findContainingGroupNameRecursive(Group group, String shapeName) {
        for (Shape component : group.getComponents()) {
            if (component.getName().equals(shapeName)) {
                return group.getName();
            }
            // Recursively check nested groups
            if (component instanceof Group nestedGroup) {
                String nestedGroupName = findContainingGroupNameRecursive(nestedGroup, shapeName);
                if (nestedGroupName != null) {
                    return nestedGroupName;
                }
            }
        }
        return null;
    }
}