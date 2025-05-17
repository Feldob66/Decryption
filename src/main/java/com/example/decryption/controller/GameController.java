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

        // Explicitly update the view to set the button labels
        GameState state = gameService.getGameState();
        gameView.updateView(state);
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

            // Update the game view and stats AFTER processing the guess
            GameState state = gameService.getGameState();
            gameView.updateView(state);

            // Update stats with latest information including the current guess
            gameView.updateStats(
                    state.getCurrentScore(),
                    state.getAttemptedWords(),
                    state.getFeedbackScores()
            );
        });

        // Handle new game button press
        gameView.setOnNewGameHandler(() -> {
            logger.info("New game requested");
            initializeGame();

            // You might want to add this to show empty stats panel after new game
            gameView.updateStats(
                    gameService.getGameState().getCurrentScore(),
                    gameService.getGameState().getAttemptedWords(),
                    gameService.getGameState().getFeedbackScores()
            );
        });

        // For the stats button, we can keep the popup functionality as a backup
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
