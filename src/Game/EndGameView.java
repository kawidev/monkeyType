package Game;

import javafx.animation.ScaleTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class EndGameView {

    private final GameModel gameModel;
    private final GridPane root;
    private final Scene scene;

    private LineChart<Number, Number> wpmChart;

    private Map<Integer, Integer> wordsPerSecond = new HashMap<>();
    private Map<Integer, Double> averageWordsPerSecond = new HashMap<>();

    private Label wpmLabel, wpmResultLabel, accLabel, accResultLabel;

    private Label correctLabel, incorrectLabel, extrasLabel, missedLabel;
    private Label correctResult, incorrectResult, extrasResult, missedResult;

    public EndGameView(GameModel gameModel) {
        this.gameModel = gameModel;
        wpmChart = createChart();
        root = createMainGridPane();

        scene = new Scene(root, 800, 600);
        scene.getStylesheets().add("styles.css");

        double baseWidth = 800.0;
        double baseHeight = 600.0;
        double baseFontSize = 15.0;

        double scaleFactorWidth = baseWidth / baseFontSize;
        double scaleFactorHeight = baseHeight / baseFontSize;

        DoubleBinding fontSizeBinding = (DoubleBinding) Bindings.min(
                scene.widthProperty().divide(scaleFactorWidth),
                scene.heightProperty().divide(scaleFactorHeight)
        );

        wpmLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        wpmResultLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        accLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        accResultLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));


        correctLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        incorrectLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        extrasLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        missedLabel.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));

        // Binding font size for other labels
        correctResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        incorrectResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        extrasResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
        missedResult.styleProperty().bind(Bindings.concat("-fx-font-size: ", fontSizeBinding.asString(), "px;"));
    }


    private GridPane createMainGridPane() {
        GridPane gridPane = new GridPane();

        int rows = 10;
        int cols = 10;

        for (int i = 0; i < cols; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(100.0 / cols);
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < rows; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight(100.0 / rows);
            gridPane.getRowConstraints().add(rowConstraints);
        }

        Label imageHeader = new Label("Gra zakończona");
        imageHeader.getStyleClass().add("game-over-label");
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), imageHeader);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.setCycleCount(ScaleTransition.INDEFINITE);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();


        imageHeader.setAlignment(Pos.CENTER);
        imageHeader.setMaxWidth(Double.MAX_VALUE);
        GridPane.setFillWidth(imageHeader, true);

        GridPane.setColumnSpan(imageHeader, 10);
        GridPane.setRowSpan(imageHeader, 2);

        gridPane.add(imageHeader, 0, 0);

        GridPane.setRowIndex(wpmChart, 2);
        GridPane.setColumnIndex(wpmChart, 1);
        GridPane.setColumnSpan(wpmChart, 8);
        GridPane.setRowSpan(wpmChart, 6);

        wpmLabel = new Label("WPM:");
        wpmLabel.getStyleClass().add("wpmLabel");
        wpmLabel.setAlignment(Pos.CENTER);
        wpmLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Ensure it expands to fill the cell
        GridPane.setConstraints(wpmLabel, 0, 3); // Position in the grid

        wpmResultLabel = new Label("25");
        wpmResultLabel.getStyleClass().add("wpmResultLabel");
        wpmResultLabel.setAlignment(Pos.CENTER);
        wpmResultLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Ensure it expands to fill the cell
        GridPane.setConstraints(wpmResultLabel, 0, 4); // Position in the grid

        accLabel = new Label("Accuracy:");
        accLabel.getStyleClass().add("accuracyLabel");
        accLabel.setAlignment(Pos.CENTER);
        accLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Ensure it expands to fill the cell
        GridPane.setConstraints(accLabel, 0, 5); // Position in the grid

        accResultLabel = new Label("25");
        accResultLabel.getStyleClass().add("accuracyResultLabel");
        accResultLabel.setAlignment(Pos.CENTER);
        accResultLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // Ensure it expands to fill the cell
        GridPane.setConstraints(accResultLabel, 0, 6); // Position in the grid

        gridPane.getChildren().addAll(
                wpmLabel, wpmResultLabel,
                accLabel, accResultLabel
        );

        correctLabel = new Label("CORRECT:");
        correctLabel.getStyleClass().add("correctLabel");
        correctResult = new Label(String.valueOf(gameModel.getTypingTextModel().getCorrectCharsCount()));

        incorrectLabel = new Label("INCORRECT:");
        incorrectLabel.getStyleClass().add("incorrectLabel");
        incorrectResult = new Label(String.valueOf(gameModel.getTypingTextModel().getIncorrectCharsCount()));

        extrasLabel = new Label("EXTRAS:");
        extrasLabel.getStyleClass().add("extrasLabel");
        extrasResult = new Label(String.valueOf(gameModel.getTypingTextModel().getExtraCharsCount()));

        missedLabel = new Label("MISSED:");
        missedLabel.getStyleClass().add("missedLabel");
        missedResult = new Label(String.valueOf(gameModel.getTypingTextModel().getMissedCharsCount()));

        gridPane.add(correctLabel, 2, 8);
        gridPane.add(correctResult, 2, 9);

        gridPane.add(incorrectLabel, 4, 8);
        gridPane.add(incorrectResult, 4, 9);

        gridPane.add(extrasLabel, 6, 8);
        gridPane.add(extrasResult, 6, 9);

        gridPane.add(missedLabel, 8, 8);
        gridPane.add(missedResult, 8, 9);

        return gridPane;
    }


    public LineChart<Number, Number> createChart() {
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();

        yAxis.setLabel("Words per Minute (WPM)");

        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series<Number, Number> actualWpmSeries = new XYChart.Series<>();
        actualWpmSeries.setName("Actual WPM");
        XYChart.Series<Number, Number> averageWpmSeries = new XYChart.Series<>();
        averageWpmSeries.setName("Average WPM");

        lineChart.setLegendSide(Side.RIGHT); // Ustawienie legendy po prawej stronie wykresu

        // Debugging: log the data points
        System.out.println("Actual WPM Data Points:");
        wordsPerSecond.forEach((second, wpm) -> {
            System.out.println("Second: " + second + ", WPM: " + wpm);
            if (second >= 0) {
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

        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true);
        lineChart.getData().clear();
        lineChart.getData().addAll(actualWpmSeries, averageWpmSeries);
        lineChart.setLegendVisible(false);

        return lineChart;
    }

    public void updateChart() {
        if (wpmChart == null) {
            wpmChart = createChart();
        } else {
            root.getChildren().remove(wpmChart);
            wpmChart.getData().clear();
        }
        populateChartData();
        root.getChildren().add(wpmChart);
    }

    public void updateResults() {
        correctResult.setText(String.valueOf(gameModel.getTypingTextModel().getCorrectCharsCount()));

        incorrectResult.setText(String.valueOf(gameModel.getTypingTextModel().getIncorrectCharsCount()));

        extrasResult.setText(String.valueOf(gameModel.getTypingTextModel().getExtraCharsCount()));

        missedResult.setText(String.valueOf(gameModel.getTypingTextModel().getMissedCharsCount()));


        int gameTimeInSeconds = (int) (gameModel.getTime() / 1000);

        int totalWpm = wordsPerSecond.size() * 60 / gameTimeInSeconds;


        wpmResultLabel.setText(String.valueOf(totalWpm));

        double accuracyPercentage = ((double) gameModel.getTypingTextModel().getCorrectCharsCount() / (gameModel.getTypingTextModel().getCorrectCharsCount() +
                gameModel.getTypingTextModel().getIncorrectCharsCount() +
                gameModel.getTypingTextModel().getExtraCharsCount() +
                gameModel.getTypingTextModel().getMissedCharsCount())) * 100;

        String formattedPercentage = String.format("%.2f", accuracyPercentage);
        accResultLabel.setText(formattedPercentage + "%");

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
