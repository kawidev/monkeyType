package Game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EndGameView {

    private GameModel gameModel;
    private VBox endGameLayout;
    private Scene scene;
    private LineChart<Number, Number> wpmChart; // LineChart to display the WPM data


    private Map<Integer, Integer> wordsPerSecond = new HashMap<>();
    private Map<Integer, Double> averageWordsPerSecond = new HashMap<>();

    public EndGameView(GameModel gameModel) {
        this.gameModel = gameModel;

        endGameLayout = new VBox(20);
        endGameLayout.setAlignment(Pos.CENTER);

        Label endGameLabel = new Label("Gra zakończona!");

        wpmChart = createChart(); // Inicjalizacja tutaj

        // Add the end game label and the WPM chart to the layout
        endGameLayout.getChildren().addAll(endGameLabel, wpmChart);
        scene = new Scene(endGameLayout, 800, 600);
    }

    public LineChart<Number, Number> createChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Time (seconds)");
        yAxis.setLabel("Words per Minute (WPM)");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Typing Test Results");

        XYChart.Series<Number, Number> actualWpmSeries = new XYChart.Series<>();
        actualWpmSeries.setName("Actual WPM");
        XYChart.Series<Number, Number> averageWpmSeries = new XYChart.Series<>();
        averageWpmSeries.setName("Average WPM");

        // Debugging: log the data points
        System.out.println("Actual WPM Data Points:");
        wordsPerSecond.forEach((second, wpm) -> {
            System.out.println("Second: " + second + ", WPM: " + wpm);
            if (second > 0) {
                actualWpmSeries.getData().add(new XYChart.Data<>(second, wpm));
            }
        });

        System.out.println("Average WPM Data Points:");
        averageWordsPerSecond.forEach((second, avgWpm) -> {
            System.out.println("Second: " + second + ", Avg WPM: " + avgWpm);
            if (second > 0) {
                averageWpmSeries.getData().add(new XYChart.Data<>(second, avgWpm));
            }
        });

        lineChart.getData().addAll(actualWpmSeries, averageWpmSeries);

        // Make sure the chart is updated
        lineChart.setAnimated(false); // Disable animations
        lineChart.setCreateSymbols(true); // This ensures data points are visible
        lineChart.getData().clear(); // Clear old data
        lineChart.getData().addAll(actualWpmSeries, averageWpmSeries); // Add new data

        return lineChart;
    }

    public void updateChart() {
        if (wpmChart == null) {
            wpmChart = createChart();
            endGameLayout.getChildren().add(wpmChart); // Dodaj wpmChart do VBox, jeśli jest nowy
        } else {
            wpmChart.getData().clear();
        }
        populateChartData(); // Aktualizuj dane wykresu
    }


    public void populateChartData() {
        wordsPerSecond = gameModel.getWordsPerSecond();
        averageWordsPerSecond = gameModel.getAverageWpmPerSecond();

        XYChart.Series<Number, Number> actualWpmSeries = new XYChart.Series<>();
        actualWpmSeries.setName("Actual WPM");
        XYChart.Series<Number, Number> averageWpmSeries = new XYChart.Series<>();
        averageWpmSeries.setName("Average WPM");

        wordsPerSecond.forEach((second, wpm) -> {
            if (second > 0) {
                actualWpmSeries.getData().add(new XYChart.Data<>(second, wpm));
            }
        });

        averageWordsPerSecond.forEach((second, avgWpm) -> {
            if (second > 0) {
                averageWpmSeries.getData().add(new XYChart.Data<>(second, avgWpm));
            }
        });

        wpmChart.getData().clear();
        wpmChart.getData().addAll(actualWpmSeries, averageWpmSeries);
    }


    public Scene getScene() {
        return scene;
    }
}
