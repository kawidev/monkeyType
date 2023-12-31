package Game;

import Language.DictionaryCache;
import Language.LanguageModel;
import Time.TimeModel;
import TypingText.TypingTextModel;
import javafx.application.Platform;
import javafx.concurrent.Task;
import util.Observable;
import util.Observer;

import java.util.*;

public class GameModel extends Observable implements Observer<Integer> {


    private final Object pauseLock = new Object();
    private long totalPauseDuration = 0;
    private long pauseStartTime;

    private TypingTestApp typingTestApp;

    private GameView gameView;
    private Boolean isGameStarted;
    private Boolean playAgain;
    private volatile boolean isGamePaused;

    private final LanguageModel language;
    private final TimeModel timeModel;
    private final TypingTextModel typingTextModel;
    private List<String> currentWords;
    private long time;
    private volatile long gameStartTime;
    private int leftTime;

    private Thread gameThread;

    DictionaryCache dictionaryCache;
    private final Map<Integer, Integer> wordsPerSecond = new HashMap<>();
    private final Map<Integer, Double> averageWpmPerSecond = new HashMap<>();
    private final Map<String, Integer> wordAndWPM = new LinkedHashMap<>();
    private String lastTypedWord;

    private final SerializationClass serializationClass;

    private boolean isLanguageChosen, isTimeChosen;

    public GameModel() {

        this.language = new LanguageModel();
        this.timeModel = new TimeModel();
        this.typingTextModel = new TypingTextModel(this);
        typingTextModel.registerObserver(this);

        this.currentWords = new ArrayList<>();
        this.dictionaryCache = new DictionaryCache();

        this.serializationClass = new SerializationClass(this);

        this.time = 0;
        this.isLanguageChosen = false;
        this.isTimeChosen = false;

        this.isGameStarted = false;
        this.isGamePaused = false;
        this.playAgain = true;
        this.lastTypedWord = null;
    }

    public void startGame() {
        if (!isGameStarted && playAgain && isTimeChosen && isLanguageChosen) {
            isGameStarted = true;
            playAgain = false;
            totalPauseDuration = 0;
            gameStartTime = System.currentTimeMillis();
            currentWords = dictionaryCache.getWords(getCurrentLanguage());
            notifyObserver();
            gameView.notifyAboutStartGameToShowTimeLabel();

            startCountdownTask(time);
        }
    }


    public void pauseGame() {
        if (!isGamePaused && isGameStarted) {
            isGamePaused = true;
            pauseStartTime = System.currentTimeMillis();
            gameView.getTypingTextView().getInputField().setDisable(true);
        }
    }

    public void resumeGame() {
        if (isGamePaused && isGameStarted) {
            totalPauseDuration += System.currentTimeMillis() - pauseStartTime;
            isGamePaused = false;
            gameView.getTypingTextView().getInputField().setDisable(false);
            synchronized (pauseLock) {
                pauseLock.notifyAll();
            }
        }
    }

    private void startCountdownTask(long totalTime) {
        Task<Void> gameTask = new Task<>() {
            @Override
            protected Void call() {
                while (!isCancelled()) {
                    if (isGamePaused) {
                        synchronized (pauseLock) {
                            try {
                                pauseLock.wait();
                            } catch (InterruptedException e) {
                                if (isCancelled()) {
                                    break;
                                }
                            }
                        }
                    } else {
                        long currentTime = System.currentTimeMillis();
                        long timePassed = currentTime - gameStartTime - totalPauseDuration;
                        leftTime = (int) ((totalTime - timePassed) / 1000);
                        if (leftTime <= 0) {
                            break;
                        }
                        updateMessage(String.valueOf(leftTime)); // Aktualizacja messageProperty
                        try {
                            Thread.sleep(1000); // Wait for one second
                        } catch (InterruptedException e) {
                            if (isCancelled()) {
                                break;
                            }
                        }
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                endGame();
            }
        };

        gameTask.messageProperty().addListener((obs, oldMessage, newMessage) -> Platform.runLater(() -> gameView.updateTimeLeft(Integer.parseInt(newMessage))));

        gameThread = new Thread(gameTask);
        gameThread.setDaemon(true);
        gameThread.start();
    }

    public void restartGameOrPlayAgain() {
            isGameStarted = false;
            isGamePaused = false;
            playAgain = true;

            // Reset language and time selections
            isLanguageChosen = false;
            isTimeChosen = false;

            // Reset counters and indexes
            totalPauseDuration = 0;
            leftTime = 0;
            gameStartTime = 0;
            pauseStartTime = 0;

            wordsPerSecond.clear();
            wordAndWPM.clear();
            averageWpmPerSecond.clear();

            // Reset counters in TypingTextModel
            typingTextModel.reset();

            // Notify view to reset language and time selections in ComboBoxes
            gameView.getLanguageMenuView().getLanguageComboBox().setValue(null);
            gameView.getTimeViewComponent().getTimeComboBox().setValue(null);


            if (gameThread != null && gameThread.isAlive()) {
                gameThread.interrupt();
                gameThread = null;
            }
            gameView.notifyAboutPlayAgainOrEndGame();
            notifyObserver();
        }

    public void endGame() {
        if (isGameStarted) {
            isGameStarted = false;
            if (gameThread != null) {
                gameThread.interrupt();
                gameThread = null;
            }

            serializationClass.save();
            printStatisticsTable();

            notifySceneClass();
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
        }
    }

    public void notifySceneClass() {
        typingTestApp.update(true);
    }

    @Override
    protected void notifyObserver() {
        for(Observer observer : observers) {
            if(observer instanceof TypingTextModel) {
                if(isLanguageChosen)
                    typingTextModel.update(currentWords);
                else {
                    String message = "To start a game you have to choose language and time and then press any KEY";
                    List<String> messageWords = Arrays.asList(message.split(" "));
                    typingTextModel.update(messageWords);
                }

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
        if (newVal == null) {
            return;
        }
        this.time = newVal * 1000L;
        System.out.printf("czas ustawiony na: %d", time / 1000);
        this.isTimeChosen = true;
    }

    @Override
    public synchronized void update(Integer wordsCount) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - gameStartTime;
        int second = Math.max(1, (int) (elapsedTime / 1000));


        int actualWpm = wordsCount * 60 / second;

        if(lastTypedWord != null) {
            wordAndWPM.put(lastTypedWord, actualWpm);
            System.out.printf("Dodano slowo: %s\n", lastTypedWord);
        }
        lastTypedWord = null;

        if (second <= time / 1000) {
            wordsPerSecond.put(second, actualWpm);

            int totalWpm = wordsPerSecond.values().stream().mapToInt(Integer::intValue).sum();

            double averageWpm = (double) totalWpm / second;
            averageWpmPerSecond.put(second, averageWpm);
        }

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
        System.out.printf("UpdateLastTypedWord: %s\n", lastTypedWord);
    }

    public void setMain(TypingTestApp typingTestApp) {
        this.typingTestApp = typingTestApp;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }

    public boolean isTimeChosen() {
        return isTimeChosen;
    }

    public boolean isLanguageChosen() {
        return isLanguageChosen;
    }

    public void setLanguageChosen(boolean state) {
        isLanguageChosen = state;
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }

    public void setGameView(GameView gameView) {
        this.gameView = gameView;
    }

    public long getTime() {
        return time;
    }
}
