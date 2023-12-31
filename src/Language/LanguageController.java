package Language;

import Game.GameModel;

public class LanguageController {

    private final GameModel gameModel;
    private final LanguageMenuComponent languageMenuComponent;

    public LanguageController(GameModel gameModel, LanguageMenuComponent languageMenuComponent) {
        this.gameModel = gameModel;
        LanguageModel languageModel = gameModel.getLanguageModel();
        this.languageMenuComponent = languageMenuComponent;

        languageModel.registerObserver(languageMenuComponent);
        setupViewListeners();
    }

    private void setupViewListeners() {
        languageMenuComponent.getLanguageComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            gameModel.changeLanguage(newVal);
            gameModel.setLanguageChosen(true);
            gameModel.getTypingTextModel().processInput("");
        });
    }
}