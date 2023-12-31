package Language;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import util.Observer;
import java.util.List;

public class LanguageMenuComponent implements Observer<List<String>> {
    private final ComboBox<String> languageComboBox;

    public LanguageMenuComponent() {
        languageComboBox = new ComboBox<>();
        languageComboBox.setPromptText("Select language");
        languageComboBox.getStyleClass().add("language-combo-box");
    }

    @Override
    public void update(List<String> languages) {
        languageComboBox.setItems(FXCollections.observableArrayList(languages));
    }
    public ComboBox<String> getLanguageComboBox() {
        return languageComboBox;
    }
}