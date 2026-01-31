package org.example.model;

import org.example.util.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides word lists for the game.
 * This class is responsible for loading and managing the daily word lists.
 */
public class WordListProvider {

    private static final Logger logger = new Logger("WordListProvider");
    private static final String WORDS_DIRECTORY = "/words";
    private static final String WORDLIST_FILE_PREFIX = "wordslength";
    private static final String WORDLIST_FILE_EXTENSION = ".txt";
    private final Random random = new Random();

    private Map<Integer, List<String>> wordsByLength;
    private Map<LocalDate, List<String>> dailyWordLists;

    public WordListProvider() {
        this.wordsByLength = new HashMap<>();
        this.dailyWordLists = new HashMap<>();
        loadWordLists();
    }

    /**
     * Loads word lists from resources directory
     */
    private void loadWordLists() {
        try {
            // Get available word lengths by checking resource files
            for (int length = 3; length <= 15; length++) {
                String filePath = WORDS_DIRECTORY + "/" + WORDLIST_FILE_PREFIX + length + WORDLIST_FILE_EXTENSION;
                InputStream is = getClass().getResourceAsStream(filePath);

                if (is != null) {
                    List<String> words = new ArrayList<>();
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            line = line.trim().toUpperCase();
                            if (!line.isEmpty() && line.length() == length) {
                                words.add(line);
                            }
                        }
                    }

                    if (!words.isEmpty()) {
                        wordsByLength.put(length, words);
                        logger.info("Loaded " + words.size() + " words of length " + length);
                    }
                }
            }

            // If no word lists were found, use fallback words
            if (wordsByLength.isEmpty()) {
                logger.warning("No word lists found in resources. Using fallback words.");
                provideFallbackWords();
            }
        } catch (IOException e) {
            logger.error("Error loading word lists", e);
            provideFallbackWords();
        }
    }

    /**
     * Creates fallback word lists in case no files are found
     */
    private void provideFallbackWords() {
        // Some sample words for testing, grouped by length
        Map<Integer, List<String>> fallbackWords = new HashMap<>();

        fallbackWords.put(3, Arrays.asList("AND", "THE", "BUT", "FOR", "NOT", "YET", "CAT", "DOG"));
        fallbackWords.put(4, Arrays.asList("CODE", "GAME", "PLAY", "TEST", "WORD", "JAVA", "LOOP", "MAIN"));
        fallbackWords.put(5, Arrays.asList("CLASS", "ARRAY", "STACK", "QUEUE", "INDEX", "WHILE", "LOGIC"));
        fallbackWords.put(6, Arrays.asList("LAMBDA", "STRING", "VECTOR", "THREAD", "PYTHON", "SYSTEM"));
        fallbackWords.put(7, Arrays.asList("PROGRAM", "BOOLEAN", "INTEGER", "COMPILE", "PACKAGE"));
        fallbackWords.put(8, Arrays.asList("VARIABLE", "FUNCTION", "DATABASE", "COMPILER", "ABSTRACT"));

        this.wordsByLength = fallbackWords;
        logger.info("Using fallback words with " + fallbackWords.size() + " different word lengths");
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
     * Gets a list of words of a specific length
     * @param length The word length to retrieve
     * @return List of words with the specified length or empty list if not found
     */
    public List<String> getWordsByLength(int length) {
        return wordsByLength.getOrDefault(length, new ArrayList<>());
    }

    /**
     * Gets a random set of words of a specific length
     * @param length The word length to retrieve
     * @param count The number of words to return
     * @return List of randomly selected words with the specified length
     */
    public List<String> getRandomWordsByLength(int length, int count) {
        List<String> words = getWordsByLength(length);

        if (words.isEmpty()) {
            return new ArrayList<>();
        }

        // If there are fewer words than requested, return all available words
        if (words.size() <= count) {
            return new ArrayList<>(words);
        }

        // Randomly select the requested number of words
        List<String> selectedWords = new ArrayList<>(words);
        Collections.shuffle(selectedWords, random);
        return selectedWords.subList(0, count);
    }

    /**
     * Generates a list of words for a specific date
     * @param date The date to generate words for
     */
    private void generateDailyWordList(LocalDate date) {
        // Use the date as a seed to ensure the same words appear on the same day
        Random seededRandom = new Random(date.toEpochDay());

        // Randomly select a word length between 4 and 8
        int[] availableLengths = wordsByLength.keySet().stream()
                .filter(len -> len >= 4 && len <= 8) // Filter for reasonable word lengths for the game
                .mapToInt(Integer::intValue)
                .toArray();

        // If no appropriate lengths found, default to length 5
        int selectedLength = availableLengths.length > 0 ?
                availableLengths[seededRandom.nextInt(availableLengths.length)] : 5;

        // Get words of the selected length
        List<String> wordsOfLength = wordsByLength.getOrDefault(selectedLength, new ArrayList<>());

        // If no words found for the selected length, try to use any available words
        if (wordsOfLength.isEmpty() && !wordsByLength.isEmpty()) {
            int anyLength = wordsByLength.keySet().iterator().next();
            wordsOfLength = wordsByLength.get(anyLength);
        }

        // Shuffle and select words
        List<String> shuffledWords = new ArrayList<>(wordsOfLength);
        Collections.shuffle(shuffledWords, seededRandom);

        // Select a subset of words (between 7-10 words)
        int wordCount = 7 + seededRandom.nextInt(4); // 7 to 10 words
        List<String> selectedWords = shuffledWords.subList(0, Math.min(wordCount, shuffledWords.size()));

        dailyWordLists.put(date, selectedWords);
        logger.info("Generated daily word list for " + date + " with " + selectedWords.size() +
                " words of length " + selectedLength);
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
            String upperWord = word.toUpperCase();
            int length = upperWord.length();

            // Add the word to the appropriate length list
            if (!wordsByLength.containsKey(length)) {
                wordsByLength.put(length, new ArrayList<>());
            }

            if (!wordsByLength.get(length).contains(upperWord)) {
                wordsByLength.get(length).add(upperWord);
            }
        }

        logger.info("Added " + words.size() + " custom words to the word lists");
    }

    /**
     * Returns the available word lengths
     * @return Set of available word lengths
     */
    public Set<Integer> getAvailableWordLengths() {
        return wordsByLength.keySet();
    }
}