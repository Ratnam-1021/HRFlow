package com.hrflow.app;

/**
 * Pure Java entry point that invokes JavaFX Application.
 * This bypasses the JavaFX 11+ Application classpath check and allows instant direct JVM startup.
 */
public class MainLauncher {
    public static void main(String[] args) {
        HRFlowApp.main(args);
    }
}
