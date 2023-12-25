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

public class TypingTestApp extends Application implements Observer<Boolean> {

    private Stage primaryStage;
    private GameModel gameModel;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        // Inicjalizacja modelu języka i gry
        gameModel = new GameModel();
        gameModel.registerObserver(this);


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
        rootLayout.setPadding(new Insets(10)); // Ustaw margines dla całego kontenera

// Dodaj komponenty do layoutu
        rootLayout.getChildren().addAll(
                languageMenuView.getLanguageComboBox(),
                timeViewComponent.getTimeComboBox(),
                typingTextView.getTextFlow(), // Dodaj TextFlow do layoutu
                typingTextView.getInputField() // Dodaj TextField do layoutu
        );

// Konfiguracja TextField
        typingTextView.getInputField().setPrefWidth(800); // Ustaw preferowaną szerokość
        typingTextView.getInputField().setMaxWidth(Region.USE_PREF_SIZE); // Ogranicz szerokość do preferowanej
        VBox.setVgrow(typingTextView.getInputField(), Priority.ALWAYS); // Rozciągnij TextField, aby wypełnił dostępną przestrzeń w pionie

// Ustawienie sceny
        Scene scene = new Scene(rootLayout, 800, 600);

        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (!typingTextView.getInputField().isFocused()) {
                typingTextView.getInputField().requestFocus();
                event.consume(); // Consume the event if it's meant for typing
            }
        });

        // You may also want to consume KeyTyped events if they are not for control keys
        scene.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            if (!event.getCharacter().equals("\r") && // Check if it's not a control character (e.g., Enter)
                    !event.getCharacter().equals("\b") && // Check if it's not a control character (e.g., Backspace)
                    !typingTextView.getInputField().isFocused()) {
                typingTextView.getInputField().requestFocus();
                event.consume(); // Consume the event if it's meant for typing
            }
        });

        primaryStage.setTitle("Typing Test Application");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Ensure TextField is focused after the scene is shown
        Platform.runLater(typingTextView.getInputField()::requestFocus);
    }


    @Override
    public void update(Boolean isGameFinished) {
        if(isGameFinished) {
            showEndGameScene();
        }
    }

    private void showEndGameScene() {
        VBox endGameLayout = new VBox(20);
        endGameLayout.setAlignment(Pos.CENTER);

        Label endGameLabel = new Label("Gra zakończona!");
        Label scoreLabel = new Label("Twój wynik: " + // Tutaj dodaj wynik z gameModel
                "\nPoprawne znaki: " + gameModel.getTypingTextModel().getCorrectCharsCount() +
                "\nNiepoprawne znaki: " + gameModel.getTypingTextModel().getIncorrectCharsCount() +
                "\nDodatkowe znaki: " + gameModel.getTypingTextModel().getExtraCharsCount() +
                "\nPominięte znaki: " + gameModel.getTypingTextModel().getMissedCharsCount() +
                "\nWpisane slowa: " + gameModel.getTypingTextModel().getWordsCount() );

        endGameLayout.getChildren().addAll(endGameLabel, scoreLabel);

        Scene endGameScene = new Scene(endGameLayout, 800, 600);
        primaryStage.setScene(endGameScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

