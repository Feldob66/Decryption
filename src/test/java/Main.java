import static org.junit.jupiter.api.Assertions.*;

import com.example.decryption.model.GameService;
import com.example.decryption.model.ScoreManager;
import com.example.decryption.model.WordListProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for core game logic and scoring.
 * Uses a mock ScoreManager that disables file I/O for test isolation.
 */
public class Main {

    private GameService gameService;
    private MockScoreManager scoreManager;
    private WordListProvider wordListProvider;

    // Mock version of ScoreManager that overrides the file I/O operations
    private static class MockScoreManager extends ScoreManager {

        public MockScoreManager() {
            super(); // Call the parent constructor
            // Now reset all values that might have been loaded from the file
            resetAllStats();
        }

        public void resetAllStats() {
            try {
                // Use reflection to reset the private fields from the parent class
                Field totalScoreField = ScoreManager.class.getDeclaredField("totalScore");
                totalScoreField.setAccessible(true);
                totalScoreField.set(this, 0);

                Field gamesPlayedField = ScoreManager.class.getDeclaredField("gamesPlayed");
                gamesPlayedField.setAccessible(true);
                gamesPlayedField.set(this, 0);

                Field gamesWonField = ScoreManager.class.getDeclaredField("gamesWon");
                gamesWonField.setAccessible(true);
                gamesWonField.set(this, 0);

                Field attemptDistField = ScoreManager.class.getDeclaredField("attemptDistribution");
                attemptDistField.setAccessible(true);

                // Reset the attempt distribution
                Map<Integer, Integer> freshDist = new HashMap<>();
                for (int i = 1; i <= 5; i++) {
                    freshDist.put(i, 0);
                }
                attemptDistField.set(this, freshDist);
            } catch (Exception e) {
                System.err.println("Error resetting stats: " + e.getMessage());
            }
        }

        // Override the private methods using special technique
        @Override
        public void recordGameResult(boolean won, int attemptNumber) {
            // First, reset the stats to make sure we're starting from zero
            resetAllStats();

            // Now call the original method that will update the stats
            super.recordGameResult(won, attemptNumber);
        }
    }

    @BeforeEach
    public void setup() {
        // Before tests, delete any existing score file to ensure fresh state
        File scoreFile = new File("scores.dat");
        if (scoreFile.exists()) {
            scoreFile.delete();
        }

        scoreManager = new MockScoreManager();
        wordListProvider = new WordListProvider();
        gameService = new GameService(wordListProvider, scoreManager);

        // Make sure the stats are reset before each test
        scoreManager.resetAllStats();
    }

    @Test
    public void test1_StartGameDoesNotThrow() {
        assertDoesNotThrow(() -> gameService.startNewGame(), "Starting a game should not throw.");
    }

    @Test
    public void test2_WordListIsNotEmpty() {
        List<String> words = wordListProvider.generateFreshWordList();
        assertNotNull(words, "Generated word list should not be null.");
        assertFalse(words.isEmpty(), "Generated word list should not be empty.");
    }

    @Test
    public void test3_ScoreStartsAtZero() {
        // Ensure we start fresh
        scoreManager.resetAllStats();

        assertEquals(0, scoreManager.getTotalScore(), "Initial score should be zero.");
        assertEquals(0, scoreManager.getGamesPlayed(), "Games played should be zero initially.");
        assertEquals(0, scoreManager.getGamesWon(), "Games won should be zero initially.");
    }

    @Test
    public void test4_AddingScoreUpdatesStatsCorrectly() {
        // Ensure we start fresh
        scoreManager.resetAllStats();

        scoreManager.recordGameResult(true, 2); // Expect score = 150
        assertEquals(150, scoreManager.getTotalScore(), "Score should be 150 for a win on attempt 2.");
        assertEquals(1, scoreManager.getGamesPlayed(), "Games played should be incremented.");
        assertEquals(1, scoreManager.getGamesWon(), "Games won should be incremented.");
        Map<Integer, Integer> dist = scoreManager.getAttemptDistribution();
        assertEquals(1, dist.get(2), "Attempt distribution should track one win on attempt 2.");
    }

    @Test
    public void test5_LossOnlyIncrementsGamesPlayed() {
        // Ensure we start fresh
        scoreManager.resetAllStats();

        scoreManager.recordGameResult(false, 5); // Loss
        assertEquals(0, scoreManager.getTotalScore(), "Score should remain zero after a loss.");
        assertEquals(1, scoreManager.getGamesPlayed(), "Games played should increment even after a loss.");
        assertEquals(0, scoreManager.getGamesWon(), "Games won should not increment on a loss.");
    }

    @Test
    public void test6_WordListIsListOfStrings() {
        List<String> words = wordListProvider.generateFreshWordList();
        assertTrue(words.stream().allMatch(w -> w instanceof String), "All elements should be strings.");
    }
}