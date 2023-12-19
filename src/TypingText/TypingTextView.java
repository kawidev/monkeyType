package TypingText;

import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import util.Observer;

import java.util.List;

public class TypingTextView implements Observer<List<TypingTextModel.CharacterWithStatus>> {

    private TextFlow textFlow;

    public TypingTextView() {
        this.textFlow = new TextFlow();
    }

    public void update(List<TypingTextModel.CharacterWithStatus> characterWithStatuses) {
        displayWords(characterWithStatuses);
    }

    // TypingTextView.java
    public void displayWords(List<TypingTextModel.CharacterWithStatus> characterWithStatuses) {
        textFlow.getChildren().clear();
        for (TypingTextModel.CharacterWithStatus cws : characterWithStatuses) {
            Text textNode = new Text(String.valueOf(cws.getCharacter())); // Display the actual character
            textNode.setFill(getColorForStatus(cws.getStatus())); // Apply color based on the status
            textFlow.getChildren().add(textNode);
        }
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
}
