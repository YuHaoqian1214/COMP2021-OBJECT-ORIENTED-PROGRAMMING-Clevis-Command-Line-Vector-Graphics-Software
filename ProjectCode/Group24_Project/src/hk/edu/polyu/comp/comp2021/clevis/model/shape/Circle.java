package hk.edu.polyu.comp.comp2021.clevis.model.shape;

import hk.edu.polyu.comp.comp2021.clevis.model.util.DoubleFormatter;

/**
 * Represents a circle shape defined by a center point and radius.
 * Extends the abstract Shape class and implements its required methods.
 */
public class Circle extends Shape{
    private double x;          // X-coordinate of the circle's center
    private double y;          // Y-coordinate of the circle's center
    private final double radius;  // Radius of the circle (must be positive)

    /**
     * Constructs a Circle with specified name, center coordinates, and radius.
     * @param name The unique name of the circle
     * @param x X-coordinate of the center
     * @param y Y-coordinate of the center
     * @param radius Radius of the circle (must be > 0)
     * @throws IllegalArgumentException If radius is <= 0
     */
    public Circle(String name, double x, double y, double radius) {
        super(name);
        if (radius <= 0) {
            throw new IllegalArgumentException("The radius must be greater than 0");
        }
        else {
            this.x = x;
            this.y = y;
            this.radius = radius;
        }
    }

    /**
     * Gets the X-coordinate of the circle's center.
     * @return x value
     */
    public double getX() {
        return this.x;
    }

    /**
     * Gets the Y-coordinate of the circle's center.
     * @return y value
     */
    public double getY() {
        return this.y;
    }

    /**
     * Gets the radius of the circle.
     * @return radius value
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Calculates the bounding box of the circle.
     * The bounding box is a square with side 2*radius, centered on the circle's center.
     * @return A double array with [minX, minY, width, height]
     */
    @Override
    public double[] getBoundingBox() {
        return new double[] {getX() - getRadius(), getY() - getRadius(), 2 * getRadius(), 2 * getRadius()};
    }

    /**
     * Moves the circle by translating its center by (dx, dy).
     * @param dx The amount to move in the X-direction
     * @param dy The amount to move in the Y-direction
     */
    @Override
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * Checks if a point lies on or very close to the circle's perimeter.
     * Uses a tolerance of 0.05 to account for floating-point precision.
     * @param px X-coordinate of the point to check
     * @param py Y-coordinate of the point to check
     * @return True if the point is on the perimeter (within tolerance)
     */
    @Override
    public boolean coversPoint(double px, double py) {
        double distanceToCenter = Math.sqrt((px - getX()) * (px - getX()) + (py - getY()) * (py - getY()));
        return Math.abs(distanceToCenter - getRadius()) < 0.05;
    }

    /**
     * Returns the type of the shape.
     * @return "Circle" as the type identifier
     */
    @Override
    public String getType() {
        return "Circle";
    }

    /**
     * Returns a string representation of the circle.
     * @return Formatted string with name, center coordinates, and radius
     */
    @Override
    public String toString() {
        return String.format("Circle %s at (%s,%s) with radius %s",
                getName(),
                DoubleFormatter.format(getX()),
                DoubleFormatter.format(getY()),
                DoubleFormatter.format(getRadius()));
    }
}