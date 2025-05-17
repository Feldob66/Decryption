package com.example.decryption.model.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Represents the current state of the game.
 * Uses an Observable pattern to notify views when the state changes.
 */
public class GameState extends Observable {

    private String targetWord;
    private List<String> wordOptions;
    private int currentAttempt;
    private final int maxAttempts = 5;
    private List<String> attemptedWords;
    private List<Integer> feedbackScores;
    private int currentScore;
    private boolean gameWon;
    private boolean gameOver;

    public GameState() {
        this.attemptedWords = new ArrayList<>();
        this.feedbackScores = new ArrayList<>();
        this.currentAttempt = 0;
        this.currentScore = 0;
        this.gameWon = false;
        this.gameOver = false;
    }

    /**
     * Updates the game state and notifies observers
     */
    public void updateState() {
        setChanged();
        notifyObservers();
    }

    // Getters and setters
    public String getTargetWord() {
        return targetWord;
    }

    public void setTargetWord(String targetWord) {
        this.targetWord = targetWord;
    }

    public List<String> getWordOptions() {
        return wordOptions;
    }

    public void setWordOptions(List<String> wordOptions) {
        this.wordOptions = wordOptions;
    }

    public int getCurrentAttempt() {
        return currentAttempt;
    }

    public void setCurrentAttempt(int currentAttempt) {
        this.currentAttempt = currentAttempt;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public List<String> getAttemptedWords() {
        return attemptedWords;
    }

    public void addAttemptedWord(String word) {
        this.attemptedWords.add(word);
    }

    public List<Integer> getFeedbackScores() {
        return feedbackScores;
    }

    public void addFeedbackScore(int score) {
        this.feedbackScores.add(score);
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
    }

    public boolean isGameWon() {
        return gameWon;
    }

    public void setGameWon(boolean gameWon) {
        this.gameWon = gameWon;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Returns the remaining attempts
     * @return Number of attempts left
     */
    public int getRemainingAttempts() {
        return maxAttempts - currentAttempt;
    }
}