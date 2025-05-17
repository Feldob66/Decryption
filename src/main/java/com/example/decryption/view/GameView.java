// GameView.java
package com.example.decryption.view;

import com.example.decryption.model.entity.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * JavaFX View for the game UI.
 */
public class GameView {

    private final VBox root;
    private final List<Button> wordButtons = new ArrayList<>();
    private final Label feedbackLabel = new Label();
    private final Label scoreLabel = new Label();
    private final Button newGameButton = new Button("New Game");
    private final Button statsButton = new Button("Toggle Stats");

    // Components for the persistent stats display
    private final VBox statsContainer = new VBox(5);
    private final Label totalScoreLabel = new Label("Total Score: 0");
    private final VBox attemptHistoryContainer = new VBox(3);
    private boolean statsVisible = true;

    // Set to keep track of words that have already been guessed
    private final Set<String> guessedWords = new HashSet<>();

    // Track if the game is over
    private boolean gameOver = false;

    private Consumer<String> onWordSelected;
    private Runnable onNewGameRequested;
    private Runnable onShowStatsRequested;

    public GameView() {
        root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titleLabel = new Label("Decryption - Word Logic Game");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane buttonGrid = new GridPane();
        buttonGrid.setHgap(10);
        buttonGrid.setVgap(10);
        buttonGrid.setAlignment(Pos.CENTER);

        for (int i = 0; i < 8; i++) {
            Button btn = new Button("Word");
            btn.setPrefWidth(180);
            final int index = i;
            btn.setOnAction(e -> {
                if (onWordSelected != null && index < wordButtons.size() && !gameOver) {
                    String selectedWord = wordButtons.get(index).getText();
                    onWordSelected.accept(selectedWord);

                    // Disable this button after selection
                    wordButtons.get(index).setDisable(true);

                    // Add to guessed words
                    guessedWords.add(selectedWord);
                }
            });
            wordButtons.add(btn);
            buttonGrid.add(btn, i % 4, i / 4);
        }

        feedbackLabel.setStyle("-fx-font-size: 14px;");
        scoreLabel.setStyle("-fx-font-size: 14px;");

        HBox controlButtons = new HBox(15, newGameButton, statsButton);
        controlButtons.setAlignment(Pos.CENTER);

        newGameButton.setOnAction(e -> {
            if (onNewGameRequested != null) {
                onNewGameRequested.run();
                // Reset guessed words and game state when starting new game
                resetGame();
            }
        });

        // Set up stats toggle button
        statsButton.setOnAction(e -> {
            statsVisible = !statsVisible;
            statsContainer.setVisible(statsVisible);
            statsContainer.setManaged(statsVisible);
            statsButton.setText(statsVisible ? "Hide Stats" : "Show Stats");
        });

        // Configure the stats container
        setupStatsContainer();

        root.getChildren().addAll(titleLabel, buttonGrid, feedbackLabel, scoreLabel, controlButtons, statsContainer);
    }

    /**
     * Sets up the container for persistent stats display
     */
    private void setupStatsContainer() {
        // Style the stats container
        statsContainer.setPadding(new Insets(10));
        statsContainer.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #cccccc; -fx-border-radius: 5;");
        statsContainer.setMaxWidth(500);

        // Stats header
        Label statsHeader = new Label("Game Statistics");
        statsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Style total score
        totalScoreLabel.setStyle("-fx-font-size: 14px;");

        // History header
        Label historyHeader = new Label("Attempt History:");
        historyHeader.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Configure attempts container
        ScrollPane scrollPane = new ScrollPane(attemptHistoryContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(150);
        scrollPane.setMaxHeight(150);

        // Add components to stats container
        statsContainer.getChildren().addAll(statsHeader, totalScoreLabel, historyHeader, scrollPane);

        // Initial visibility
        statsContainer.setVisible(statsVisible);
        statsContainer.setManaged(statsVisible);
    }

    public VBox getRoot() {
        return root;
    }

    public void setOnWordSelectionHandler(Consumer<String> handler) {
        this.onWordSelected = handler;
    }

    public void setOnNewGameHandler(Runnable handler) {
        this.onNewGameRequested = handler;
    }

    public void setOnShowStatsHandler(Runnable handler) {
        this.onShowStatsRequested = handler;
    }

    public void updateView(GameState state) {
        List<String> words = state.getWordOptions();
        for (int i = 0; i < wordButtons.size(); i++) {
            if (i < words.size()) {
                String word = words.get(i);
                wordButtons.get(i).setText(word);

                // Disable if word was already guessed or if max attempts reached or game is over
                boolean shouldDisable = guessedWords.contains(word) ||
                        state.getCurrentAttempt() >= state.getMaxAttempts() ||
                        gameOver;
                wordButtons.get(i).setDisable(shouldDisable);
            } else {
                wordButtons.get(i).setText("-");
                wordButtons.get(i).setDisable(true);
            }
        }
        scoreLabel.setText("Attempts: " + state.getCurrentAttempt() + " / " + state.getMaxAttempts());

        // Disable all word buttons if max attempts reached
        if (state.getCurrentAttempt() >= state.getMaxAttempts()) {
            gameOver = true;
            disableAllWordButtons();
        }
    }

    public void showFeedback(String message, boolean isCorrect) {
        feedbackLabel.setText(message);
        feedbackLabel.setStyle(isCorrect ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    public void showGameWonMessage(int score) {
        gameOver = true;
        showFeedback("Congratulations! You won! Score: " + score, true);

        // Ensure all buttons are disabled when game is won
        disableAllWordButtons();
    }

    public void showGameLostMessage(String correctWord) {
        gameOver = true;
        showFeedback("Game Over. The correct word was: " + correctWord, false);
        disableAllWordButtons();
    }

    /**
     * Handles the game state when a correct word is guessed.
     * This should be called from your game controller when a guess is correct.
     *
     * @param correctWord The word that was correctly guessed
     * @param score The player's final score
     */
    public void handleCorrectGuess(String correctWord, int score) {
        // Add the correct word to guessed words
        guessedWords.add(correctWord);

        // Set game to over state
        gameOver = true;

        // Show win message
        showGameWonMessage(score);

        // Disable all buttons to prevent further guesses
        disableAllWordButtons();
    }

    /**
     * Disables all word buttons
     */
    private void disableAllWordButtons() {
        for (Button button : wordButtons) {
            button.setDisable(true);
        }
    }

    /**
     * Updates the persistent stats display
     */
    public void updateStats(int totalScore, List<String> attemptedWords, List<Integer> feedbackScores) {
        // Update total score
        totalScoreLabel.setText("Total Score: " + totalScore);

        // Clear previous attempts
        attemptHistoryContainer.getChildren().clear();

        // Add each attempt to the history
        for (int i = 0; i < attemptedWords.size(); i++) {
            // Calculate total characters in the word
            int wordLength = attemptedWords.get(i).length();

            // Create attempt information label
            String attemptText = "Attempt " + (i + 1) + ": " + attemptedWords.get(i) +
                    " (Correct Chars: " + feedbackScores.get(i) + "/" + wordLength + ")";
            Label attemptLabel = new Label(attemptText);

            // Style based on whether it's the most recent attempt
            if (i == attemptedWords.size() - 1) {
                attemptLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #0066cc;");
            }

            // Add to container
            attemptHistoryContainer.getChildren().add(attemptLabel);
        }

        // Make sure stats are visible after an update
        if (!statsVisible && attemptedWords.size() > 0) {
            statsVisible = true;
            statsContainer.setVisible(true);
            statsContainer.setManaged(true);
            statsButton.setText("Hide Stats");
        }
    }

    /**
     * Show stats in a popup - keep as a fallback option
     */
    public void showStatistics(int totalScore, List<String> attemptedWords, List<Integer> feedbackScores) {
        // Update the persistent display
        updateStats(totalScore, attemptedWords, feedbackScores);

        // Only show the popup if explicitly requested
        if (onShowStatsRequested != null) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Statistics");
            alert.setHeaderText("Your Game Stats");

            StringBuilder content = new StringBuilder();
            content.append("Total Score: ").append(totalScore).append("\n")
                    .append("Attempts Made: ").append(attemptedWords.size()).append("\n\n");

            for (int i = 0; i < attemptedWords.size(); i++) {
                int wordLength = attemptedWords.get(i).length();
                content.append("Attempt ").append(i + 1).append(": ")
                        .append(attemptedWords.get(i))
                        .append(" (Correct Chars: ").append(feedbackScores.get(i))
                        .append("/").append(wordLength).append(")\n");
            }

            alert.setContentText(content.toString());
            alert.showAndWait();
        }
    }

    /**
     * Resets the game view for a new game
     */
    public void resetGame() {
        guessedWords.clear();
        gameOver = false;
        feedbackLabel.setText("");
        for (Button button : wordButtons) {
            button.setDisable(false);
        }

        // Don't clear stats when resetting the game
        // User can see history across games
    }
}