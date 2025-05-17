package com.example.decryption.model;

import com.example.decryption.model.entity.GameState;
import com.example.decryption.util.Logger;

import java.util.List;

/**
 * Main game logic implementation.
 * Handles the core gameplay mechanics and scoring.
 */
public class GameService {

    private static final Logger logger = new Logger("GameService");

    private final WordListProvider wordListProvider;
    private final ScoreManager scoreManager;
    private GameState gameState;

    public GameService(WordListProvider wordListProvider, ScoreManager scoreManager) {
        this.wordListProvider = wordListProvider;
        this.scoreManager = scoreManager;
        this.gameState = new GameState();
    }

    /**
     * Starts a new game
     */
    public void startNewGame() {
        // Create a new game state
        gameState = new GameState();

        // Get word list for today
        List<String> todaysWords = wordListProvider.getDailyWordList();
        gameState.setWordOptions(todaysWords);

        // Select target word
        String targetWord = wordListProvider.selectTargetWord(todaysWords);
        gameState.setTargetWord(targetWord);

        logger.info("New game started with target word: " + targetWord);
        logger.info("Word options: " + String.join(", ", todaysWords));

        // Notify observers about the state change
        gameState.updateState();
    }

    /**
     * Process a player's word guess
     * @param guessedWord The word guessed by the player
     * @return Result object with feedback
     */
    public GuessResult makeGuess(String guessedWord) {
        if (gameState.isGameOver()) {
            logger.info("Game is already over");
            return new GuessResult(false, 0, "Game is already over");
        }

        if (!gameState.getWordOptions().contains(guessedWord)) {
            logger.info("Invalid guess: " + guessedWord);
            return new GuessResult(false, 0, "Word is not in the options list");
        }

        // Record the attempt
        gameState.setCurrentAttempt(gameState.getCurrentAttempt() + 1);
        gameState.addAttemptedWord(guessedWord);

        // Calculate characters in correct position
        int correctCharCount = calculateCorrectCharacters(guessedWord, gameState.getTargetWord());
        gameState.addFeedbackScore(correctCharCount);

        // Check if the word is correct
        boolean isCorrect = guessedWord.equals(gameState.getTargetWord());

        if (isCorrect) {
            // Player won
            gameState.setGameWon(true);
            gameState.setGameOver(true);

            // Calculate score based on attempts
            int score = scoreManager.calculateScore(gameState.getCurrentAttempt());
            gameState.setCurrentScore(score);

            // Record game result
            scoreManager.recordGameResult(true, gameState.getCurrentAttempt());

            logger.info("Player won on attempt " + gameState.getCurrentAttempt() + " with score " + score);
        } else if (gameState.getCurrentAttempt() >= gameState.getMaxAttempts()) {
            // Player lost (ran out of attempts)
            gameState.setGameOver(true);
            scoreManager.recordGameResult(false, gameState.getCurrentAttempt());

            logger.info("Player lost after " + gameState.getCurrentAttempt() + " attempts");
        }

        // Notify observers about the state change
        gameState.updateState();

        return new GuessResult(
                isCorrect,
                correctCharCount,
                isCorrect ? "Correct!" : correctCharCount + "/" + gameState.getTargetWord().length() + " correct characters"
        );
    }

    /**
     * Calculate how many characters are in the correct position
     * @param guessedWord The player's guess
     * @param targetWord The actual target word
     * @return Number of characters in correct position
     */
    private int calculateCorrectCharacters(String guessedWord, String targetWord) {
        int correctCount = 0;
        int length = Math.min(guessedWord.length(), targetWord.length());

        for (int i = 0; i < length; i++) {
            if (guessedWord.charAt(i) == targetWord.charAt(i)) {
                correctCount++;
            }
        }

        return correctCount;
    }

    /**
     * Get the current game state
     * @return Current game state
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Class representing the result of a guess
     */
    public static class GuessResult {
        private final boolean correct;
        private final int correctCharCount;
        private final String message;

        public GuessResult(boolean correct, int correctCharCount, String message) {
            this.correct = correct;
            this.correctCharCount = correctCharCount;
            this.message = message;
        }

        public boolean isCorrect() {
            return correct;
        }

        public int getCorrectCharCount() {
            return correctCharCount;
        }

        public String getMessage() {
            return message;
        }
    }
}