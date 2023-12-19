package Time;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TimeModel {
    private List<Integer> availableTimes;

    public TimeModel() {
        this.availableTimes = new ArrayList<>();
        loadTimes();
    }

    private void loadTimes() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("times.txt");
            if (is == null) {
                throw new IOException("File times.txt not found");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                availableTimes = reader.lines()
                        .flatMap(line -> Arrays.stream(line.split(",")))
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .sorted()
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, possibly logging it and providing a fallback
        }
    }

    public List<Integer> getAvailableTimes() {
        return availableTimes;
    }
}
