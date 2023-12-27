package Game;

import Language.DictionaryCache;
import Language.LanguageModel;
import Time.TimeModel;
import TypingText.TypingTextModel;
import TypingText.TypingTextView;
import javafx.concurrent.Task;
import util.Observable;
import util.Observer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameModel extends Observable implements Observer<Integer> {


    private TypingTestApp typingTestApp;
    private Boolean isGameStarted, playAgain, isGameFinish;

    private final LanguageModel language;
    private final TimeModel timeModel;
    private final TypingTextModel typingTextModel;
    private List<String> currentWords;
    private long time;
    private volatile long gameStartTime;

    private Thread gameThread;

    DictionaryCache dictionaryCache;
    private final Map<Integer, Integer> wordsPerSecond = new HashMap<>();
    private final Map<Integer, Double> averageWpmPerSecond = new HashMap<>();
    private final Map<String, Integer> wordAndWPM = new HashMap<>();
    private String lastTypedWord;

    private final SerializationClass serializationClass;

    public GameModel() {

        this.language = new LanguageModel();
        this.timeModel = new TimeModel();
        this.typingTextModel = new TypingTextModel();
        typingTextModel.registerObserver(this);

        this.currentWords = new ArrayList<>();
        this.dictionaryCache = new DictionaryCache();

        this.serializationClass = new SerializationClass(this);

        this.isGameStarted = false;
        this.isGameFinish = false;
        this.playAgain = true;
        this.lastTypedWord = null;
    }

    public void startGame() {
        if (!isGameStarted && playAgain && time > 0) {
            isGameStarted = true;
            playAgain = false;
            System.out.println("Gra rozpoczeta");
            currentWords = dictionaryCache.getWords(getCurrentLanguage());
            notifyObserver();

            this.gameStartTime = System.currentTimeMillis();

            Task<Void> gameTask = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    long endTime = System.currentTimeMillis() + time;
                    while (System.currentTimeMillis() < endTime) {
                        Thread.sleep(1000); // czekaj sekundę
                        long timeLeft = (endTime - System.currentTimeMillis()) / 1000;
                        updateMessage("Pozostały czas: " + timeLeft + " sekund");
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    super.succeeded();
                    endGame();
                }
            };

            // Uruchom zadanie w nowym wątku
            gameThread = new Thread(gameTask);
            gameThread.setDaemon(true); // Ustaw jako wątek demoniczny
            gameThread.start();

            // Ustaw właściwość message zadania do aktualizacji interfejsu użytkownika z pozostałym czasem
            gameTask.messageProperty().addListener((obs, oldMessage, newMessage) -> {
                // Aktualizuj UI tutaj za pomocą newMessage
            });

            System.out.println("Gra rozpoczęta. Masz " + (time / 1000) + " sekund.");
        }
    }

    public void endGame() {
        if (isGameStarted) {
            isGameStarted = false;
            isGameFinish = true;
            if (gameThread != null) {
                gameThread.interrupt();
                gameThread = null;
            }

            serializationClass.save();
            printStatisticsTable();

            notifySceneClass();
            // Zbierz statystyki
            int wordsWritten = typingTextModel.getWordsCount();
            int correctChars = typingTextModel.getCorrectCharsCount();
            int incorrectChars = typingTextModel.getIncorrectCharsCount();
            int extraChars = typingTextModel.getExtraCharsCount();
            int missedChars = typingTextModel.getMissedCharsCount();



            System.out.println("Gra zakończona.");
            System.out.println("Napisane słowa: " + wordsWritten);
            System.out.println("Poprawne znaki: " + correctChars);
            System.out.println("Niepoprawne znaki: " + incorrectChars);
            System.out.println("Dodatkowe znaki: " + extraChars);
            System.out.println("Pominiete znaki: " + missedChars);

            // Tutaj możesz wywołać logikę, która pokaże wyniki użytkownikowi
            // Może to być wywołanie metody z kontrolera, który aktualizuje widok.
        }
    }

    public void notifySceneClass() {
        typingTestApp.update(true);
    }

    @Override
    protected void notifyObserver() {
        for(Observer observer : observers) {
            if(observer instanceof TypingTextModel) {
                typingTextModel.update(currentWords);
            }
        }

    }

    public void resetTypingModel() {
        typingTextModel.reset();
        notifyObserver();
    }


    public void changeLanguage(String language) {
        this.language.setLanguage(language);
        currentWords = dictionaryCache.getWords(language);
        System.out.println("Zmieniono jezyk na " + language);


        // skoro jezyk zostal zmieniony muszę zaimplementowac logikę związaną z informowaniem komponentów
        // dotyczących pola do wprowadzania tekstu

        notifyObserver();
    }

    public LanguageModel getLanguageModel() {
        return language;
    }

    public TimeModel getTimeModel() {
        return timeModel;
    }

    public TypingTextModel getTypingTextModel() {
        return typingTextModel;
    }

    public void setTime(int time) {
        this.time = time * 1000L;
    }

    public String getCurrentLanguage() {
        return language.getCurrentLanguage();
    }

    public void setTimeLimit(Integer newVal) {
        this.time = newVal * 1000L;
        System.out.printf("czas ustawiony na: %d", time / 1000);
    }

    @Override
    public synchronized void update(Integer wordsCount) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - gameStartTime;
        int second = (int) (elapsedTime / 1000);

        if (second <= 0) {
            // Avoid division by zero or negative seconds
            System.err.println("Invalid second encountered: " + second);
            return;
        }

        // Calculate the actual WPM for the last second
        int actualWpm = (wordsCount * 60) / second;  // Assuming wordsCount is the number of words in the last second

        // Update the last word's WPM if it's not 0
        if(lastTypedWord != null && actualWpm != 0) {
            wordAndWPM.put(lastTypedWord, actualWpm);
        }

        // Update words per second map
        if (second <= time / 1000) {
            wordsPerSecond.put(second, actualWpm);

            // Calculate the total WPM up to the current second
            int totalWpm = wordsPerSecond.values().stream().mapToInt(Integer::intValue).sum();

            // Calculate the average WPM up to the current second
            double averageWpm = (double) totalWpm / second; // Divide by the number of seconds, not the size of the map
            averageWpmPerSecond.put(second, averageWpm);
        } else {
            System.err.println("Invalid second encountered: " + second);
        }

        // Reset the typing model if needed
        if(wordsCount == 30) {
            dictionaryCache.removeWordsFromCache(this.getCurrentLanguage());
            currentWords = dictionaryCache.getWords(this.getCurrentLanguage());
            resetTypingModel();
        }
    }


    public void printStatisticsTable() {


        System.out.println("Czas (sekundy) | WPM na sekundę | Średni WPM na sekundę");
        System.out.println("---------------------------------------------------");

        for (Integer second : wordsPerSecond.keySet()) {
            int wpmPerSecond = wordsPerSecond.getOrDefault(second, 0);
            double averageWpm = averageWpmPerSecond.getOrDefault(second, 0.0);

            System.out.printf("%14d | %13d | %24.2f%n", second, wpmPerSecond, averageWpm);
        }
    }


    public Map<Integer, Integer> getWordsPerSecond() {
        return wordsPerSecond;
    }
    public Map<Integer, Double> getAverageWpmPerSecond() {
        return averageWpmPerSecond;
    }

    public Map<String, Integer> getWordAndWPM() {
        return wordAndWPM;
    }

    public void updateLastTypedWord(String lastTypedWord) {
        this.lastTypedWord = lastTypedWord;
    }

    public void setMain(TypingTestApp typingTestApp) {
        this.typingTestApp = typingTestApp;
    }
}
