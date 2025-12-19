package hk.edu.polyu.comp.comp2021.clevis.model.shape;

import hk.edu.polyu.comp.comp2021.clevis.model.util.DoubleFormatter;

/**
 * Represents a rectangle shape, extending the {@link Shape} class.
 * A rectangle is defined by its top-left corner coordinates (x, y), width, and height.
 * The width and height must be positive values.
 */
public class Rectangle extends Shape {
    // The x-coordinate of the top-left corner of the rectangle
    private double x;
    // The y-coordinate of the top-left corner of the rectangle
    private double y;
    // The width of the rectangle (immutable once initialized)
    private final double width;
    // The height of the rectangle (immutable once initialized)
    private final double height;

    /**
     * Constructs a {@code Rectangle} with the specified name, coordinates, width, and height.
     *
     * @param name   the name of the rectangle
     * @param x      the x-coordinate of the top-left corner
     * @param y      the y-coordinate of the top-left corner
     * @param width  the width of the rectangle (must be greater than 0)
     * @param height the height of the rectangle (must be greater than 0)
     * @throws IllegalArgumentException if width or height is less than or equal to 0
     */
    public Rectangle(String name, double x, double y, double width, double height) {
        super(name);
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("The width and height must be greater than 0");
        }
        else {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    /**
     * Returns the x-coordinate of the top-left corner of the rectangle.
     *
     * @return the x-coordinate
     */
    public double getX() {
        return this.x;
    }

    /**
     * Returns the y-coordinate of the top-left corner of the rectangle.
     *
     * @return the y-coordinate
     */
    public double getY() {
        return this.y;
    }

    /**
     * Returns the width of the rectangle.
     *
     * @return the width (positive value)
     */
    public double getWidth() {
        return this.width;
    }

    /**
     * Returns the height of the rectangle.
     *
     * @return the height (positive value)
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * {@inheritDoc}
     * Returns the bounding box of the rectangle as a double array.
     * The array follows the format: [x, y, width, height].
     *
     * @return a double array representing the bounding box
     */
    @Override
    public double[] getBoundingBox() {
        return new double[]{getX(), getY(), getWidth(), getHeight()};
    }

    /**
     * {@inheritDoc}
     * Moves the rectangle by the specified delta values.
     * The top-left corner (x, y) is updated by adding dx and dy respectively.
     *
     * @param dx the distance to move along the x-axis
     * @param dy the distance to move along the y-axis
     */
    @Override
    public void move(double dx, double dy) {
        this.x += dx;
        this.y += dy;
    }

    /**
     * {@inheritDoc}
     * Determines if the specified point (px, py) is considered "covered" by the rectangle.
     * A point is covered if its minimum distance to any of the rectangle's four edges is less than 0.05.
     *
     * @param px the x-coordinate of the point to check
     * @param py the y-coordinate of the point to check
     * @return true if the point is covered by the rectangle, false otherwise
     */
    @Override
    public boolean coversPoint(double px, double py) {
        double minDistance = Double.MAX_VALUE;

        minDistance = Math.min(minDistance, distanceToLineSegment(px, py, getX(), getY(), getX(), getY() + getHeight()));
        minDistance = Math.min(minDistance, distanceToLineSegment(px, py, getX() + getWidth(), getY(), getX() + getWidth(), getY() + getHeight()));
        minDistance = Math.min(minDistance, distanceToLineSegment(px, py, getX(), getY(), getX() + getWidth(), getY()));
        minDistance = Math.min(minDistance, distanceToLineSegment(px, py, getX(), getY() + getHeight(), getX() + getWidth(), getY() + getHeight()));

        return minDistance < 0.05;
    }

    /**
     * {@inheritDoc}
     * Returns the type of the shape, which is "Rectangle".
     *
     * @return the string "Rectangle"
     */
    @Override
    public String getType() {
        return "Rectangle";
    }

    /**
     * Returns a string representation of the rectangle, including its name,
     * coordinates, width, and height (formatted using {@link DoubleFormatter}).
     *
     * @return a formatted string describing the rectangle
     */
    @Override
    public String toString() {
        return String.format("Rectangle %s at (%s,%s) with width %s height %s",
                getName(),
                DoubleFormatter.format(getX()),
                DoubleFormatter.format(getY()),
                DoubleFormatter.format(getWidth()),
                DoubleFormatter.format(getHeight()));
    }
}