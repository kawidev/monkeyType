package Game;

import Language.LanguageController;
import Time.TimeController;
import TypingText.TypingTextController;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class GameController {
    private final GameModel gameModel;
    private final GameView gameView;
    private final EndGameView endGameView;
    private final Stage primaryStage;

    private boolean isTabPressed = false;
    private boolean isEnterPressed = false;

    public GameController(Stage primaryStage, GameModel gameModel, GameView gameView, EndGameView endGameView) {
        this.primaryStage = primaryStage;
        this.gameView = gameView;
        this.gameModel = gameModel;
        this.endGameView = endGameView;

        gameModel.setGameView(gameView);
        new LanguageController(gameModel, gameView.getLanguageMenuView());
        new TimeController(gameModel, gameView.getTimeViewComponent());
        new TypingTextController(gameModel, gameModel.getTypingTextModel(), gameView.getTypingTextView());

        setupListeners();
    }

    private void setupListeners() {
        addKeyEvents(gameView.getScene());
        addKeyEvents(endGameView.getScene());

        gameView.getScene().addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (gameModel.isGamePaused()) {
                event.consume();
                return;
            }
            String input = event.getCharacter();
            gameModel.getTypingTextModel().processInput(input);

            if (!event.getCharacter().equals("\r") &&
                    !event.getCharacter().equals("\b") &&
                    !gameView.getTypingTextView().getInputField().isFocused()) {
                gameView.getTypingTextView().getInputField().requestFocus();
                gameModel.startGame();
            }
            event.consume();
        });
    }

    private void addKeyEvents(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                isTabPressed = true;
                checkAndPerformAction();
            } else if (event.getCode() == KeyCode.ENTER) {
                isEnterPressed = true;
                checkAndPerformAction();
            }
            handleKeyShortcuts(event);
        });

        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.TAB) {
                isTabPressed = false;
            } else if (event.getCode() == KeyCode.ENTER) {
                isEnterPressed = false;
            }
        });
    }

    private void checkAndPerformAction() {
        if (isTabPressed && isEnterPressed) {
            playAgain();
            gameModel.restartGameOrPlayAgain();
        }
    }

    private void handleKeyShortcuts(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER && event.isShiftDown()) {
            // SHIFT + ENTER
            gameModel.restartGameOrPlayAgain();
            event.consume();
            System.out.println("Restart/Play Again shortcut activated");
        } else if (event.isControlDown() && event.isShiftDown() && event.getCode() == KeyCode.P) {
            // CTRL + SHIFT + P
            pauseOrResumeGame();
            event.consume();
            System.out.println("Pause/Resume game shortcut activated");
        } else if (event.getCode() == KeyCode.ESCAPE) {
            // ESCAPE
            Platform.exit();
        }
    }

    private void pauseOrResumeGame() {
        if (gameModel.isGameStarted()) {
            if (!gameModel.isGamePaused()) {
                gameModel.pauseGame();
            } else {
                gameModel.resumeGame();
            }
        }
    }

    public void updateLanguageMenuView() {
        gameView.getLanguageMenuView().update(gameModel.getLanguageModel().getAvailableLanguages());
    }

    public void updateTimeViewComponent() {
        gameView.getTimeViewComponent().update(gameModel.getTimeModel().getAvailableTimes());
    }

    public void finishGame() {
        endGameView.updateChart();
        endGameView.updateResults();
        Platform.runLater(() -> primaryStage.setScene(endGameView.getScene()));
    }
    public void playAgain() {
        Platform.runLater(() -> primaryStage.setScene(gameView.getScene()));
    }
}
