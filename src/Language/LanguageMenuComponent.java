package Language;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import util.Observer;

import java.util.List;

public class LanguageMenuComponent implements Observer<List<String>> {
    private final ComboBox<String> languageComboBox = new ComboBox<>();

    @Override
    public void update(List<String> languages) {
        languageComboBox.setItems(FXCollections.observableArrayList(languages));
    }
    public ComboBox<String> getLanguageComboBox() {
        return languageComboBox;
    }
}

