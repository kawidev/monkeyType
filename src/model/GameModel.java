package model;

import Language.DictionaryCache;
import Language.LanguageModel;
import Time.TimeModel;
import TypingText.TypingTextModel;
import util.Observable;

import java.util.ArrayList;
import java.util.List;

public class GameModel extends Observable {


    private LanguageModel language;
    private TimeModel timeModel;
    private TypingTextModel typingTextModel;
    private List<String> currentWords;
    private long time;

    DictionaryCache dictionaryCache;

    public GameModel() {
        this.language = new LanguageModel();
        this.timeModel = new TimeModel();
        this.typingTextModel = new TypingTextModel();

        this.currentWords = new ArrayList<>();
        this.dictionaryCache = new DictionaryCache();
    }

    @Override
    protected void notifyObserver() {
        typingTextModel.update(currentWords);
    }


    public void changeLanguage(String language) {
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
}
