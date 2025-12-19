package hk.edu.polyu.comp.comp2021.clevis.model.shape;

/**
 * Abstract base class for all geometric shapes in the Clevis application.
 * Defines core methods for shape manipulation, collision detection, and metadata.
 */
public abstract class Shape {
    private final String name;  // Unique identifier for the shape

    /**
     * Constructs a Shape with a specified unique name.
     * @param name The unique name of the shape
     */
    public Shape(String name) {
        this.name = name;
    }

    /**
     * Gets the unique name of the shape.
     * @return The shape's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Calculates the bounding box of the shape.
     * @return A double array with [minX, minY, width, height]
     */
    public abstract double[] getBoundingBox();

    /**
     * Moves the shape by specified delta values.
     * @param dx The amount to move in the X-direction
     * @param dy The amount to move in the Y-direction
     */
    public abstract void move(double dx, double dy);

    /**
     * Checks if a point lies on or within the shape (with tolerance for precision).
     * @param px X-coordinate of the point to check
     * @param py Y-coordinate of the point to check
     * @return True if the point is covered by the shape
     */
    public abstract boolean coversPoint(double px, double py);

    /**
     * Returns the type of the shape (e.g., "Line", "Circle").
     * @return The shape's type as a string
     */
    public abstract String getType();

    /**
     * Checks if this shape's bounding box intersects with another shape's bounding box.
     * @param other The other shape to check for intersection
     * @return True if bounding boxes intersect, false otherwise
     */
    public boolean intersects(Shape other) {
        double[] thisBox = this.getBoundingBox();
        double[] otherBox = other.getBoundingBox();

        double x1 = thisBox[0], y1 = thisBox[1], w1 = thisBox[2], h1 = thisBox[3];
        double x2 = otherBox[0], y2 = otherBox[1], w2 = otherBox[2], h2 = otherBox[3];

        // Check for overlap on both X and Y axes
        boolean checkXAxis = (x1 + w1 >= x2) && (x2 + w2 >= x1);
        boolean checkYAxis = (y1 + h1 >= y2) && (y2 + h2 >= y1);

        return checkXAxis && checkYAxis;
    }

    /**
     * Calculates the shortest distance from a point to a line segment.
     * @param px X-coordinate of the point
     * @param py Y-coordinate of the point
     * @param x1 X-coordinate of the segment's first endpoint
     * @param y1 Y-coordinate of the segment's first endpoint
     * @param x2 X-coordinate of the segment's second endpoint
     * @param y2 Y-coordinate of the segment's second endpoint
     * @return The distance from the point to the segment
     */
    protected double distanceToLineSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double lineLengthSq = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);

        // Handle degenerate segment (points are the same)
        if (lineLengthSq == 0) {
            return Math.sqrt((px - x1) * (px - x1) + (py - y1) * (py - y1));
        }

        // Calculate projection parameter t
        double t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / lineLengthSq;
        t = Math.max(0, Math.min(1, t)); // Clamp t to [0, 1]

        // Calculate projection point on the segment
        double projectionX = x1 + t * (x2 - x1);
        double projectionY = y1 + t * (y2 - y1);

        // Return distance from point to projection
        return Math.sqrt((px - projectionX) * (px - projectionX) + (py - projectionY) * (py - projectionY));
    }

    /**
     * Returns a string representation of the shape.
     * @return Formatted string with shape details
     */
    @Override
    public abstract String toString();
}