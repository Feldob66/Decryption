package com.example.decryption.model;

import com.example.decryption.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;

/**
 * Provides word lists for the game.
 * This class is responsible for loading and managing the daily word lists.
 */
public class WordListProvider {

    private static final Logger logger = new Logger("WordListProvider");
    private static final String DEFAULT_WORDLIST_PATH = "/wordlists/default_words.txt";
    private final Random random = new Random();

    private List<String> allWords;
    private Map<LocalDate, List<String>> dailyWordLists;

    public WordListProvider() {
        this.allWords = new ArrayList<>();
        this.dailyWordLists = new HashMap<>();
        loadDefaultWordList();
    }

    /**
     * Loads the default wordlist from resources
     */
    private void loadDefaultWordList() {
        try {
            InputStream is = getClass().getResourceAsStream(DEFAULT_WORDLIST_PATH);

            // If the default wordlist doesn't exist yet, create a sample one
            if (is == null) {
                logger.info("Default wordlist not found. Using built-in word list.");
                // Some sample words for testing
                allWords = Arrays.asList(
                        "ALGORITHM", "BOOTSTRAP", "COMPILER", "DATABASE", "ENCRYPTION",
                        "FUNCTION", "GRAPHICS", "HARDWARE", "INTERNET", "JAVASCRIPT",
                        "KEYBOARD", "LANGUAGE", "MICROCHIP", "NETWORK", "OPERATING",
                        "PROTOCOL", "QUANTUM", "RENDERING", "SOFTWARE", "TERMINAL",
                        "UNIVERSAL", "VARIABLE", "WIRELESS", "XENON", "YOUTUBE", "ZIPPING"
                );
                return;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim().toUpperCase();
                    if (!line.isEmpty()) {
                        allWords.add(line);
                    }
                }
            }

            logger.info("Loaded " + allWords.size() + " words from default word list");
        } catch (IOException e) {
            logger.error("Error loading word list", e);
        }
    }

    /**
     * Gets the daily word list for today
     * @return List of words for today's challenge
     */
    public List<String> getDailyWordList() {
        LocalDate today = LocalDate.now();

        if (!dailyWordLists.containsKey(today)) {
            generateDailyWordList(today);
        }

        return dailyWordLists.get(today);
    }

    /**
     * Generates a list of words for a specific date
     * @param date The date to generate words for
     */
    private void generateDailyWordList(LocalDate date) {
        // Use the date as a seed to ensure the same words appear on the same day
        Random seededRandom = new Random(date.toEpochDay());

        // Get all available words
        List<String> shuffledWords = new ArrayList<>(allWords);
        Collections.shuffle(shuffledWords, seededRandom);

        // Select a subset of words (between 7-10 words)
        int wordCount = 7 + seededRandom.nextInt(4); // 7 to 10 words
        List<String> selectedWords = shuffledWords.subList(0, Math.min(wordCount, shuffledWords.size()));

        dailyWordLists.put(date, selectedWords);
        logger.info("Generated daily word list for " + date + " with " + selectedWords.size() + " words");
    }

    /**
     * Gets a random word from the list as the target word
     * @param wordList The list to select from
     * @return A randomly selected word
     */
    public String selectTargetWord(List<String> wordList) {
        if (wordList == null || wordList.isEmpty()) {
            return "";
        }
        return wordList.get(random.nextInt(wordList.size()));
    }

    /**
     * Adds custom words to the word list
     * @param words List of words to add
     */
    public void addCustomWords(List<String> words) {
        if (words == null || words.isEmpty()) {
            return;
        }

        for (String word : words) {
            if (!allWords.contains(word.toUpperCase())) {
                allWords.add(word.toUpperCase());
            }
        }

        logger.info("Added " + words.size() + " custom words to the word list");
    }
}