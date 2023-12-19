package Time;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import util.Observer;
import java.util.List;

public class TimeView implements Observer<List<Integer>> {
    private ComboBox<Integer> timeComboBox = new ComboBox<>();

    public ComboBox<Integer> getTimeComboBox() {
        return timeComboBox;
    }

    @Override
    public void update(List<Integer> timeList) {
        timeComboBox.setItems(FXCollections.observableArrayList(timeList));
    }

    // ... metody do inicjalizacji ComboBox z opcjami czasu
}
