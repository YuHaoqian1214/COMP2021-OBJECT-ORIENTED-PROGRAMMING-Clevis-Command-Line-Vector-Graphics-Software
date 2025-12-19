package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.controller.ClevisController;
import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import java.io.IOException;

/**
 * The entry point of the Clevis application.
 * Handles command-line arguments, validates input files, and launches either the GUI or command-line mode.
 */
public class Application {
    /**
     * Main method to process command-line arguments and initialize the application.
     * Parses arguments to determine log file paths and whether to launch in GUI mode.
     * Validates file extensions and user input to decide the mode.
     *
     * @param args Command-line arguments. Expected format: -html <htmlFile> -txt <txtFile> [-gui]
     */
    public static void main(String[] args) {
        String htmlFile = null; // Path for the HTML log file
        String txtFile = null;  // Path for the TXT log file
        boolean guiMode = false; // Flag to indicate if GUI mode is enabled

        int i = 0;
        // Parse command-line arguments
        while (i < args.length) {
            String arg = args[i];
            if (arg.equals("-gui")) {
                // Enable GUI mode if "-gui" flag is present
                guiMode = true;
                i++;
            } else if (arg.equals("-html") || arg.equals("-txt")) {
                // Handle log file path arguments (-html and -txt)
                if (i + 1 >= args.length) {
                    System.out.println("Missing value for " + arg);
                    System.exit(1);
                }
                String value = args[i + 1];
                if (arg.equals("-html")) {
                    htmlFile = value;
                } else {
                    txtFile = value;
                }
                i += 2; // Move past the argument and its value
            } else {
                // Exit if an invalid argument is encountered
                System.out.println("Invalid argument: " + arg);
                System.exit(1);
            }
        }

        // Validate that both log files are provided and have correct extensions
        if (htmlFile == null || txtFile == null ||
                !htmlFile.toLowerCase().endsWith(".html") ||
                !txtFile.toLowerCase().endsWith(".txt")) {
            System.out.println("Usage error! Correct format: java Application -html log.html -txt log.txt [-gui]");
            System.exit(1);
        }

        // If GUI mode not specified via argument, prompt user for input
        if (!guiMode) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Do you want to use GUI? (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            guiMode = input.equals("y");
        }

        // Prepare arguments for Clevis initialization
        String[] clevisArgs = {"-html", htmlFile, "-txt", txtFile};

        // Launch GUI mode using SwingUtilities to ensure thread safety
        if (guiMode) {
            SwingUtilities.invokeLater(() -> {
                try {
                    new ClevisController(args);
                } catch (IOException e) {
                    System.out.println("GUI startup failed: " + e.getMessage());
                }
            });
        } else {
            // Launch command-line mode
            new Clevis().start(clevisArgs);
        }
    }
}