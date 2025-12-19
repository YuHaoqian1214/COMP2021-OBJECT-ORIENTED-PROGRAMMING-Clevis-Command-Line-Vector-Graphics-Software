package hk.edu.polyu.comp.comp2021.clevis.model.shape;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of shapes that can be treated as a single entity.
 * Extends the abstract Shape class and contains a list of component shapes.
 */
public class Group extends Shape {
    private final List<Shape> components;  // List of shapes contained in this group

    /**
     * Constructs a Group with a specified name and list of components.
     * @param name The unique name of the group
     * @param components The list of shapes to include in the group
     */
    public Group(String name, List<Shape> components) {
        super(name);
        this.components = new ArrayList<>(components); // Defensive copy
    }

    /**
     * Gets a copy of the group's components to prevent external modification.
     * @return A new list containing the group's components
     */
    public List<Shape> getComponents() {
        return new ArrayList<>(components);
    }

    /**
     * Calculates the bounding box of the group, which encloses all its components.
     * @return A double array with [minX, minY, width, height]
     */
    @Override
    public double[] getBoundingBox() {
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        // Find min/max coordinates across all components' bounding boxes
        for (Shape shape : getComponents()) {
            double[] innerBox = shape.getBoundingBox();
            minX = Math.min(minX, innerBox[0]);
            minY = Math.min(minY, innerBox[1]);
            maxX = Math.max(maxX, innerBox[0] + innerBox[2]);
            maxY = Math.max(maxY, innerBox[1] + innerBox[3]);
        }

        double width = maxX - minX;
        double height = maxY - minY;

        return new double[]{minX, minY, width, height};
    }

    /**
     * Moves the entire group by translating all its components by (dx, dy).
     * @param dx The amount to move in the X-direction
     * @param dy The amount to move in the Y-direction
     */
    @Override
    public void move(double dx, double dy) {
        for (Shape shape : getComponents()) {
            shape.move(dx, dy);
        }
    }

    /**
     * Checks if a point is covered by any component in the group.
     * @param px X-coordinate of the point to check
     * @param py Y-coordinate of the point to check
     * @return True if any component covers the point, false otherwise
     */
    @Override
    public boolean coversPoint(double px, double py) {
        for (Shape shape : getComponents()) {
            if (shape.coversPoint(px, py)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the type of the shape.
     * @return "Group" as the type identifier
     */
    @Override
    public String getType() {
        return "Group";
    }

    /**
     * Returns a string representation of the group, including its components.
     * @return Formatted string with group name and component names
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Group ").append(getName()).append(" containing: [");
        for (int i = 0; i < getComponents().size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(getComponents().get(i).getName());
        }
        sb.append("]");
        return sb.toString();
    }
}