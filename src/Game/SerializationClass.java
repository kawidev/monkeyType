package Game;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class SerializationClass implements Serializable {

    private GameModel gameModel;
    private Map<String, Integer> wordAnd_WPN;


    public SerializationClass(GameModel gameModel) {
        this.gameModel = gameModel;
        this.wordAnd_WPN = new HashMap<>();
        update();
    }

    public void update() {
        wordAnd_WPN = gameModel.getWordAndWPM();
    }

    public void save() {

        update();
        // Format the current date and time to include in the filename
        String pattern = "yyyyMMdd_HHmmss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String date = simpleDateFormat.format(new Date());
        String filename = "GameStats_" + date + ".txt"; // Example: "GameStats_20230130_153045.txt"

        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(filename), "UTF-8"))) {
            for (Map.Entry<String, Integer> entry : wordAnd_WPN.entrySet()) {
                writer.write(entry.getKey() + " -> " + entry.getValue() + "wpm\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
