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
     * Starts a new game by generating a new list of 8 words and picking a target.
     */
    public void startNewGame() {
        gameState = new GameState();

        List<String> freshWords = wordListProvider.generateFreshWordList();
        if (freshWords == null || freshWords.size() != 8) {
            throw new IllegalStateException("Word list must contain exactly 8 words.");
        }

        gameState.setWordOptions(freshWords);

        String targetWord = wordListProvider.selectTargetWord(freshWords);
        if (!freshWords.contains(targetWord)) {
            throw new IllegalStateException("Target word must be selected from the provided word list.");
        }

        gameState.setTargetWord(targetWord);

        logger.info("New game started with target word: " + targetWord);
        logger.info("Word options: " + String.join(", ", freshWords));

        gameState.updateState();  // This should notify observers
    }

    /**
     * Process a player’s guess and return the result.
     */
    public GuessResult makeGuess(String guessedWord) {
        if (gameState.isGameOver()) {
            logger.info("Game is already over");
            return new GuessResult(false, "Game is already over");
        }

        if (!gameState.getWordOptions().contains(guessedWord)) {
            logger.info("Invalid guess: " + guessedWord);
            return new GuessResult(false, "Word is not in the options list");
        }

        gameState.setCurrentAttempt(gameState.getCurrentAttempt() + 1);
        gameState.addAttemptedWord(guessedWord);

        int correctCharCount = calculateCorrectCharacters(guessedWord, gameState.getTargetWord());
        gameState.addFeedbackScore(correctCharCount);

        boolean isCorrect = guessedWord.equals(gameState.getTargetWord());

        if (isCorrect) {
            gameState.setGameWon(true);
            gameState.setGameOver(true);
            int score = scoreManager.calculateScore(gameState.getCurrentAttempt());
            gameState.setCurrentScore(score);
            scoreManager.recordGameResult(true, gameState.getCurrentAttempt());

            logger.info("Player won on attempt " + gameState.getCurrentAttempt() + " with score " + score);
        } else if (gameState.getCurrentAttempt() >= gameState.getMaxAttempts()) {
            gameState.setGameOver(true);
            scoreManager.recordGameResult(false, gameState.getCurrentAttempt());

            logger.info("Player lost after " + gameState.getCurrentAttempt() + " attempts");
        }

        gameState.updateState(); // Notify view to update

        return new GuessResult(
                isCorrect,
                isCorrect
                        ? "Correct! You've decrypted the word!"
                        : correctCharCount + "/" + gameState.getTargetWord().length() + " correct characters"
        );
    }

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

    public GameState getGameState() {
        return gameState;
    }

    public static class GuessResult {
        private final boolean correct;
        private final String message;

        public GuessResult(boolean correct, String message) {
            this.correct = correct;
            this.message = message;
        }

        public boolean isCorrect() {
            return correct;
        }

        public String getMessage() {
            return message;
        }
    }
}
