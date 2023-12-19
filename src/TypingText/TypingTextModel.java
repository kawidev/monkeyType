package TypingText;

import model.GameModel;
import util.Observable;
import util.Observer;

import java.util.ArrayList;
import java.util.List;

public class TypingTextModel extends Observable implements Observer<List<String>> {

    private List<String> words;

    public TypingTextModel() {
        this.words = new ArrayList<>();
    }

    @Override
    protected void notifyObserver() {
        for(Observer observer : observers) {
            observer.update(words);
        }
    }

    @Override
    public void update(List<String> newWords) {
        this.words = newWords;
        notifyObserver();
    }

}
