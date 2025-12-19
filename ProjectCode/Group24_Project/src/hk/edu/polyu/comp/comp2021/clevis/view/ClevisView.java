package hk.edu.polyu.comp.comp2021.clevis.view;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.*;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Rectangle;
import hk.edu.polyu.comp.comp2021.clevis.model.shape.Shape;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * View component of the Clevis application in MVC pattern.
 * Handles the UI display, input fields, and rendering of shapes.
 */
public class ClevisView extends JFrame {
    protected final JTextField commandField;
    protected final JButton executeButton;
    protected final DrawingPanel drawingPanel;
    protected final JTextArea historyArea;
    private double scale = 1.0;
    private double transX = 0;
    private double transY = 0;
    private final Clevis model;

    /**
     * Constructs the ClevisView.
     *
     * @param model The Clevis model instance.
     */
    public ClevisView(Clevis model) {
        this.model = model;

        setTitle("Clevis GUI");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(800, 600));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        historyArea.setBackground(new Color(245, 245, 245));

        JScrollPane historyScroll = new JScrollPane(historyArea);
        historyScroll.setPreferredSize(new Dimension(300, 600));
        historyScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.GRAY),
                "Command History",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12)
        ));

        JScrollPane drawingScroll = new JScrollPane(drawingPanel);
        drawingScroll.setPreferredSize(new Dimension(800, 600));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                drawingScroll,
                historyScroll
        );
        splitPane.setResizeWeight(0.7);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        commandField = new JTextField(40);
        commandField.setFont(new Font("Monospaced", Font.PLAIN, 14));

        executeButton = new JButton("Execute");

        JButton zoomIn = new JButton("Zoom In");
        JButton zoomOut = new JButton("Zoom Out");

        zoomIn.addActionListener(e -> {
            scale *= 1.2;
            updateView();
        });

        zoomOut.addActionListener(e -> {
            scale /= 1.2;
            updateView();
        });

        inputPanel.add(new JLabel("Command:"));
        inputPanel.add(commandField);
        inputPanel.add(executeButton);
        inputPanel.add(zoomIn);
        inputPanel.add(zoomOut);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        inputPanel.setBackground(new Color(230, 230, 230));

        add(mainPanel, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        appendHistory("=== Command History ===\n\n");

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
     * Sets the action listener for the execute button and command field enter.
     *
     * @param listener The action listener.
     */
    public void setExecuteListener(ActionListener listener) {
        executeButton.addActionListener(listener);
        commandField.addActionListener(listener);
    }

    /**
     * Gets the current command from the text field.
     *
     * @return The command string.
     */
    public String getCommand() {
        return commandField.getText().trim();
    }

    /**
     * Clears the command text field.
     */
    public void clearCommand() {
        commandField.setText("");
    }

    /**
     * Appends text to the history area and scrolls to the bottom.
     *
     * @param text The text to append.
     */
    public void appendHistory(String text) {
        historyArea.append(text);
        historyArea.setCaretPosition(historyArea.getDocument().getLength());
    }

    /**
     * Loads the content of a log file into the history area.
     *
     * @param filePath Path to the log file.
     * @param header Header text to prepend before the log content.
     */
    public void loadLogFile(String filePath, String header) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            appendHistory(header);
            String line;
            while ((line = br.readLine()) != null) {
                appendHistory(line + "\n");
            }
            appendHistory("\n");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading log file: " + e.getMessage());
        }
    }

    /**
     * Updates the drawing view by recalculating the scale and repainting the panel.
     */
    public void updateView() {
        double[] overallBox = getOverallBoundingBox();
        if (overallBox != null) {
            double viewWidth = drawingPanel.getWidth();
            double viewHeight = drawingPanel.getHeight();
            double boxWidth = overallBox[2];
            double boxHeight = overallBox[3];
            if (scale == 1.0) {
                scale = Math.min(viewWidth / boxWidth, viewHeight / boxHeight) * 0.9;
            }
            transX = 0;
            transY = 0;
        }
        drawingPanel.repaint();
    }

    /**
     * Calculates the overall bounding box for all shapes.
     *
     * @return An array [minX, minY, width, height] or null if no shapes.
     */
    private double[] getOverallBoundingBox() {
        Object rawList = getRawShapeList();
        if (rawList == null) {
            return null;
        }
        double[] minMax = {Double.MAX_VALUE, Double.MAX_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        boolean updated = updateBoundingBoxMinMax(rawList, minMax);
        if (!updated) {
            return null;
        }
        double boxWidth = minMax[2] - minMax[0];
        double boxHeight = minMax[3] - minMax[1];
        return new double[]{minMax[0], minMax[1], boxWidth, boxHeight};
    }

    /**
     * Recursively updates the min/max coordinates for bounding box calculation.
     *
     * @param obj The object (list or shape) to process.
     * @param minMax Array to update: [minX, minY, maxX, maxY].
     * @return True if the minMax was updated.
     */
    private boolean updateBoundingBoxMinMax(Object obj, double[] minMax) {
        boolean updated = false;
        if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            for (Object item : list) {
                if (updateBoundingBoxMinMax(item, minMax)) {
                    updated = true;
                }
            }
        } else if (obj instanceof Shape) {
            Shape shape = (Shape) obj;
            double[] box = shape.getBoundingBox();
            double shapeMinX = Math.max(box[0], 0);
            double shapeMinY = Math.max(box[1], 0);
            double shapeMaxX = Math.max(box[0] + box[2], 0);
            double shapeMaxY = Math.max(box[1] + box[3], 0);
            if (shapeMaxX > shapeMinX && shapeMaxY > shapeMinY) {
                minMax[0] = Math.min(minMax[0], shapeMinX);
                minMax[1] = Math.min(minMax[1], shapeMinY);
                minMax[2] = Math.max(minMax[2], shapeMaxX);
                minMax[3] = Math.max(minMax[3], shapeMaxY);
                updated = true;
            }
        }
        return updated;
    }

    /**
     * Retrieves the raw list of shapes from the Clevis model using reflection.
     *
     * @return The list of shapes or an empty list if access fails.
     */
    private Object getRawShapeList() {
        try {
            Field shapeListField = Clevis.class.getDeclaredField("shapeList");
            shapeListField.setAccessible(true);
            return shapeListField.get(model);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Inner panel class for drawing shapes.
     */
    private class DrawingPanel extends JPanel {
        /**
         * Constructs the DrawingPanel with a white background.
         */
        public DrawingPanel() {
            setBackground(Color.WHITE);
        }
        /**
         * Paints the component, applying transformations and drawing shapes.
         *
         * @param g The graphics context.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();

            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            AffineTransform transform = new AffineTransform();
            transform.translate(transX, transY);
            transform.scale(scale, scale);
            g2d.setTransform(transform);
            // Clip to non-negative coordinates (logical space)
            g2d.clip(new Rectangle2D.Double(0, 0, Double.MAX_VALUE / 2, Double.MAX_VALUE / 2));
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(0.1f));
            drawShapes(g2d, getRawShapeList());
            g2d.dispose();
        }
        /**
         * Recursively draws shapes or lists of shapes.
         *
         * @param g2d The graphics context.
         * @param obj The object (list or shape) to draw.
         */
        private void drawShapes(Graphics2D g2d, Object obj) {
            if (obj instanceof List) {
                List<?> list = (List<?>) obj;
                for (Object item : list) {
                    drawShapes(g2d, item);
                }
            } else if (obj instanceof Shape) {
                Shape shape = (Shape) obj;
                if (shape instanceof Group) {
                    drawShapes(g2d, ((Group) shape).getComponents());
                } else if (shape instanceof Circle) {
                    Circle circle = (Circle) shape;
                    double radius = circle.getRadius();
                    g2d.drawOval((int) (circle.getX() - radius),
                            (int) (circle.getY() - radius),
                            (int) (2 * radius),
                            (int) (2 * radius));
                } else if (shape instanceof Line) {
                    Line line = (Line) shape;
                    g2d.drawLine((int) line.getX1(), (int) line.getY1(),
                            (int) line.getX2(), (int) line.getY2());
                } else if (shape instanceof Rectangle) {
                    Rectangle rect = (Rectangle) shape;
                    g2d.drawRect((int) rect.getX(), (int) rect.getY(),
                            (int) rect.getWidth(), (int) rect.getHeight());
                } else if (shape instanceof Square) {
                    Square square = (Square) shape;
                    g2d.drawRect((int) square.getX(), (int) square.getY(),
                            (int) square.getSide(), (int) square.getSide());
                }
            }
        }
    }
}