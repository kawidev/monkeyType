package TypingText;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import util.Observer;

import java.util.LinkedList;

public class TypingTextView implements Observer<LinkedList<WordNode>> {

    private final TextFlow textFlow;
    private final TextField typingArea;
    private final Rectangle cursor;

    private LinkedList<WordNode> wordNodes = new LinkedList<>();


    public TypingTextView() {
        VBox layout = new VBox(5);
        this.textFlow = new TextFlow();
        this.typingArea = new TextField();
        cursor = createCursor();

        textFlow.setPrefSize(600, 100); // Adjust size as needed
        typingArea.setPrefWidth(600); // Adjust width as needed
        typingArea.setOpacity(0);
        typingArea.setFocusTraversable(true);
        typingArea.setMouseTransparent(true);

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(textFlow);
        layout.getChildren().add(typingArea);
        layout.getChildren().add(cursor);

        Platform.runLater(typingArea::requestFocus);

        typingArea.textProperty().addListener((obs, oldText, newText) -> Platform.runLater(this::updateCursorPosition));

        layout.setOnMouseClicked(event -> typingArea.requestFocus());


    }

    @Override
    public void update(LinkedList<WordNode> wordNodes) {
        this.wordNodes = wordNodes;
        displayWords(wordNodes);
        updateCursorPosition();
    }

    public void displayWords(LinkedList<WordNode> wordNodes) {
        Platform.runLater(() -> {
            textFlow.getChildren().clear();
            for (WordNode wordNode : wordNodes) {
                for (CharacterNode charNode : wordNode.getCharacterNodes()) {
                    Text textNode = new Text(String.valueOf(charNode.getCharacter()));
                    textNode.setFill(getColorForStatus(charNode.getStatus()));
                    textNode.setStyle("-fx-font-size: 22px;");
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
            case MISSING -> Color.BLACK;
            case NOT_TYPED -> Color.GRAY;
            default -> Color.BLACK;
        };
    }

    public void animateText(Text textNode) {
        ScaleTransition st = new ScaleTransition(Duration.millis(200), textNode);
        st.setFromX(1);
        st.setFromY(1);
        st.setToX(1.2); // Slightly increase size for visible effect
        st.setToY(1.2);
        st.setAutoReverse(true);
        st.setCycleCount(2); // Play the animation twice (scale up and down)
        st.play();
    }

    private Rectangle createCursor() {
        Rectangle cursor = new Rectangle(2, 20, Color.BLACK); // Adjust size as needed
        // Blinking animation
        FadeTransition ft = new FadeTransition(Duration.millis(500), cursor);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(Animation.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
        return cursor;
    }

    public void updateCursorPosition() {
        // Remove the cursor from its current position
        textFlow.getChildren().remove(cursor);

        // Calculate the cursor position based on the CharacterNode statuses
        int cursorPosition = calculateCursorPositionBasedOnCaret();

        // Ensure the position is within valid range
        if (cursorPosition < 0) cursorPosition = 0;
        if (cursorPosition > textFlow.getChildren().size()) cursorPosition = textFlow.getChildren().size();

        // Debugging: Print the cursor position
        System.out.println("Updating cursor position to: " + cursorPosition);

        // Add the cursor at the new position
        textFlow.getChildren().add(cursorPosition, cursor);

        // Animate the last updated character if it's not the cursor
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
                    break; // No need to check further characters in this word
                }
                cursorPositionInTextFlow++; // Move the cursor position for every typed character node
            }

            if (foundNotTyped) {
                break; // No need to check further words
            }

            // Account for the space after each word
            cursorPositionInTextFlow++;
        }

        System.out.println("Calculated cursor position: " + cursorPositionInTextFlow);
        return cursorPositionInTextFlow;
    }

    public void setModel(TypingTextModel model) {
    }

    public Text findTextNode(String character) {
        // Assuming characters are added sequentially and each character is a separate Text node in the TextFlow
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