package TypingText;

import util.Observable;
import util.Observer;

import java.util.ArrayList;
import java.util.List;

public class TypingTextModel extends Observable implements Observer<List<String>> {

    private String originalText; // The full text to be typed
    private List<CharacterWithStatus> characterStatuses; // List of statuses for each character

    public TypingTextModel() {
        this.characterStatuses = new ArrayList<>();
    }

    // Notifies all observers with the updated character statuses
    @Override
    protected void notifyObserver() {
        for (Observer observer : observers) {
            observer.update(this.characterStatuses);
        }
    }

    // Updates the original text and resets the character statuses
    @Override
    public void update(List<String> newWords) {
        this.originalText = String.join(" ", newWords);
        this.characterStatuses.clear();
        for (char c : originalText.toCharArray()) {
            this.characterStatuses.add(new CharacterWithStatus(c, CharacterStatus.NOT_TYPED));
        }
        notifyObserver();
    }

    // Process the current input from the user
    public void processInput(String currentInput) {
        resetCharacterStatuses();
        for (int i = 0; i < currentInput.length(); i++) {
            if (i < originalText.length()) {
                CharacterStatus status = currentInput.charAt(i) == originalText.charAt(i) ? CharacterStatus.CORRECT : CharacterStatus.INCORRECT;
                characterStatuses.set(i, new CharacterWithStatus(originalText.charAt(i), status));
            } else {
                characterStatuses.add(new CharacterWithStatus(currentInput.charAt(i), CharacterStatus.EXTRA));
            }
        }
        notifyObserver();
    }

    // Resets the character statuses to NOT_TYPED
    private void resetCharacterStatuses() {
        for (int i = 0; i < characterStatuses.size(); i++) {
            characterStatuses.get(i).setStatus(CharacterStatus.NOT_TYPED);
        }
    }

    // Inner class to pair a character with its typing status
    public static class CharacterWithStatus {
        private final char character;
        private CharacterStatus status; // Made non-final to allow reset

        public CharacterWithStatus(char character, CharacterStatus status) {
            this.character = character;
            this.status = status;
        }

        public char getCharacter() {
            return character;
        }

        public CharacterStatus getStatus() {
            return status;
        }

        public void setStatus(CharacterStatus status) {
            this.status = status; // Setter method to allow status updates
        }
    }

    // Enum to represent the status of a character as typed by the user
    public enum CharacterStatus {
        CORRECT, INCORRECT, EXTRA, MISSING, NOT_TYPED
    }
}
