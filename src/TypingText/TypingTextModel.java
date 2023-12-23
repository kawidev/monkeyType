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
            List<CharacterNode> characterNodes = currentWord.getCharacterNodes();

            if (newCharacter.equals(" ")) {
                for (int i = currentCharIndex; i < currentWord.getCharacterNodes().size(); i++) {
                    currentWord.getCharacterNodes().get(i).setStatus(TypingTextModel.CharacterStatus.MISSED);
                }
                if (currentWordIndex < originalText.size() - 1) {
                    currentWordIndex++;
                    currentCharIndex = 0;
                }
            } else {
                if (currentCharIndex < characterNodes.size()) {
                    CharacterNode currentCharNode = characterNodes.get(currentCharIndex);
                    char inputChar = newCharacter.charAt(0);
                    char expectedChar = currentCharNode.getCharacter();

                    if (inputChar == expectedChar) {
                        currentCharNode.setStatus(TypingTextModel.CharacterStatus.CORRECT);
                        currentCharIndex++;
                    } else {
                        int foundIndex = -1;
                        for (int offset = 1; offset <= 2; offset++) {
                            if (currentCharIndex + offset < characterNodes.size() &&
                                    characterNodes.get(currentCharIndex + offset).getCharacter() == inputChar) {
                                foundIndex = currentCharIndex + offset;
                                break;
                            }
                        }
                        if (foundIndex != -1) {
                            for (int j = currentCharIndex; j < foundIndex; j++) {
                                characterNodes.get(j).setStatus(TypingTextModel.CharacterStatus.MISSED);
                            }
                            characterNodes.get(foundIndex).setStatus(TypingTextModel.CharacterStatus.CORRECT);
                            currentCharIndex = foundIndex + 1;
                        } else {
                            currentCharNode.setStatus(TypingTextModel.CharacterStatus.INCORRECT);
                            currentCharIndex++;
                        }
                    }
                } else {
                    currentWord.addCharacter(newCharacter.charAt(0));
                    currentCharIndex++;
                }
            }
            notifyObserver();
        }
    }
    public enum CharacterStatus {
        CORRECT, INCORRECT, EXTRA, MISSING, NOT_TYPED, MISSED;
    }
}