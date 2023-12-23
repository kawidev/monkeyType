package TypingText;

import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import model.GameModel;
import javafx.scene.input.KeyEvent;

public class TypingTextController {

    private GameModel gameModel;
    private TypingTextModel textModel;
    private TypingTextView textView;

    public TypingTextController(GameModel gameModel, TypingTextModel textModel, TypingTextView textView) {
        this.gameModel = gameModel;
        this.textModel = textModel;
        this.textView = textView;

        gameModel.registerObserver(textModel);
        textModel.registerObserver(textView);

        setupTypingListener();
    }

    private void setupTypingListener() {
        TextField inputField = textView.getInputField();

        inputField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                textModel.processInput("");
                event.consume();
            } else if (event.getCode() == KeyCode.SPACE) {
                // Explicitly handle the spacebar keypress.
                textModel.processInput(" ");
                event.consume();
            }
        });

        inputField.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            if (!character.equals("\b") && !character.equals(" ")) {
                // Process normal characters, excluding backspace and space which are handled separately.
                textModel.processInput(character);
                event.consume();
            }
        });
    }

}