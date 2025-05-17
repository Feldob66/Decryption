package com.example.decryption;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.example.decryption.controller.GameController;
import com.example.decryption.model.GameService;
import com.example.decryption.model.ScoreManager;
import com.example.decryption.model.WordListProvider;
import com.example.decryption.util.Logger;
import com.example.decryption.view.GameView;

/**
 * Main application class for the Decryption word game.
 * This serves as the entry point for the JavaFX application.
 */
public class Main extends Application {

    private static final Logger logger = new Logger("Main");

    @Override
    public void start(Stage primaryStage) {
        try {
            logger.info("Starting Decryption application");

            // Initialize model components
            WordListProvider wordListProvider = new WordListProvider();
            ScoreManager scoreManager = new ScoreManager();
            GameService gameService = new GameService(wordListProvider, scoreManager);

            // Initialize view
            GameView gameView = new GameView();

            // Initialize controller with model and view
            GameController gameController = new GameController(gameService, gameView);

            // Set up the scene
            Scene scene = new Scene(gameView.getRoot(), 800, 600);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            // Configure and show the stage
            primaryStage.setTitle("Decryption - Word Logic Game");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(650);
            primaryStage.setMinHeight(500);
            primaryStage.show();

            // Initialize the game
            gameController.initializeGame();

            logger.info("Application started successfully");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        logger.info("Application shutting down");
        // Perform cleanup operations here if needed
    }

    /**
     * The main method is the entry point for the application
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}