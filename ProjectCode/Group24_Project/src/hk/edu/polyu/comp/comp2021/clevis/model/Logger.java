package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * The Logger class handles logging of commands to both HTML and TXT files.
 * Implements AutoCloseable to support try-with-resources for safe resource management.
 */
public class Logger implements AutoCloseable {
    private BufferedWriter htmlWriter;  // Writer for HTML log file
    private BufferedWriter txtWriter;   // Writer for TXT log file
    private int Index = 1;              // Sequential index for logged commands

    /**
     * Constructs a Logger with specified paths for HTML and TXT logs.
     * Initializes file writers and sets up the HTML document structure.
     * @param htmlPath Path to the HTML log file
     * @param txtPath Path to the TXT log file
     * @throws IOException If file creation or writer initialization fails
     */
    public Logger(String htmlPath, String txtPath) throws IOException {
        this.htmlWriter = null;
        this.txtWriter = null;

        try {
            File htmlFile = new File(htmlPath);
            File txtFile = new File(txtPath);

            this.htmlWriter = new BufferedWriter(new FileWriter(htmlFile));
            this.txtWriter = new BufferedWriter(new FileWriter(txtFile));

            // Initialize HTML document structure with styling
            initializeHtmlDocument();

        } catch (IOException e) {
            // Close any partially initialized resources before propagating the error
            try {
                close();
            } catch (IOException closeException) {
                e.addSuppressed(closeException);
            }
            throw e;
        }
    }

    /**
     * Initializes the HTML document with basic structure and CSS styling.
     * Sets up a table for organizing command logs.
     * @throws IOException If writing to the HTML file fails
     */
    private void initializeHtmlDocument() throws IOException {
        htmlWriter.write("<html><head><style>");
        htmlWriter.write("body{font-family:sans-serif;}");
        htmlWriter.write("table{border-collapse:collapse;width:100%;}");
        htmlWriter.write("th,td{border:1px solid #dddddd;text-align:left;padding:8px;}");
        htmlWriter.write("tr:nth-child(even){background-color:#f2f2f2;}");
        htmlWriter.write("th{background-color:#4CAF50;color:white;}");
        htmlWriter.write("</style></head><body>");
        htmlWriter.write("<h2>Command Log</h2>");
        htmlWriter.write("<table border=\"1\">");
        htmlWriter.newLine();
        htmlWriter.write("<tr><th>Index</th><th>Command</th></tr>");
        htmlWriter.newLine();
    }

    /**
     * Logs a command to both HTML and TXT files.
     * Increments the command index after logging.
     * @param command The command string to log
     * @throws IOException If writing to either file fails
     */
    public void logCommand(String command) throws IOException {
        // Write to HTML file as a table row
        htmlWriter.write("<tr><td>" + Index + "</td><td>" + command + "</td></tr>");
        htmlWriter.newLine();
        Index++;

        // Write to TXT file as a plain line
        txtWriter.write(command);
        txtWriter.newLine();

        // Flush buffers to ensure immediate writing to disk
        htmlWriter.flush();
        txtWriter.flush();
    }

    /**
     * Closes all open resources and finalizes the HTML document.
     * Implements AutoCloseable to allow automatic resource management.
     * @throws IOException If closing any writer fails
     */
    public void close() throws IOException {
        IOException firstException = null;

        // Finalize and close HTML writer
        if (htmlWriter != null) {
            try {
                htmlWriter.write("</table></body></html>");
                htmlWriter.close();
            } catch (IOException e) {
                firstException = e;
            }
        }

        // Close TXT writer
        if (txtWriter != null) {
            try {
                txtWriter.close();
            } catch (IOException e) {
                if (firstException == null) {
                    firstException = e;
                } else {
                    firstException.addSuppressed(e);
                }
            }
        }

        // Propagate the first encountered exception (if any)
        if (firstException != null) {
            throw firstException;
        }
    }
}