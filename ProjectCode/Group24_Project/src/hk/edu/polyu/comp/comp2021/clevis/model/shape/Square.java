package hk.edu.polyu.comp.comp2021.clevis.model.shape;

import hk.edu.polyu.comp.comp2021.clevis.model.util.DoubleFormatter;

/**
 * Represents a square shape, a special case of Rectangle with equal width and height.
 * Extends Rectangle and overrides methods specific to square properties.
 */
public class Square extends Rectangle{
    private final double side;  // Length of the square's side

    /**
     * Constructs a Square with specified name, top-left corner coordinates, and side length.
     * @param name The unique name of the square
     * @param x X-coordinate of the top-left corner
     * @param y Y-coordinate of the top-left corner
     * @param l Length of the side
     */
    public Square(String name, double x, double y, double l) {
        super(name,x,y,l,l);  // Call Rectangle constructor with equal width and height
        this.side = l;
    }

    /**
     * Gets the length of the square's side.
     * @return The side length
     */
    public double getSide() {
        return this.side;
    }

    /**
     * Returns the type of the shape.
     * @return "Square" as the type identifier
     */
    @Override
    public String getType() {
        return "Square";
    }

    /**
     * Returns a string representation of the square.
     * @return Formatted string with name, coordinates, and side length
     */
    @Override
    public String toString() {
        return String.format("Square %s at (%s,%s) with side %s",
                getName(),
                DoubleFormatter.format(getX()),
                DoubleFormatter.format(getY()),
                DoubleFormatter.format(getSide()));
    }
}