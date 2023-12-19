package TypingText;

import javafx.scene.text.TextFlow;
import javafx.scene.text.Text;
import util.Observer;

import java.util.ArrayList;
import java.util.List;

public class TypingTextView implements Observer<List<String>> {

    List<String> words;
    private TextFlow textFlow;

    public TypingTextView() {
        this.words = new ArrayList<>();
        this.textFlow = new TextFlow();
    }

    @Override
    public void update(List<String> newWords) {
        words = newWords;
        // Aktualizuj interfejs użytkownika z nowymi słowami
        displayWords();
    }

    private void displayWords() {
        textFlow.getChildren().clear(); // Wyczyść poprzedni tekst
        for (String word : words) {
            Text textWord = new Text(word + " ");
            // Ustawienie stylów dla textWord jeśli to potrzebne
            textFlow.getChildren().add(textWord);
        }
    }

    public TextFlow getTextFlow() {
        return textFlow;
    }
}
