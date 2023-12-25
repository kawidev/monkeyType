package Game;

import Language.DictionaryCache;
import Language.LanguageModel;
import Time.TimeModel;
import TypingText.TypingTextModel;
import javafx.concurrent.Task;
import util.Observable;
import util.Observer;

import java.util.ArrayList;
import java.util.List;

public class GameModel extends Observable implements Observer<Integer> {

    private Boolean isGameStarted, playAgain, isGameFinish;

    private LanguageModel language;
    private TimeModel timeModel;
    private TypingTextModel typingTextModel;
    private List<String> currentWords;
    private long time;
    private Thread gameThread;

    DictionaryCache dictionaryCache;

    public GameModel() {

        this.language = new LanguageModel();
        this.timeModel = new TimeModel();
        this.typingTextModel = new TypingTextModel();
        typingTextModel.registerObserver(this);

        this.currentWords = new ArrayList<>();
        this.dictionaryCache = new DictionaryCache();

        this.isGameStarted = false;
        this.isGameFinish = false;
        this.playAgain = true;
    }

    public void startGame() {
        if (!isGameStarted && playAgain && time > 0) {
            isGameStarted = true;
            playAgain = false;
            System.out.println("Gra rozpoczeta");
            currentWords = dictionaryCache.getWords(getCurrentLanguage());
            notifyObserver();

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
        for(Observer observer : observers) {
            observer.update(isGameFinish);
        }
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
    public void update(Integer wordsCount) {
        if(wordsCount == 30 ) {
            dictionaryCache.removeWordsFromCache(this.getCurrentLanguage());
            currentWords = dictionaryCache.getWords(this.getCurrentLanguage());
            resetTypingModel();
        }
    }
}
