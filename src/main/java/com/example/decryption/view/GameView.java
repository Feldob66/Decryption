package org.example.view;

import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import com.example.decryption.model.entity.GameState;
import com.example.decryption.util.Logger;

import java.util.List;
import java.util.function.Consumer;

/**
 * View class responsible for rendering the UI.
 */
public class GameView {

    private static final Logger logger = new Logger("GameView");

    // UI Components
    private BorderPane rootPane;
    private VBox wordListContainer;
    private VBox attemptsContainer;
    private Label statusLabel;
    private Button newGameButton;
    private Button statsButton;

    // Event handlers
    private Consumer<String> onWordSelectionHandler;
    private Runnable onNewGameHandler;
    private Runnable onShowStatsHandler;

    public GameView() {
        initializeUI();
    }

    /**
     * Initialize the main UI components
     */
    private void initializeUI() {
        // Root pane
        rootPane = new BorderPane();
        rootPane.setPadding(new Insets(20));
        rootPane.getStyleClass().add("root-pane");

        // Title
        Label titleLabel = new Label("DECRYPTION");
        titleLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#3498db"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(10));
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Status bar
        statusLabel = new Label("Welcome to Decryption! Select a word to begin.");
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setPadding(new Insets(5));
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.getStyleClass().add("status-label");

        // Control buttons
        HBox buttonBar = new HBox(10);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10));

        newGameButton = new Button("New Game");
        newGameButton.getStyleClass().add("game-button");
        newGameButton.setOnAction(e -> {
            if (onNewGameHandler != null) {
                onNewGameHandler.run();
            }
        });

        statsButton = new Button("Statistics");
        statsButton.getStyleClass().add("game-button");
        statsButton.setOnAction(e -> {
            if (onShowStatsHandler != null) {
                onShowStatsHandler.run();
            }
        });

        buttonBar.getChildren().addAll(newGameButton, statsButton);

        // Word list container
        wordListContainer = new VBox(5);
        wordListContainer.setAlignment(Pos.CENTER);
        wordListContainer.setPadding(new Insets(10));
        wordListContainer.getStyleClass().add("word-list-container");

        // Attempts container
        attemptsContainer = new VBox(5);
        attemptsContainer.setAlignment(Pos.CENTER_LEFT);
        attemptsContainer.setPadding(new Insets(10));
        attemptsContainer.getStyleClass().add("attempts-container");

        // Info section showing game rules
        VBox infoBox = createInfoSection();

        // Main layout with grid
        GridPane centerGrid = new GridPane();
        centerGrid.setHgap(20);
        centerGrid.setVgap(10);
        centerGrid.setPadding(new Insets(10));

        // Add components to grid
        centerGrid.add(wordListContainer, 0, 0);
        centerGrid.add(attemptsContainer, 1, 0);

        // Create a scroll pane for the center content
        ScrollPane scrollPane = new ScrollPane(centerGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Main layout assembly
        VBox topSection = new VBox();
        topSection.getChildren().addAll(titleLabel, statusLabel);

        // Layout the root pane
        rootPane.setTop(topSection);
        rootPane.setCenter(scrollPane);
        rootPane.setBottom(buttonBar);

        // Add info section to the right
        rootPane.setRight(infoBox);
    }

    /**
     * Creates the info section with game rules
     * @return VBox containing game info
     */
    private VBox createInfoSection() {
        VBox infoBox = new VBox(10);
        infoBox.setPadding(new Insets(10));
        infoBox.setMaxWidth(250);
        infoBox.getStyleClass().add("info-section");

        Label infoTitle = new Label("HOW TO PLAY");
        infoTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Text infoText = new Text(
                "1. Select a word from the list.\n\n" +
                        "2. After each guess, you'll see how many characters are in the correct position.\n\n" +
                        "3. You have 5 attempts to guess the correct word.\n\n" +
                        "4. Points:\n" +
                        "   - 1st try: 200 pts\n" +
                        "   - 2nd try: 150 pts\n" +
                        "   - 3rd try: 100 pts\n" +
                        "   - 4th try: 50 pts\n" +
                        "   - 5th try: 0 pts"
        );
        infoText.setWrappingWidth(230);
        infoText.setTextAlignment(TextAlignment.LEFT);

        infoBox.getChildren().addAll(infoTitle, infoText);
        return infoBox;
    }

    /**
     * Updates the view based on the current game state
     * @param state Current game state
     */
    public void updateView(GameState state) {
        // Clear existing word buttons
        wordListContainer.getChildren().clear();

        // Add header for word list
        Label wordListHeader = new Label("SELECT A WORD:");
        wordListHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        wordListContainer.getChildren().add(wordListHeader);

        // Add word buttons
        if (state.getWordOptions() != null) {
            for (String word : state.getWordOptions()) {
                Button wordButton = new Button(word);
                wordButton.setMaxWidth(Double.MAX_VALUE);
                wordButton.getStyleClass().add("word-button");

                // Disable if the word has already been attempted
                if (state.getAttemptedWords().contains(word) || state.isGameOver()) {
                    wordButton.setDisable(true);
                }

                // Set action handler
                wordButton.setOnAction(e -> {
                    if (onWordSelectionHandler != null) {
                        onWordSelectionHandler.accept(word);
                    }
                });

                wordListContainer.getChildren().add(wordButton);
            }
        }

        // Update attempts display
        updateAttemptsDisplay(state);

        // Update status label
        if (state.isGameOver()) {
            if (state.isGameWon()) {
                statusLabel.setText("Congratulations! You won with " + state.getCurrentScore() + " points!");
                statusLabel.setTextFill(Color.GREEN);
            } else {
                statusLabel.setText("Game over! The word was: " + state.getTargetWord());
                statusLabel.setTextFill(Color.RED);
            }
        } else {
            statusLabel.setText("Attempt " + (state.getCurrentAttempt() + 1) + " of " + state.getMaxAttempts());
            statusLabel.setTextFill(Color.BLACK);
        }
    }

    /**
     * Updates the attempts display with the current game state
     * @param state Current game state
     */
    private void updateAttemptsDisplay(GameState state) {
        attemptsContainer.getChildren().clear();

        // Add header for attempts
        Label attemptsHeader = new Label("YOUR ATTEMPTS:");
        attemptsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        attemptsContainer.getChildren().add(attemptsHeader);

        // Add attempt history
        List<String> attemptedWords = state.getAttemptedWords();
        List<Integer> feedbackScores = state.getFeedbackScores();

        for (int i = 0; i < attemptedWords.size(); i++) {
            String attemptedWord = attemptedWords.get(i);
            int correctChars = feedbackScores.get(i);

            // Create a HBox for each attempt row
            HBox attemptRow = new HBox(10);
            attemptRow.setAlignment(Pos.CENTER_LEFT);

            // Attempt number
            Label attemptNumLabel = new Label((i + 1) + ".");
            attemptNumLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            // Word
            Label wordLabel = new Label(attemptedWord);
            wordLabel.setFont(Font.font("Monospace", 14));

            // Feedback
            Label feedbackLabel = new Label(correctChars + "/" + state.getTargetWord().length());
            feedbackLabel.setFont(Font.font("Arial", 14));

            // Determine if this was the winning attempt
            boolean isWinningAttempt = state.isGameWon() && i == attemptedWords.size() - 1;

            // Style the attempt row
            if (isWinningAttempt) {
                attemptRow.setBackground(new Background(new BackgroundFill(
                        Color.web("#d6f5d6"), new CornerRadii(5), Insets.EMPTY)));
                attemptRow.setBorder(new Border(new BorderStroke(
                        Color.GREEN, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
                attemptRow.setPadding(new Insets(5));
            } else {
                attemptRow.setBackground(new Background(new BackgroundFill(
                        Color.web("#f5f5f5"), new CornerRadii(5), Insets.EMPTY)));
                attemptRow.setBorder(new Border(new BorderStroke(
                        Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
                attemptRow.setPadding(new Insets(5));
            }

            // Add labels to the row
            attemptRow.getChildren().addAll(attemptNumLabel, wordLabel, feedbackLabel);

            // Add the row to the container
            attemptsContainer.getChildren().add(attemptRow);
        }

        // Add remaining attempts info
        int remainingAttempts = state.getMaxAttempts() - state.getCurrentAttempt();
        if (remainingAttempts > 0 && !state.isGameOver()) {
            Label remainingLabel = new Label("Remaining attempts: " + remainingAttempts);
            remainingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            attemptsContainer.getChildren().add(remainingLabel);
        }
    }

    /**
     * Shows feedback to the user
     * @param message Feedback message
     * @param isCorrect Whether the guess was correct
     */
    public void showFeedback(String message, boolean isCorrect) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isCorrect ? Color.GREEN : Color.BLACK);

        // Reset status after a delay
        if (!isCorrect) {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                statusLabel.setText("Select your next word");
                statusLabel.setTextFill(Color.BLACK);
            });
            pause.play();
        }
    }

    /**
     * Shows game won message
     * @param score Final score
     */
    public void showGameWonMessage(int score) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Victory!");
        alert.setHeaderText("Congratulations!");
        alert.setContentText("You guessed the word correctly!\nYou earned " + score + " points.");
        alert.showAndWait();
    }

    /**
     * Shows game lost message
     * @param correctWord The correct word
     */
    public void showGameLostMessage(String correctWord) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("You ran out of attempts!");
        alert.setContentText("The correct word was: " + correctWord +
                "\nBetter luck next time!");
        alert.showAndWait();
    }

    /**
     * Shows statistics dialog
     * @param currentScore Current game score
     * @param attemptedWords List of attempted words
     * @param feedbackScores List of feedback scores
     */
    public void showStatistics(int currentScore, List<String> attemptedWords, List<Integer> feedbackScores) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Statistics");
        alert.setHeaderText("Game Progress");

        StringBuilder content = new StringBuilder();
        content.append("Current Score: ").append(currentScore).append("\n\n");
        content.append("Attempts:\n");

        for (int i = 0; i < attemptedWords.size(); i++) {
            content.append(i + 1).append(". ")
                    .append(attemptedWords.get(i)).append(" - ")
                    .append(feedbackScores.get(i)).append(" correct\n");
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }

    /**
     * Gets the root pane
     * @return Root BorderPane
     */
    public BorderPane getRoot() {
        return rootPane;
    }

    /**
     * Sets the handler for word selection
     * @param handler Consumer that takes the selected word
     */
    public void setOnWordSelectionHandler(Consumer<String> handler) {
        this.onWordSelectionHandler = handler;
    }

    /**
     * Sets the handler for new game button
     * @param handler Runnable to handle new game action
     */
    public void setOnNewGameHandler(Runnable handler) {
        this.onNewGameHandler = handler;
    }

    /**
     * Sets the handler for showing statistics
     * @param handler Runnable to handle stats button action
     */
    public void setOnShowStatsHandler(Runnable handler) {
        this.onShowStatsHandler = handler;
    }
}