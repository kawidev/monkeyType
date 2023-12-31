package Game;

import Language.LanguageMenuComponent;
import Time.TimeView;
import TypingText.TypingTextView;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class GameView {
    private final Scene scene;

    private final LanguageMenuComponent languageMenuView;
    private final TimeView timeViewComponent;
    private final TypingTextView typingTextView;

    private Label timeCounter;

    private final BooleanProperty gameStarted;

    public GameView() {
        scene = new Scene(new Group(), 800, 600);
        typingTextView = new TypingTextView(scene);

        languageMenuView = new LanguageMenuComponent();
        timeViewComponent = new TimeView();

        GridPane gridPane = createGridPane();
        configureTextFlow();
        // Add the grid to the scene
        scene.setRoot(gridPane);
        scene.getStylesheets().add("mainstyle.css");

        scaleComponentsWithWindow(scene);
        createGlowAnimation(languageMenuView.getLanguageComboBox());
        createGlowAnimation(timeViewComponent.getTimeComboBox());

        gameStarted = new SimpleBooleanProperty(false);
        timeCounter.visibleProperty().bind(gameStarted);

    }

    public GridPane createGridPane() {
        GridPane mainGridPane = new GridPane();
        mainGridPane.setVgap(10);

        ColumnConstraints mainColumn = new ColumnConstraints();
        mainColumn.setHgrow(Priority.ALWAYS);
        mainColumn.setFillWidth(true);
        mainGridPane.getColumnConstraints().addAll(mainColumn);

        RowConstraints row0 = new RowConstraints();
        row0.setPercentHeight(20);
        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(40);
        row1.setFillHeight(true);
        row1.setVgrow(Priority.ALWAYS);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(20);
        RowConstraints row3 = new RowConstraints();
        row3.setPercentHeight(20);
        mainGridPane.getRowConstraints().addAll(row0, row1, row2, row3);

        GridPane nestedGridPane = new GridPane();

        for (int i = 0; i < 2; i++) {
            ColumnConstraints nestedCol = new ColumnConstraints();
            nestedCol.setHgrow(Priority.ALWAYS);
            nestedCol.setFillWidth(true);

            RowConstraints nestedRow = new RowConstraints();
            nestedRow.setVgrow(Priority.ALWAYS);
            nestedRow.setFillHeight(true);

            nestedGridPane.getColumnConstraints().add(nestedCol);
            nestedGridPane.getRowConstraints().add(nestedRow);
        }

        Label headerLabel = new Label("Monkey Type");

        DoubleBinding fontSizeBinding = scene.widthProperty().add(scene.heightProperty()).divide(32);

        headerLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        GridPane.setHalignment(headerLabel, HPos.CENTER);
        GridPane.setValignment(headerLabel, VPos.CENTER);
        GridPane.setColumnSpan(headerLabel, 2);


        GridPane.setHalignment(timeViewComponent.getTimeComboBox(), HPos.LEFT);
        GridPane.setValignment(timeViewComponent.getTimeComboBox(), VPos.CENTER);
        timeViewComponent.getTimeComboBox().setPrefHeight(25);
        timeViewComponent.getTimeComboBox().setPrefWidth(250);

        GridPane.setHalignment(languageMenuView.getLanguageComboBox(), HPos.RIGHT);
        GridPane.setHgrow(languageMenuView.getLanguageComboBox(), Priority.ALWAYS);
        GridPane.setVgrow(languageMenuView.getLanguageComboBox(), Priority.ALWAYS);
        languageMenuView.getLanguageComboBox().setPrefHeight(25);
        languageMenuView.getLanguageComboBox().setPrefWidth(250);

        nestedGridPane.add(headerLabel, 0, 0);
        nestedGridPane.add(languageMenuView.getLanguageComboBox(), 0, 1);
        nestedGridPane.add(timeViewComponent.getTimeComboBox(), 1, 1);

        mainGridPane.add(nestedGridPane, 0, 0);
        GridPane.setHalignment(nestedGridPane, HPos.CENTER);
        GridPane.setValignment(nestedGridPane, VPos.CENTER);

        GridPane.setConstraints(typingTextView.getTextFlow(), 0, 1);
        GridPane.setHgrow(typingTextView.getTextFlow(), Priority.ALWAYS);
        GridPane.setVgrow(typingTextView.getTextFlow(), Priority.ALWAYS);
        GridPane.setHalignment(typingTextView.getTextFlow(), HPos.CENTER);
        GridPane.setValignment(typingTextView.getTextFlow(), VPos.CENTER);
        mainGridPane.getChildren().add(typingTextView.getTextFlow());


        GridPane.setConstraints(typingTextView.getInputField(), 0, 1);
        GridPane.setHgrow(typingTextView.getInputField(), Priority.ALWAYS);
        GridPane.setVgrow(typingTextView.getInputField(), Priority.ALWAYS);
        GridPane.setHalignment(typingTextView.getInputField(), HPos.CENTER);
        GridPane.setValignment(typingTextView.getInputField(), VPos.CENTER);
        mainGridPane.getChildren().add(typingTextView.getInputField());

        timeCounter = new Label("");
        timeCounter.getStylesheets().add("footer.css");
        timeCounter.getStyleClass().add("time-counter");

        Timeline colorChangeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        // Initial color value
                        new KeyValue(timeCounter.styleProperty(), "-fx-background-color: red;")),
                new KeyFrame(Duration.seconds(1),

                        e -> {
                            Color newColor = new Color(Math.random(), Math.random(), Math.random(), 1);
                            timeCounter.setStyle(String.format("-fx-background-color: #%02X%02X%02X; -fx-background-radius: 50%%;",
                                    (int) (newColor.getRed() * 255),
                                    (int) (newColor.getGreen() * 255),
                                    (int) (newColor.getBlue() * 255)));
                        })
        );
        colorChangeTimeline.setCycleCount(Timeline.INDEFINITE);
        colorChangeTimeline.play();

        GridPane.setRowIndex(timeCounter, 2);
        mainGridPane.getChildren().add(timeCounter);

        VBox mainFooter = new VBox(10);
        mainFooter.getStylesheets().add("footer.css");

        HBox firstShortcut = createShortcutHBox("Tab", "+", "Enter", "- Restart Game");
        HBox escShortcut = createShortcutHBox("Esc", "- End Game");
        HBox secondShortcut = createShortcutHBox("Ctrl", "+", "Shift", "+", "P", "- Pause Game");

        mainFooter.getChildren().addAll(firstShortcut, secondShortcut, escShortcut);

        GridPane.setConstraints(timeCounter, 0, 2);
        GridPane.setHalignment(timeCounter, HPos.CENTER);
        GridPane.setValignment(timeCounter, VPos.TOP);


        GridPane.setConstraints(mainFooter, 0, 3);
        GridPane.setHalignment(mainFooter, HPos.CENTER);
        GridPane.setValignment(mainFooter, VPos.CENTER);

        mainFooter.prefHeightProperty().bind(scene.heightProperty().multiply(0.1).subtract(10));

        mainFooter.prefWidthProperty().bind(scene.widthProperty());
        mainFooter.prefHeightProperty().bind(scene.heightProperty());

        GridPane.setConstraints(mainFooter, 0, 3);
        GridPane.setHalignment(mainFooter, HPos.CENTER);
        GridPane.setValignment(mainFooter, VPos.TOP);
        mainFooter.prefWidthProperty().bind(scene.widthProperty());
        mainFooter.prefHeightProperty().bind(scene.heightProperty().multiply(0.2).subtract(10));
        mainGridPane.add(mainFooter, 0, 3);

        return mainGridPane;
    }

    private HBox createShortcutHBox(String... keysAndText) {
        HBox shortcutBox = new HBox(5);
        shortcutBox.setAlignment(Pos.CENTER);

        DoubleBinding fontSizeBinding = scene.widthProperty().add(scene.heightProperty()).divide(100);

        for (int i = 0; i < keysAndText.length - 1; i++) {
            String key = keysAndText[i];
            if (key.equals("+")) {
                Text plus = new Text(key);
                plus.getStyleClass().add("non-key-text");
                // Bind the font size.
                plus.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
                shortcutBox.getChildren().add(plus);
            } else {
                Label keyLabel = new Label(key);
                keyLabel.getStyleClass().add("key-label");
                // Bind the font size.
                keyLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
                shortcutBox.getChildren().add(keyLabel);
            }
        }
        Text actionText = new Text(keysAndText[keysAndText.length - 1]);
        actionText.getStyleClass().add("non-key-text");
        // Bind the font size.
        actionText.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        shortcutBox.getChildren().add(actionText);

        return shortcutBox;
    }

    private void createGlowAnimation(Node node) {
        Glow glow = new Glow(0.0);
        node.setEffect(glow);

        Timeline glowAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.0)),
                new KeyFrame(Duration.seconds(0.5), new KeyValue(glow.levelProperty(), 0.3)),
                new KeyFrame(Duration.seconds(1.0), new KeyValue(glow.levelProperty(), 0.0))
        );
        glowAnimation.setCycleCount(Timeline.INDEFINITE);
        glowAnimation.setAutoReverse(true);

        node.setOnMouseEntered(e -> glowAnimation.play());
        node.setOnMouseExited(e -> {
            glowAnimation.stop();
            glow.setLevel(0.0);
        });
    }

    private void scaleComponentsWithWindow(Scene scene) {
        double initialWindowWidth = 800;
        double initialWindowHeight = 600;
        double initialButtonWidth = 250;
        double initialButtonHeight = 25;


        languageMenuView.getLanguageComboBox().setPrefSize(initialButtonWidth, initialButtonHeight);

        languageMenuView.getLanguageComboBox().prefWidthProperty().bind(
                scene.widthProperty().multiply(initialButtonWidth / initialWindowWidth));

        languageMenuView.getLanguageComboBox().prefHeightProperty().bind(
                scene.heightProperty().multiply(initialButtonHeight / initialWindowHeight));

        timeViewComponent.getTimeComboBox().setPrefSize(initialButtonWidth, initialButtonHeight);

        timeViewComponent.getTimeComboBox().prefWidthProperty().bind(
                scene.widthProperty().multiply(initialButtonWidth / initialWindowWidth));

        timeViewComponent.getTimeComboBox().prefHeightProperty().bind(
                scene.heightProperty().multiply(initialButtonHeight / initialWindowHeight));

    }

    private void configureTextFlow() {
        typingTextView.getTextFlow().setPrefWidth(600);
        typingTextView.getTextFlow().setMaxWidth(600);
    }

    public Scene getScene() {
        return scene;
    }

    public TypingTextView getTypingTextView() {
        return typingTextView;
    }

    public LanguageMenuComponent getLanguageMenuView() {
        return languageMenuView;
    }

    public TimeView getTimeViewComponent() {
        return timeViewComponent;
    }

    public void notifyAboutStartGameToShowTimeLabel() {
        gameStarted.set(true);
    }

    public void notifyAboutPlayAgainOrEndGame() {
        gameStarted.set(false);
    }

    public void updateTimeLeft(int remainingTime) {
        Platform.runLater(() -> timeCounter.setText(String.valueOf(remainingTime)));

        }
}
