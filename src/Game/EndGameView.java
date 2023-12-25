package Game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EndGameView {
    private VBox endGameLayout;
    private Scene scene;
    private Label scoreLabel;

    public EndGameView(GameModel gameModel) {
        endGameLayout = new VBox(20);
        endGameLayout.setAlignment(Pos.CENTER);

        Label endGameLabel = new Label("Gra zakończona!");
        scoreLabel = new Label(getScoreText(gameModel));

        endGameLayout.getChildren().addAll(endGameLabel, scoreLabel);
        scene = new Scene(endGameLayout, 800, 600);
    }

    private String getScoreText(GameModel gameModel) {
        return "Twój wynik: " +
                "\nPoprawne znaki: " + gameModel.getTypingTextModel().getCorrectCharsCount() +
                "\nNiepoprawne znaki: " + gameModel.getTypingTextModel().getIncorrectCharsCount() +
                "\nDodatkowe znaki: " + gameModel.getTypingTextModel().getExtraCharsCount() +
                "\nPominięte znaki: " + gameModel.getTypingTextModel().getMissedCharsCount() +
                "\nWpisane slowa: " + gameModel.getTypingTextModel().getWordsCount();
    }

    public Scene getScene() {
        return scene;
    }

    public void updateScores(GameModel gameModel) {
        scoreLabel.setText(getScoreText(gameModel));
    }
}
