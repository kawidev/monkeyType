package main;

import Language.LanguageController;
import Language.LanguageMenuComponent;
import Time.TimeController;
import Time.TimeView;
import TypingText.TypingTextController;
import TypingText.TypingTextModel;
import TypingText.TypingTextView;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
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


        // Stylizowanie textFlow
        typingTextView.getTextFlow().setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: solid;");
        typingTextView.getTextFlow().setPrefWidth(800); // Ustaw szerokość preferowaną
        typingTextView.getTextFlow().setPrefHeight(100); // Ustaw wysokość preferowaną
        typingTextView.getTextFlow().setMaxWidth(Region.USE_PREF_SIZE); // Ogranicz szerokość do preferowanej
        typingTextView.getTextFlow().setMaxHeight(Region.USE_PREF_SIZE); // Ogranicz wysokość do preferowanej
        VBox.setVgrow(typingTextView.getTextFlow(), Priority.ALWAYS); // Rozciągnij TextFlow, aby wypełnił dostępną przestrzeń w pionie
        typingTextView.getTextFlow().setPadding(new Insets(5)); // Dodaj wewnętrzny margines
        typingTextView.getTextFlow().setTextAlignment(TextAlignment.CENTER); // Wyrównaj tekst do środka

        // Ustawienie layoutu i sceny
        VBox rootLayout = new VBox(20); // Dodaj spację między elementami
        rootLayout.setAlignment(Pos.CENTER); // Wyśrodkuj elementy w VBox
        rootLayout.getChildren().addAll(languageMenuView.getLanguageComboBox(), timeViewComponent.getTimeComboBox(), typingTextView.getTextFlow());
        Scene scene = new Scene(rootLayout, 800, 600);

        // Ustawienie layoutu i sceny

        primaryStage.setTitle("Typing Test Application");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

