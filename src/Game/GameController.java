package Game;

import Language.LanguageController;
import Language.LanguageModel;
import Time.TimeController;
import TypingText.TypingTextController;
import javafx.application.Platform;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GameController {
    private GameModel gameModel;
    private GameView gameView;
    private EndGameView endGameView;
    private Stage primaryStage;

    private LanguageController languageController;
    private TimeController timeController;
    private TypingTextController typingTextController;

    public GameController(Stage primaryStage, GameModel gameModel, GameView gameView, EndGameView endGameView) {
        this.primaryStage = primaryStage;
        this.gameModel = gameModel;
        this.gameView = gameView;
        this.endGameView = endGameView;

        languageController = new LanguageController(gameModel, gameView.getLanguageMenuView());
        this.timeController = new TimeController(gameModel, gameView.getTimeViewComponent());
        this.typingTextController = new TypingTextController(gameModel, gameModel.getTypingTextModel(), gameView.getTypingTextView());

        setupListeners();
    }

    private void setupListeners() {

        gameView.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!gameView.getTypingTextView().getInputField().isFocused()) {
                gameView.getTypingTextView().getInputField().requestFocus();
                gameModel.startGame();
                event.consume();
            }
        });

        gameView.getScene().addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().equals("\r") &&
                    !event.getCharacter().equals("\b") &&
                    !gameView.getTypingTextView().getInputField().isFocused()) {
                gameView.getTypingTextView().getInputField().requestFocus();
                gameModel.startGame();
                event.consume();
            }
        });
    }



    public void updateLanguageMenuView() {
        // Tutaj aktualizujemy LanguageMenuComponent
        gameView.getLanguageMenuView().update(gameModel.getLanguageModel().getAvailableLanguages());
    }

    public void updateTimeViewComponent() {
        // Tutaj aktualizujemy TimeViewComponent
        gameView.getTimeViewComponent().update(gameModel.getTimeModel().getAvailableTimes());
    }

    public void finishGame() {
        // Logika kończenia gry, np. zatrzymywanie timerów, zapisywanie wyników
        endGameView.updateChart();
        Platform.runLater(() -> {
            primaryStage.setScene(endGameView.getScene());
        });
    }
}
