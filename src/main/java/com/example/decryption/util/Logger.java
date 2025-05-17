package com.example.decryption.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Simple logger utility for the application.
 */
public class Logger {

    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE = "game.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final String className;

    public Logger(String className) {
        this.className = className;
        initLogDirectory();
    }

    /**
     * Ensures the log directory exists
     */
    private void initLogDirectory() {
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.err.println("Failed to create log directory: " + LOG_DIRECTORY);
            }
        }
    }

    /**
     * Logs an info message
     * @param message The message to log
     */
    public void info(String message) {
        log("INFO", message);
    }

    /**
     * Logs an error message
     * @param message The message to log
     */
    public void error(String message) {
        log("ERROR", message);
    }

    /**
     * Logs an error message with exception details
     * @param message The message to log
     * @param exception The exception to include in the log
     */
    public void error(String message, Exception exception) {
        log("ERROR", message + " - " + exception.getMessage());
        exception.printStackTrace();
    }

    /**
     * Logs a debug message
     * @param message The message to log
     */
    public void debug(String message) {
        log("DEBUG", message);
    }

    /**
     * Writes a log entry to the log file
     * @param level The log level
     * @param message The message to log
     */
    private void log(String level, String message) {
        String timestamp = DATE_FORMAT.format(new Date());
        String logEntry = String.format("[%s] [%s] [%s] %s", timestamp, level, className, message);

        // Print to console
        System.out.println(logEntry);

        // Write to a file
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE, true))) {
            writer.println(logEntry);
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }}