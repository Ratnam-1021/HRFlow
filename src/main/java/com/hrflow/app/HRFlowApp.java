package com.hrflow.app;

import com.hrflow.ui.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.Taskbar;
import java.io.InputStream;
import java.net.URL;

/**
 * Main JavaFX Desktop Application entry point for HRFlow.
 */
public class HRFlowApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Initialize AppContext and persistent storage
        AppContext.getInstance();

        // 1. Set Application Window Icon
        try (InputStream is = getClass().getResourceAsStream("/com/hrflow/images/app_icon_orange.png")) {
            if (is != null) {
                primaryStage.getIcons().add(new Image(is));
            }
        } catch (Exception ignored) {}

        // 2. Set macOS Dock Icon
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    URL iconUrl = getClass().getResource("/com/hrflow/images/app_icon_orange.png");
                    if (iconUrl != null) {
                        taskbar.setIconImage(ImageIO.read(iconUrl));
                    }
                }
            }
        } catch (Exception ignored) {}

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
