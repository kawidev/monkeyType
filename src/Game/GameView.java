package Game;

import Language.LanguageMenuComponent;
import Time.TimeView;
import TypingText.TypingTextView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

public class GameView {
    private final VBox rootLayout;
    private final Scene scene;

    // Komponenty interfejsu użytkownika
    private LanguageMenuComponent languageMenuView;
    private TimeView timeViewComponent;
    private TypingTextView typingTextView;

    public GameView() {
        // Inicjalizacja komponentów interfejsu
        languageMenuView = new LanguageMenuComponent();
        timeViewComponent = new TimeView();
        typingTextView = new TypingTextView();

        // Konfiguracja TextFlow
        configureTextFlow();

        // Ustawienie layoutu i sceny
        rootLayout = new VBox(20);
        configureLayout();

        scene = new Scene(rootLayout, 800, 600);
        // Event filtry zostaną przeniesione do GameController
    }

    private void configureTextFlow() {
        // Stylizowanie textFlow
        typingTextView.getTextFlow().setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-border-style: solid;");
        typingTextView.getTextFlow().setPrefWidth(800);
        typingTextView.getTextFlow().setPrefHeight(100);
        typingTextView.getTextFlow().setMaxWidth(Region.USE_PREF_SIZE);
        typingTextView.getTextFlow().setMaxHeight(Region.USE_PREF_SIZE);
        VBox.setVgrow(typingTextView.getTextFlow(), Priority.ALWAYS);
        typingTextView.getTextFlow().setPadding(new Insets(5));
        typingTextView.getTextFlow().setTextAlignment(TextAlignment.CENTER);
    }

    private void configureLayout() {
        // Ustawienie layoutu
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.setPadding(new Insets(10));
        rootLayout.getChildren().addAll(
                languageMenuView.getLanguageComboBox(),
                timeViewComponent.getTimeComboBox(),
                typingTextView.getTextFlow(),
                typingTextView.getInputField()
        );

        // Konfiguracja TextField
        typingTextView.getInputField().setPrefWidth(800);
        typingTextView.getInputField().setMaxWidth(Region.USE_PREF_SIZE);
        VBox.setVgrow(typingTextView.getInputField(), Priority.ALWAYS);
    }

    public Scene getScene() {
        return scene;
    }

    // Gettery dla komponentów interfejsu
    public TypingTextView getTypingTextView() {
        return typingTextView;
    }

    public LanguageMenuComponent getLanguageMenuView() {
        return languageMenuView;
    }

    public TimeView getTimeViewComponent() {
        return timeViewComponent;
    }
}
