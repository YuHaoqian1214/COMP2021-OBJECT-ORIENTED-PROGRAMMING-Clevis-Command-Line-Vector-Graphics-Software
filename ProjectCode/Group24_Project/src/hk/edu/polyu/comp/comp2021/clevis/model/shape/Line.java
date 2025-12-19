package hk.edu.polyu.comp.comp2021.clevis.model.shape;

import hk.edu.polyu.comp.comp2021.clevis.model.util.DoubleFormatter;

/**
 * Represents a line segment shape with two endpoints.
 * Extends the abstract Shape class and implements its required methods.
 */
public class Line extends Shape{
    private double x1;  // X-coordinate of the first endpoint
    private double y1;  // Y-coordinate of the first endpoint
    private double x2;  // X-coordinate of the second endpoint
    private double y2;  // Y-coordinate of the second endpoint

    /**
     * Constructs a Line with specified name and endpoints.
     * @param name The unique name of the line
     * @param x1 X-coordinate of the first endpoint
     * @param y1 Y-coordinate of the first endpoint
     * @param x2 X-coordinate of the second endpoint
     * @param y2 Y-coordinate of the second endpoint
     */
    public Line(String name, double x1, double y1, double x2, double y2) {
        super(name);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /**
     * Gets the X-coordinate of the first endpoint.
     * @return x1 value
     */
    public double getX1() {
        return this.x1;
    }

    /**
     * Gets the Y-coordinate of the first endpoint.
     * @return y1 value
     */
    public double getY1() {
        return this.y1;
    }

    /**
     * Gets the X-coordinate of the second endpoint.
     * @return x2 value
     */
    public double getX2() {
        return this.x2;
    }

    /**
     * Gets the Y-coordinate of the second endpoint.
     * @return y2 value
     */
    public double getY2() {
        return this.y2;
    }

    /**
     * Calculates the bounding box of the line segment.
     * The bounding box is the smallest rectangle that contains the line.
     * @return A double array with [minX, minY, width, height]
     */
    @Override
    public double[] getBoundingBox() {
        double minX = Math.min(getX1(), getX2());
        double minY = Math.min(getY1(), getY2());
        double maxX = Math.max(getX1(), getX2());
        double maxY = Math.max(getY1(), getY2());

        return new double[]{minX, minY, maxX - minX, maxY - minY};
    }

    /**
     * Moves the line by specified delta values.
     * Both endpoints are translated by (dx, dy).
     * @param dx The amount to move in the X-direction
     * @param dy The amount to move in the Y-direction
     */
    @Override
    public void move(double dx, double dy) {
        this.x1 += dx;
        this.y1 += dy;
        this.x2 += dx;
        this.y2 += dy;
    }

    /**
     * Checks if a point lies on or very close to the line segment.
     * Uses a tolerance of 0.05 to account for floating-point precision.
     * @param px X-coordinate of the point to check
     * @param py Y-coordinate of the point to check
     * @return True if the point is on the segment (within tolerance)
     */
    @Override
    public boolean coversPoint(double px, double py) {
        return distanceToLineSegment(px, py, getX1(), getY1(), getX2(), getY2()) < 0.05;
    }

    /**
     * Returns the type of the shape.
     * @return "Line" as the type identifier
     */
    @Override
    public String getType() {
        return "Line";
    }

    /**
     * Returns a string representation of the line.
     * @return Formatted string with name and endpoints
     */
    @Override
    public String toString() {
        return String.format("Line %s from (%s,%s) to (%s,%s)",
                getName(),
                DoubleFormatter.format(getX1()),
                DoubleFormatter.format(getY1()),
                DoubleFormatter.format(getX2()),
                DoubleFormatter.format(getY2()));
    }
}