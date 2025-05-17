package com.example.decryption.view;

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

public class GameView {

    private static final Logger logger = new Logger("GameView");

    private BorderPane rootPane;
    private VBox wordListContainer;
    private VBox attemptsContainer;
    private Label statusLabel;
    private Button newGameButton;
    private Button statsButton;

    private Consumer<String> onWordSelectionHandler;
    private Runnable onNewGameHandler;
    private Runnable onShowStatsHandler;

    public GameView() {
        initializeUI();
    }

    private void initializeUI() {
        rootPane = new BorderPane();
        rootPane.setPadding(new Insets(20));
        rootPane.getStyleClass().add("root-pane");

        // Title label
        Label titleLabel = new Label("DECRYPTION");
        titleLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#3498db"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setPadding(new Insets(10));
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        // Status label
        statusLabel = new Label("Welcome to Decryption! Select a word to begin.");
        statusLabel.setFont(Font.font("Arial", 14));
        statusLabel.setPadding(new Insets(5));
        statusLabel.setMaxWidth(Double.MAX_VALUE);
        statusLabel.setAlignment(Pos.CENTER);
        statusLabel.getStyleClass().add("status-label");

        VBox topSection = new VBox(5);
        topSection.setAlignment(Pos.CENTER);
        topSection.setPadding(new Insets(10));
        topSection.getChildren().addAll(titleLabel, statusLabel);
        rootPane.setTop(topSection);

        // Control buttons
        newGameButton = new Button("New Game");
        statsButton = new Button("Statistics");

        // Optional: comment these out to test button visibility if CSS is faulty
        newGameButton.getStyleClass().add("game-button");
        statsButton.getStyleClass().add("game-button");

        newGameButton.setOnAction(e -> {
            if (onNewGameHandler != null) onNewGameHandler.run();
        });
        statsButton.setOnAction(e -> {
            if (onShowStatsHandler != null) onShowStatsHandler.run();
        });

        HBox buttonBar = new HBox(10, newGameButton, statsButton);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(10));
        buttonBar.setMinHeight(50);
        buttonBar.setStyle("-fx-background-color: #f0f0f0;");

        VBox bottomBox = new VBox(buttonBar);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(10));
        rootPane.setBottom(bottomBox);

        // Word list and attempts
        wordListContainer = new VBox(5);
        wordListContainer.setAlignment(Pos.TOP_CENTER);
        wordListContainer.setPadding(new Insets(10));
        wordListContainer.getStyleClass().add("word-list-container");

        attemptsContainer = new VBox(5);
        attemptsContainer.setAlignment(Pos.TOP_LEFT);
        attemptsContainer.setPadding(new Insets(10));
        attemptsContainer.getStyleClass().add("attempts-container");

        GridPane centerGrid = new GridPane();
        centerGrid.setHgap(20);
        centerGrid.setVgap(10);
        centerGrid.setPadding(new Insets(10));
        centerGrid.add(wordListContainer, 0, 0);
        centerGrid.add(attemptsContainer, 1, 0);

        ScrollPane scrollPane = new ScrollPane(centerGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        rootPane.setCenter(scrollPane);

        VBox infoBox = createInfoSection();
        rootPane.setRight(infoBox);
    }

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

    public void updateView(GameState state) {
        wordListContainer.getChildren().clear();

        Label wordListHeader = new Label("SELECT A WORD:");
        wordListHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        wordListContainer.getChildren().add(wordListHeader);

        if (state.getWordOptions() != null) {
            for (String word : state.getWordOptions()) {
                Button wordButton = new Button(word);
                wordButton.setMaxWidth(Double.MAX_VALUE);
                wordButton.getStyleClass().add("word-button");

                if (state.getAttemptedWords().contains(word) || state.isGameOver()) {
                    wordButton.setDisable(true);
                }

                wordButton.setOnAction(e -> {
                    if (onWordSelectionHandler != null) {
                        onWordSelectionHandler.accept(word);
                    }
                });

                wordListContainer.getChildren().add(wordButton);
            }
        }

        updateAttemptsDisplay(state);

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

    private void updateAttemptsDisplay(GameState state) {
        attemptsContainer.getChildren().clear();

        Label attemptsHeader = new Label("YOUR ATTEMPTS:");
        attemptsHeader.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        attemptsContainer.getChildren().add(attemptsHeader);

        List<String> attemptedWords = state.getAttemptedWords();
        List<Integer> feedbackScores = state.getFeedbackScores();

        for (int i = 0; i < attemptedWords.size(); i++) {
            String attemptedWord = attemptedWords.get(i);
            int correctChars = feedbackScores.get(i);

            HBox attemptRow = new HBox(10);
            attemptRow.setAlignment(Pos.CENTER_LEFT);

            Label attemptNumLabel = new Label((i + 1) + ".");
            attemptNumLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

            Label wordLabel = new Label(attemptedWord);
            wordLabel.setFont(Font.font("Monospace", 14));

            Label feedbackLabel = new Label(correctChars + "/" + state.getTargetWord().length());
            feedbackLabel.setFont(Font.font("Arial", 14));

            boolean isWinningAttempt = state.isGameWon() && i == attemptedWords.size() - 1;

            if (isWinningAttempt) {
                attemptRow.setBackground(new Background(new BackgroundFill(
                        Color.web("#d6f5d6"), new CornerRadii(5), Insets.EMPTY)));
                attemptRow.setBorder(new Border(new BorderStroke(
                        Color.GREEN, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
            } else {
                attemptRow.setBackground(new Background(new BackgroundFill(
                        Color.web("#f5f5f5"), new CornerRadii(5), Insets.EMPTY)));
                attemptRow.setBorder(new Border(new BorderStroke(
                        Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(5), new BorderWidths(1))));
            }

            attemptRow.setPadding(new Insets(5));
            attemptRow.getChildren().addAll(attemptNumLabel, wordLabel, feedbackLabel);
            attemptsContainer.getChildren().add(attemptRow);
        }

        int remainingAttempts = state.getMaxAttempts() - state.getCurrentAttempt();
        if (remainingAttempts > 0 && !state.isGameOver()) {
            Label remainingLabel = new Label("Remaining attempts: " + remainingAttempts);
            remainingLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            attemptsContainer.getChildren().add(remainingLabel);
        }
    }

    public void showFeedback(String message, boolean isCorrect) {
        statusLabel.setText(message);
        statusLabel.setTextFill(isCorrect ? Color.GREEN : Color.BLACK);

        if (!isCorrect) {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                statusLabel.setText("Select your next word");
                statusLabel.setTextFill(Color.BLACK);
            });
            pause.play();
        }
    }

    public void showGameWonMessage(int score) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Victory!");
        alert.setHeaderText("Congratulations!");
        alert.setContentText("You guessed the word correctly!\nYou earned " + score + " points.");
        alert.showAndWait();
    }

    public void showGameLostMessage(String correctWord) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Over");
        alert.setHeaderText("You ran out of attempts!");
        alert.setContentText("The correct word was: " + correctWord +
                "\nBetter luck next time!");
        alert.showAndWait();
    }

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

    public BorderPane getRoot() {
        return rootPane;
    }

    public void setOnWordSelectionHandler(Consumer<String> handler) {
        this.onWordSelectionHandler = handler;
    }

    public void setOnNewGameHandler(Runnable handler) {
        this.onNewGameHandler = handler;
    }

    public void setOnShowStatsHandler(Runnable handler) {
        this.onShowStatsHandler = handler;
    }
}
