package Time;

import model.GameModel;

public class TimeController {

    private GameModel gameModel;
    private TimeView timeView;

    public TimeController(GameModel gameModel, TimeView timeView) {
        this.gameModel = gameModel;
        this.timeView = timeView;

        setupViewListeners();
    }

    private void setupViewListeners() {
        timeView.getTimeComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            gameModel.setTimeLimit(newVal); // Przekazanie wybranego czasu do GameModel
        });
    }

}
