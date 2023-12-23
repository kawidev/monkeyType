package TypingText;

import util.Observable;
import util.Observer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TypingTextModel extends Observable implements Observer<List<String>> {

    private LinkedList<WordNode> originalText;
    private int currentWordIndex, currentCharIndex;


    public TypingTextModel() {
        this.originalText = new LinkedList<>();
    }

    // Notifies all observers with the updated character statuses
    @Override
    protected void notifyObserver() {
        for (Observer observer : observers) {
            observer.update(originalText);
        }
    }

    // Updates the original text and resets the character statuses
    @Override
    public void update(List<String> newWords) {
        this.originalText.clear();

        currentWordIndex = 0;
        currentCharIndex = 0;

        for(String word : newWords) {
            WordNode newWordNode = new WordNode();
            for(Character charInWord : word.toCharArray()) {
                newWordNode.addCharacter(charInWord);
            }
            this.originalText.add(newWordNode);
        }
        notifyObserver();
    }

    public void processInput(String newCharacter) {
        if (currentWordIndex >= 0) {
            WordNode currentWord = originalText.get(currentWordIndex);

            if (currentCharIndex >= currentWord.getCharacterNodes().size()) {
                // Check if the entered character is a space and there is a next word
                if (newCharacter.equals(" ") && currentWordIndex < originalText.size() - 1) {
                    // Proceed to the next word
                    currentWordIndex++;
                    currentCharIndex = 0;
                } else {
                    // Add a new CharacterNode with status EXTRA to the current word
                    currentWord.addCharacter(newCharacter.charAt(0));
                    currentCharIndex++; // Increment the index
                }
            } else {
                // Check if the entered character is correct
                CharacterNode currentCharNode = currentWord.getCharacterNodes().get(currentCharIndex);
                char expectedChar = currentCharNode.getCharacter();
                if (newCharacter.charAt(0) == expectedChar) {
                    currentCharNode.setStatus(TypingTextModel.CharacterStatus.CORRECT);
                } else {
                    currentCharNode.setStatus(TypingTextModel.CharacterStatus.INCORRECT);
                }
                currentCharIndex++;
            }

            notifyObserver();
        }
    }









    public enum CharacterStatus {
        CORRECT, INCORRECT, EXTRA, MISSING, NOT_TYPED, SKIPPED;
    }
}