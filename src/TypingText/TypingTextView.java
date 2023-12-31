package TypingText;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.StringExpression;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import util.Observer;

import java.util.LinkedList;

public class TypingTextView implements Observer<LinkedList<WordNode>> {

    DoubleBinding fontSizeBinding, cursorSizeBinding;

    private final TextFlow textFlow;
    private final TextField typingArea;
    private final Rectangle cursor;

    private LinkedList<WordNode> wordNodes = new LinkedList<>();

    public TypingTextView(Scene scene) {
        VBox layout = new VBox(5);
        this.textFlow = new TextFlow();
        Text text = new Text("To start a game you must choose language, time and then press any key on your keyboard");
        text.setTextAlignment(TextAlignment.CENTER);
        textFlow.getChildren().add(text);
        this.typingArea = new TextField();

        textFlow.setPrefSize(600, 100);
        typingArea.setPrefWidth(600);
        typingArea.setOpacity(0);
        typingArea.setFocusTraversable(true);
        typingArea.setMouseTransparent(true);

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(textFlow);
        layout.getChildren().add(typingArea);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.BLACK);

        textFlow.setEffect(dropShadow);

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(dropShadow.radiusProperty(), 3d)),
                new KeyFrame(Duration.seconds(1), new KeyValue(dropShadow.radiusProperty(), 30d)),
                new KeyFrame(Duration.seconds(2), new KeyValue(dropShadow.radiusProperty(), 3d))
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.setAutoReverse(true);
        timeline.play();


        Platform.runLater(typingArea::requestFocus);

        typingArea.textProperty().addListener((obs, oldText, newText) -> Platform.runLater(this::updateCursorPosition));

        layout.setOnMouseClicked(event -> typingArea.requestFocus());

        double baseWidth = 800.0;
        double baseHeight = 600.0;
        double baseFontSize = 22;
        double baseCursorHeight = 20;

        // Calculate the font size binding
        fontSizeBinding = (DoubleBinding) Bindings.min(
                scene.widthProperty().divide(baseWidth / baseFontSize),
                scene.heightProperty().divide(baseHeight / baseFontSize)
        );

        cursorSizeBinding = scene.heightProperty()
                .divide(baseHeight / baseCursorHeight);

        cursor = createCursor(cursorSizeBinding);
        layout.getChildren().add(cursor);

        StringExpression fontSizeStyleBinding = Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;");

        this.textFlow.getChildren().forEach(node -> {
            if (node instanceof Text) {
                node.styleProperty().bind(fontSizeStyleBinding);
            }
        });
    }

    @Override
    public void update(LinkedList<WordNode> wordNodes) {
        this.wordNodes = wordNodes;
        displayWords(wordNodes);
        updateCursorPosition();
    }

    public void displayWords(LinkedList<WordNode> wordNodes) {

        StringExpression fontSizeStyleBinding = Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;");
        Platform.runLater(() -> {
            textFlow.getChildren().clear();
            for (WordNode wordNode : wordNodes) {
                for (CharacterNode charNode : wordNode.getCharacterNodes()) {
                    Text textNode = new Text(String.valueOf(charNode.getCharacter()));
                    textNode.setFill(getColorForStatus(charNode.getStatus()));

                    if (fontSizeStyleBinding != null) {
                        textNode.styleProperty().bind(fontSizeStyleBinding);
                    }
                    textFlow.getChildren().add(textNode);
                }
                textFlow.getChildren().add(new Text(" "));
            }
            updateCursorPosition();
        });
    }

    private Color getColorForStatus(TypingTextModel.CharacterStatus status) {
        return switch (status) {
            case CORRECT -> Color.GREEN;
            case INCORRECT -> Color.RED;
            case EXTRA -> Color.ORANGE;
            case MISSING -> Color.WHITE;
            case NOT_TYPED -> Color.GRAY;
            default -> Color.BLACK;
        };
    }

    public void animateText(Text textNode) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), textNode);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2);
        st.play();
    }

    private Rectangle createCursor(DoubleBinding cursorSizeBinding) {
        Rectangle cursor = new Rectangle(2, 20, Color.WHITE);
        cursor.heightProperty().bind(cursorSizeBinding);
        FadeTransition ft = new FadeTransition(Duration.millis(500), cursor);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
        return cursor;
    }

    public void updateCursorPosition() {
        textFlow.getChildren().remove(cursor);

        int cursorPosition = calculateCursorPositionBasedOnCaret();

        if (cursorPosition < 0) cursorPosition = 0;
        if (cursorPosition > textFlow.getChildren().size()) cursorPosition = textFlow.getChildren().size();

        textFlow.getChildren().add(cursorPosition, cursor);

        if (cursorPosition > 0 && textFlow.getChildren().get(cursorPosition - 1) instanceof Text lastUpdatedChar) {
            animateText(lastUpdatedChar);
        }
    }

    private int calculateCursorPositionBasedOnCaret() {
        int cursorPositionInTextFlow = 0;
        boolean foundNotTyped = false;

        for (WordNode wordNode : this.wordNodes) {
            for (CharacterNode charNode : wordNode.getCharacterNodes()) {
                if (charNode.getStatus() == TypingTextModel.CharacterStatus.NOT_TYPED) {
                    foundNotTyped = true;
                    break;
                }
                cursorPositionInTextFlow++;
            }

            if (foundNotTyped) {
                break;
            }
            cursorPositionInTextFlow++;
        }
        return cursorPositionInTextFlow;
    }

    public void setModel() {
    }

    public Text findTextNode(String character) {
        ObservableList<Node> textNodes = textFlow.getChildren();
        int typedLength = typingArea.getText().length();

        if (typedLength > 0 && typedLength <= textNodes.size()) {
            Node node = textNodes.get(typedLength - 1);
            if (node instanceof Text && ((Text) node).getText().equals(character)) {
                return (Text) node;
            }
        }
        return null;
    }

    public TextFlow getTextFlow() {
        return textFlow;
    }

    public TextField getInputField() {
        return typingArea;
    }
}