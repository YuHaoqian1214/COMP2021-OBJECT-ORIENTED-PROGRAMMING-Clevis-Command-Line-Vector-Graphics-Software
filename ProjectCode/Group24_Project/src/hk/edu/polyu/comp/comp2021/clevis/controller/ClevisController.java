package hk.edu.polyu.comp.comp2021.clevis.controller;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

/**
 * Controller component of the Clevis application in MVC pattern.
 * Handles user interactions, updates the model, and refreshes the view.
 */
public class ClevisController {
    private final Clevis model;
    private final hk.edu.polyu.comp.comp2021.clevis.view.ClevisView view;

    /**
     * Constructs the ClevisController with command-line arguments.
     *
     * @param args Command-line arguments for log files.
     * @throws IOException If logger initialization fails.
     */
    public ClevisController(String[] args) throws IOException {
        model = new Clevis();
        String htmlLogFile = args[1];
        String txtLogFile = args[3];

        initializeLogger(htmlLogFile, txtLogFile);

        view = new hk.edu.polyu.comp.comp2021.clevis.view.ClevisView(model);
        view.loadLogFile(txtLogFile, "TXT Log:\n");
        view.loadLogFile(htmlLogFile, "HTML Log:\n");

        view.setExecuteListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeCommand();
            }
        });
    }

    /**
     * Initializes the logger component using reflection.
     * 
     * @param htmlLogFile Path to HTML log file
     * @param txtLogFile Path to text log file
     * @throws IOException If logger initialization fails
     */
    private void initializeLogger(String htmlLogFile, String txtLogFile) throws IOException {
        try {
            Class<?> loggerClass = Class.forName("hk.edu.polyu.comp.comp2021.clevis.model.Logger");
            Object logger = loggerClass.getConstructor(String.class, String.class).newInstance(htmlLogFile, txtLogFile);

            Field loggerField = Clevis.class.getDeclaredField("logger");
            loggerField.setAccessible(true);
            loggerField.set(model, logger);
        } catch (Exception e) {
            throw new IOException("Failed to initialize logger", e);
        }
    }

    /**
     * Executes user command from the view.
     * Processes command, updates model, and refreshes view.
     */
    private void executeCommand() {
        String command = view.getCommand();
        if (!command.isEmpty()) {
            try {
                String timestamp = String.format("[%tF %<tT] %s\n", System.currentTimeMillis(), command);
                view.appendHistory(timestamp);

                logCommand(command);

                model.processCommand(command);

                if (command.equals("quit")) {
                    closeLogger();
                    view.dispose();
                    System.exit(0);
                }

                view.updateView();
            } catch (Exception ex) {
                String timestamp = String.format("[%tF %<tT] ", System.currentTimeMillis());
                String errorMsg = timestamp + "Error: " + ex.getMessage() + "\n";
                view.appendHistory(errorMsg);
                JOptionPane.showMessageDialog(view, "Error: " + ex.getMessage());
            }
            view.clearCommand();
        }
    }

    /**
     * Logs command using reflection to access logger methods.
     * 
     * @param command The command to log
     * @throws Exception If logging fails
     */
    private void logCommand(String command) throws Exception {
        Field loggerField = Clevis.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        Object logger = loggerField.get(model);
        Method logMethod = logger.getClass().getMethod("logCommand", String.class);
        logMethod.invoke(logger, command);
    }

    /**
     * Closes logger resources using reflection.
     * 
     * @throws Exception If closing logger fails
     */
    private void closeLogger() throws Exception {
        Field loggerField = Clevis.class.getDeclaredField("logger");
        loggerField.setAccessible(true);
        Object logger = loggerField.get(model);
        Method closeMethod = logger.getClass().getMethod("close");
        closeMethod.invoke(logger);
    }
}