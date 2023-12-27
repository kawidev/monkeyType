package Game;

import Language.LanguageController;
import Language.LanguageMenuComponent;
import Time.TimeController;
import Time.TimeView;
import TypingText.TypingTextController;
import TypingText.TypingTextModel;
import TypingText.TypingTextView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import Game.GameModel;
import util.Observer;

public class TypingTestApp extends Application {

    private Stage primaryStage;
    private GameModel gameModel;
    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        gameModel = new GameModel();
        gameModel.setMain(this);

        GameView gameView = new GameView();
        EndGameView endGameView = new EndGameView(gameModel);
        gameController = new GameController(primaryStage, gameModel, gameView, endGameView);

        // Uaktualnienie widoków
        gameController.updateLanguageMenuView();
        gameController.updateTimeViewComponent();

        primaryStage.setTitle("Typing Test Application");
        primaryStage.setScene(gameView.getScene());
        primaryStage.show();
    }

    public void update(Boolean isGameFinished) {
        if(isGameFinished) {
            gameController.finishGame();
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}

