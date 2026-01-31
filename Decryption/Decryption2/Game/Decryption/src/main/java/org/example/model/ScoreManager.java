package org.example.model;

import org.example.util.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the player's score and statistics.
 */
public class ScoreManager {

    private static final Logger logger = new Logger("ScoreManager");
    private static final String SCORE_FILE_PATH = "scores.dat";

    private int totalScore;
    private int gamesPlayed;
    private int gamesWon;
    private Map<Integer, Integer> attemptDistribution; // Attempt number -> count

    public ScoreManager() {
        this.totalScore = 0;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.attemptDistribution = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            attemptDistribution.put(i, 0);
        }

        loadScores();
    }

    /**
     * Calculates the score based on the attempt number
     * @param attemptNumber The attempt number (1-based)
     * @return The score for this attempt
     */
    public int calculateScore(int attemptNumber) {
        return switch (attemptNumber) {
            case 1 -> 200;
            case 2 -> 150;
            case 3 -> 100;
            case 4 -> 50;
            default -> 0;
        };
    }

    /**
     * Records a game result
     * @param won Whether the game was won
     * @param attemptNumber The attempt number when the game ended
     */
    public void recordGameResult(boolean won, int attemptNumber) {
        gamesPlayed++;

        if (won) {
            gamesWon++;
            int score = calculateScore(attemptNumber);
            totalScore += score;

            // Update attempt distribution
            attemptDistribution.put(attemptNumber, attemptDistribution.get(attemptNumber) + 1);

            logger.info("Game won on attempt " + attemptNumber + " with score " + score);
        } else {
            logger.info("Game lost after " + attemptNumber + " attempts");
        }

        saveScores();
    }

    /**
     * Saves the scores to file
     */
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(SCORE_FILE_PATH))) {
            oos.writeInt(totalScore);
            oos.writeInt(gamesPlayed);
            oos.writeInt(gamesWon);
            oos.writeObject(attemptDistribution);
            logger.info("Scores saved successfully");
        } catch (IOException e) {
            logger.error("Error saving scores", e);
        }
    }

    /**
     * Loads the scores from file
     */
    @SuppressWarnings("unchecked")
    private void loadScores() {
        File file = new File(SCORE_FILE_PATH);
        if (!file.exists()) {
            logger.info("No score file found. Starting with fresh scores.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            totalScore = ois.readInt();
            gamesPlayed = ois.readInt();
            gamesWon = ois.readInt();
            attemptDistribution = (Map<Integer, Integer>) ois.readObject();
            logger.info("Scores loaded successfully");
        } catch (IOException | ClassNotFoundException e) {
            logger.error("Error loading scores", e);
        }
    }

    // Getters
    public int getTotalScore() {
        return totalScore;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }

    public Map<Integer, Integer> getAttemptDistribution() {
        return attemptDistribution;
    }

    /**
     * Gets the win percentage
     * @return Win percentage as a double between 0 and 100
     */
    public double getWinPercentage() {
        if (gamesPlayed == 0) {
            return 0;
        }
        return (double) gamesWon / gamesPlayed * 100;
    }
}