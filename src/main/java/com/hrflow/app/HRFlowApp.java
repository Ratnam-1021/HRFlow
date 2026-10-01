package com.hrflow.app;

import com.hrflow.ui.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * Main JavaFX Desktop Application entry point for HRFlow.
 */
public class HRFlowApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize AppContext and persistent storage
        AppContext.getInstance();

        MainLayout mainLayout = new MainLayout();
        Scene scene = new Scene(mainLayout, 1280, 820);

        URL cssUrl = getClass().getResource("/com/hrflow/css/style.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        primaryStage.setTitle("HRFlow — Intelligent HR Automation System");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1080);
        primaryStage.setMinHeight(700);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
