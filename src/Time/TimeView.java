package Time;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import util.Observer;
import java.util.List;

public class TimeView implements Observer<List<Integer>> {
    private final ComboBox<Integer> timeComboBox;

    public TimeView() {
        this.timeComboBox = new ComboBox<>();
        timeComboBox.setPromptText("Select time");
        timeComboBox.setStyle("""
                -fx-background-radius: 20;
                    -fx-border-radius: 20;
                    -fx-padding: 3;
                    -fx-alignment: right;""");
    }

    public ComboBox<Integer> getTimeComboBox() {
        return timeComboBox;
    }

    @Override
    public void update(List<Integer> timeList) {
        timeComboBox.setItems(FXCollections.observableArrayList(timeList));
    }
}
