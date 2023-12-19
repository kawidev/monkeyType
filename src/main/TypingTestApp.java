package main;

import Language.LanguageController;
import Language.LanguageMenuComponent;
import Time.TimeController;
import Time.TimeView;
import TypingText.TypingTextController;
import TypingText.TypingTextModel;
import TypingText.TypingTextView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.GameModel;

public class TypingTestApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        // Inicjalizacja modelu języka i gry
        GameModel gameModel = new GameModel();



        // Inicjalizacja widoku menu językowego
        LanguageMenuComponent languageMenuView = new LanguageMenuComponent();
        // Inicjalizacja widoku menu czasu
        TimeView timeViewComponent = new TimeView();

        // Kontroler łączący model języka, model gry i widok
        LanguageController languageMenuController = new LanguageController(gameModel, languageMenuView);
        TimeController timeController = new TimeController(gameModel, timeViewComponent);

        TypingTextModel typingTextModel = gameModel.getTypingTextModel();
        TypingTextView typingTextView = new TypingTextView();
        TypingTextController typingTextController = new TypingTextController(gameModel, typingTextModel, typingTextView);

        // Uaktualnienie widoku menu językowego dostępnymi językami
        languageMenuView.update(gameModel.getLanguageModel().getAvailableLanguages());
        // Uaktualnienie widoku menu czasu dostępnymi opcjami czasu
        timeViewComponent.update(gameModel.getTimeModel().getAvailableTimes());


        // Ustawienie layoutu i sceny
        VBox rootLayout = new VBox(languageMenuView.getLanguageComboBox(), timeViewComponent.getTimeComboBox());
        rootLayout.getChildren().addAll(typingTextView.getTextFlow());
        Scene scene = new Scene(rootLayout, 800, 600);


        primaryStage.setTitle("Typing Test Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

