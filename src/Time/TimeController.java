package Time;

import Game.GameModel;

public class TimeController {

    private final GameModel gameModel;
    private final TimeView timeView;

    public TimeController(GameModel gameModel, TimeView timeView) {
        this.gameModel = gameModel;
        this.timeView = timeView;

        setupViewListeners();
    }

    private void setupViewListeners() {
        timeView.getTimeComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            gameModel.setTimeLimit(newVal);
            gameModel.getTypingTextModel().processInput("");
        });
    }

}
