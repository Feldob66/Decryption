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
    private static final String WORDLIST_FOLDER = "/wordlists/";
    private static final int MIN_WORD_LENGTH = 3;
    private static final int MAX_WORD_LENGTH = 14;
    private static final int DAILY_WORD_COUNT = 8;

    private final Random random = new Random();
    private List<String> allWords;
    private Map<LocalDate, List<String>> dailyWordLists;
    private final Map<Integer, List<String>> wordsByLength = new HashMap<>();

    public WordListProvider() {
        this.allWords = new ArrayList<>();
        this.dailyWordLists = new HashMap<>();
        loadWordListsByLength();
    }

    private void loadWordListsByLength() {
        for (int length = MIN_WORD_LENGTH; length <= MAX_WORD_LENGTH; length++) {
            String fileName = WORDLIST_FOLDER + "wordslength" + length + ".txt";
            try (InputStream is = getClass().getResourceAsStream(fileName)) {
                if (is == null) {
                    logger.warn("Word list file not found: " + fileName);
                    continue;
                }

                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        line = line.trim().toUpperCase();
                        if (!line.isEmpty()) {
                            allWords.add(line);
                            wordsByLength.putIfAbsent(length, new ArrayList<>());
                            wordsByLength.get(length).add(line);
                        }
                    }
                }

                logger.info("Loaded words from " + fileName);
            } catch (IOException e) {
                logger.error("Error loading word list from " + fileName, e);
            }
        }

        logger.info("Total words loaded: " + allWords.size());
    }

    public List<String> getDailyWordList() {
        LocalDate today = LocalDate.now();
        if (!dailyWordLists.containsKey(today)) {
            generateDailyWordList(today);
        }
        return dailyWordLists.get(today);
    }

    private void generateDailyWordList(LocalDate date) {
        Random seededRandom = new Random(date.toEpochDay());
        List<String> shuffledWords = new ArrayList<>(allWords);
        Collections.shuffle(shuffledWords, seededRandom);

        List<String> selectedWords = shuffledWords.subList(0, Math.min(DAILY_WORD_COUNT, shuffledWords.size()));
        if (selectedWords.size() < DAILY_WORD_COUNT) {
            logger.warn("Not enough words to fill 8. Padding with placeholders.");
            while (selectedWords.size() < DAILY_WORD_COUNT) {
                selectedWords.add("PLACEHOLDER");
            }
        }

        dailyWordLists.put(date, selectedWords);
        logger.info("Generated daily word list for " + date + " with " + selectedWords.size() + " words");
    }

    public List<String> generateFreshWordList() {
        if (wordsByLength.isEmpty()) return Collections.emptyList();

        List<Integer> lengths = new ArrayList<>(wordsByLength.keySet());
        int chosenLength = lengths.get(random.nextInt(lengths.size()));
        List<String> wordPool = new ArrayList<>(wordsByLength.getOrDefault(chosenLength, Collections.emptyList()));

        Collections.shuffle(wordPool, random);
        List<String> selected = wordPool.subList(0, Math.min(DAILY_WORD_COUNT, wordPool.size()));

        if (selected.size() < DAILY_WORD_COUNT) {
            logger.warn("Not enough fresh words to fill 8. Padding with placeholders.");
            while (selected.size() < DAILY_WORD_COUNT) {
                selected.add("PLACEHOLDER");
            }
        }

        return selected;
    }

    public String selectTargetWord(List<String> wordList) {
        if (wordList == null || wordList.isEmpty()) {
            return "";
        }
        return wordList.get(random.nextInt(wordList.size()));
    }

    public void addCustomWords(List<String> words) {
        if (words == null || words.isEmpty()) return;

        for (String word : words) {
            String upper = word.toUpperCase();
            if (!allWords.contains(upper)) {
                allWords.add(upper);

                int len = upper.length();
                wordsByLength.putIfAbsent(len, new ArrayList<>());
                wordsByLength.get(len).add(upper);
            }
        }

        logger.info("Added " + words.size() + " custom words to the word list");
    }
}
