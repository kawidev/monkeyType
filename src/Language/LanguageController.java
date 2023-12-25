package Language;

import Game.GameModel;

public class LanguageController {

    private GameModel gameModel;
    private LanguageModel languageModel;
    private LanguageMenuComponent languageMenuComponent;

    public LanguageController(GameModel gameModel, LanguageMenuComponent languageMenuComponent) {
        this.gameModel = gameModel;
        this.languageModel = gameModel.getLanguageModel();
        this.languageMenuComponent = languageMenuComponent;

        languageModel.registerObserver(languageMenuComponent);
        setupViewListeners();
    }

    private void setupViewListeners() {
        languageMenuComponent.getLanguageComboBox().valueProperty().addListener((obs, oldVal, newVal) -> {
            // Logika reagująca na zmianę wybranego języka
            // Może obejmować informowanie modelu gry o zmianie języka

            gameModel.changeLanguage(newVal);
        });
    }
}
