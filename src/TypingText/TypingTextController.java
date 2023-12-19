package TypingText;

import model.GameModel;

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
    }
}
