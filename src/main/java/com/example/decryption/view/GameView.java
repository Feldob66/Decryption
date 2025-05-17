// GameView.java
package com.example.decryption.view;

import com.example.decryption.model.entity.GameState;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;
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
    private final Button statsButton = new Button("Show Stats");

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
                if (onWordSelected != null && index < wordButtons.size()) {
                    onWordSelected.accept(wordButtons.get(index).getText());
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
            if (onNewGameRequested != null) onNewGameRequested.run();
        });

        statsButton.setOnAction(e -> {
            if (onShowStatsRequested != null) onShowStatsRequested.run();
        });

        root.getChildren().addAll(titleLabel, buttonGrid, feedbackLabel, scoreLabel, controlButtons);
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
                wordButtons.get(i).setText(words.get(i));
                wordButtons.get(i).setDisable(false);
            } else {
                wordButtons.get(i).setText("-");
                wordButtons.get(i).setDisable(true);
            }
        }
        scoreLabel.setText("Attempts: " + state.getCurrentAttempt() + " / " + state.getMaxAttempts());
    }

    public void showFeedback(String message, boolean isCorrect) {
        feedbackLabel.setText(message);
        feedbackLabel.setStyle(isCorrect ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
    }

    public void showGameWonMessage(int score) {
        showFeedback("Congratulations! You won! Score: " + score, true);
    }

    public void showGameLostMessage(String correctWord) {
        showFeedback("Game Over. The correct word was: " + correctWord, false);
    }

    public void showStatistics(int totalScore, List<String> attemptedWords, List<Integer> feedbackScores) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Statistics");
        alert.setHeaderText("Your Game Stats");

        StringBuilder content = new StringBuilder();
        content.append("Total Score: ").append(totalScore).append("\n")
                .append("Attempts Made: ").append(attemptedWords.size()).append("\n\n");

        for (int i = 0; i < attemptedWords.size(); i++) {
            content.append("Attempt ").append(i + 1).append(": ")
                    .append(attemptedWords.get(i))
                    .append(" (Correct Chars: ").append(feedbackScores.get(i)).append(")\n");
        }

        alert.setContentText(content.toString());
        alert.showAndWait();
    }
}
