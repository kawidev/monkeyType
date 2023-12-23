package TypingText;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import util.Observer;

import java.util.LinkedList;

public class TypingTextView implements Observer<LinkedList<WordNode>> {

    private VBox layout;
    private TextFlow textFlow;
    private TextField typingArea;

    public TypingTextView() {
        this.layout = new VBox(5);
        this.textFlow = new TextFlow();
        this.typingArea = new TextField();

        textFlow.setPrefSize(600, 100); // Adjust size as needed
        typingArea.setPrefWidth(600); // Adjust width as needed

        layout.setAlignment(Pos.CENTER);
        layout.getChildren().add(textFlow);
        layout.getChildren().add(typingArea);

        Platform.runLater(() -> typingArea.requestFocus());
    }

    @Override
    public void update(LinkedList<WordNode> wordNodes) {
        displayWords(wordNodes);
    }

    public void displayWords(LinkedList<WordNode> wordNodes) {
        Platform.runLater(() -> {
            textFlow.getChildren().clear();
            for (WordNode wordNode : wordNodes) {
                for (CharacterNode charNode : wordNode.getCharacterNodes()) { // Zakładając metodę getCharacterNodes
                    Text textNode = new Text(String.valueOf(charNode.getCharacter()));
                    textNode.setFill(getColorForStatus(charNode.getStatus())); // Zakładając metodę getStatus
                    textFlow.getChildren().add(textNode);
                }
                textFlow.getChildren().add(new Text(" ")); // Dodanie spacji między słowami
            }
        });
    }


    private Color getColorForStatus(TypingTextModel.CharacterStatus status) { // Ensure this matches the enum location
        switch (status) {
            case CORRECT:
                return Color.GREEN;
            case INCORRECT:
                return Color.RED;
            case EXTRA:
                return Color.ORANGE;
            case MISSING:
                return Color.BLACK;
            case NOT_TYPED:
                return Color.GRAY;
            default:
                return Color.BLACK; // Default color if none of the statuses match
        }
    }


    public TextFlow getTextFlow() {
        return textFlow;
    }

    public VBox getLayout() {
        return layout;
    }

    public TextField getInputField() {
        return typingArea;
    }
}