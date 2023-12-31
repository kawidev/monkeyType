package Game;

import javafx.application.Application;
import javafx.stage.Stage;

public class TypingTestApp extends Application {

    private GameController gameController;

    @Override
    public void start(Stage primaryStage) {
        GameModel gameModel = new GameModel();
        gameModel.setMain(this);

        GameView gameView = new GameView();
        EndGameView endGameView = new EndGameView(gameModel);
        gameController = new GameController(primaryStage, gameModel, gameView, endGameView);

        gameController.updateLanguageMenuView();
        gameController.updateTimeViewComponent();

        primaryStage.setTitle("Typing Test Application");
        primaryStage.setScene(gameView.getScene());

        primaryStage.setMinHeight(260);
        primaryStage.setMinWidth(260);
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

