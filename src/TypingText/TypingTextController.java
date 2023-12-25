package TypingText;

import javafx.beans.Observable;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import Game.GameModel;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;

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
        textView.setModel(textModel);

        setupTypingListener();
    }
    private void setupTypingListener() {
        TextField inputField = textView.getInputField();

        // Update cursor position whenever the text changes.
        inputField.textProperty().addListener((obs, oldText, newText) -> {
            textView.updateCursorPosition();
        });

        inputField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                System.out.println("Backspace pressed"); // Debug print
                textModel.processInput("");
                textView.updateCursorPosition();
                event.consume();
            } else if (event.getCode().isWhitespaceKey()) {
                textView.updateCursorPosition();
                event.consume();
            }
        });


        inputField.addEventHandler(KeyEvent.KEY_TYPED, event -> {
            String character = event.getCharacter();
            if (!character.isEmpty() && !character.equals("\b") && !character.equals("\r")) {
                System.out.println("Character typed: " + character); // Debug print
                textModel.processInput(character);
                Text correspondingTextNode = textView.findTextNode(character);
                if (correspondingTextNode != null) {
                    textView.animateText(correspondingTextNode);
                }
                textView.updateCursorPosition(); // Update cursor position after typing
                event.consume();
            }
        });



    }





}