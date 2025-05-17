package com.example.decryption.controller;

import com.example.decryption.model.GameService;
import com.example.decryption.model.entity.GameState;
import com.example.decryption.util.Logger;
import com.example.decryption.view.GameView;

import java.util.Observable;
import java.util.Observer;

/**
 * Controller class for handling user interactions and connecting model with view.
 * Implements an Observer pattern to listen for model changes.
 */
public class GameController implements Observer {

    private static final Logger logger = new Logger("GameController");

    private final GameService gameService;
    private final GameView gameView;

    public GameController(GameService gameService, GameView gameView) {
        this.gameService = gameService;
        this.gameView = gameView;

        // Register as observer of game state
        gameService.getGameState().addObserver(this);

        // Set up event handlers in the view
        setupEventHandlers();
    }

    /**
     * Initialize the game
     */
    public void initializeGame() {
        // Start a new game
        gameService.startNewGame();

        // Game state is automatically updated through an Observer pattern
    }

    /**
     * Setup event handlers for the view
     */
    private void setupEventHandlers() {
        // Handle word selection from the list
        gameView.setOnWordSelectionHandler(word -> {
            logger.info("Player selected word: " + word);

            // Process the guess
            GameService.GuessResult result = gameService.makeGuess(word);

            // Display feedback to the user
            gameView.showFeedback(result.getMessage(), result.isCorrect());

            if (result.isCorrect()) {
                gameView.showGameWonMessage(gameService.getGameState().getCurrentScore());
            } else if (gameService.getGameState().isGameOver()) {
                gameView.showGameLostMessage(gameService.getGameState().getTargetWord());
            }
        });

        // Handle new game button press
        gameView.setOnNewGameHandler(() -> {
            logger.info("New game requested");
            initializeGame();
        });

        // Handle stats button press
        gameView.setOnShowStatsHandler(() -> {
            logger.info("Statistics requested");
            gameView.showStatistics(
                    gameService.getGameState().getCurrentScore(),
                    gameService.getGameState().getAttemptedWords(),
                    gameService.getGameState().getFeedbackScores()
            );
        });
    }

    /**
     * Called when observed game state changes
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof GameState) {
            GameState state = (GameState) o;

            // Update the view with the new state
            gameView.updateView(state);

            logger.debug("Updated view with new game state");
        }
    }
}