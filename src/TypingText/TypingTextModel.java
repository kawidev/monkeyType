package TypingText;

import util.Observable;
import util.Observer;

import java.util.LinkedList;
import java.util.List;

public class TypingTextModel extends Observable implements Observer<List<String>> {

    private LinkedList<WordNode> originalText;
    private int currentWordIndex, currentCharIndex;

    private int correctCharsCounter, incorrectCharsCounter, extraCharsCounter, missedCharsCounter, typedWordsCounter;

    public TypingTextModel() {
        this.originalText = new LinkedList<>();
        this.correctCharsCounter = 0;
        this.incorrectCharsCounter = 0;
        this.extraCharsCounter = 0;
        this.missedCharsCounter = 0;
        this.typedWordsCounter = 0;
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
        if (currentWordIndex >= 0 && newCharacter != null && !newCharacter.isEmpty()) {
            WordNode currentWord = originalText.get(currentWordIndex);
            List<CharacterNode> characterNodes = currentWord.getCharacterNodes();

            if (newCharacter.equals(" ")) {
                for (int i = currentCharIndex; i < characterNodes.size(); i++) {
                    if (characterNodes.get(i).getStatus() == TypingTextModel.CharacterStatus.NOT_TYPED) {
                        characterNodes.get(i).setStatus(TypingTextModel.CharacterStatus.MISSED);
                        missedCharsCounter++;
                    }
                }

                for (int i = characterNodes.size(); i < currentCharIndex; i++) {
                    currentWord.addExtraCharacter(' '); // Space is not an extra character, so don't count it
                }

                if (currentWordIndex < originalText.size() - 1) {
                    currentWordIndex++;
                    typedWordsCounter++;
                }
                currentCharIndex = 0;
            } else {
                if (currentCharIndex < characterNodes.size()) {
                    CharacterNode currentCharNode = characterNodes.get(currentCharIndex);
                    char inputChar = newCharacter.charAt(0);
                    char expectedChar = currentCharNode.getCharacter();

                    if (inputChar == expectedChar) {
                        currentCharNode.setStatus(TypingTextModel.CharacterStatus.CORRECT);
                        correctCharsCounter++;
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
                            missedCharsCounter += (foundIndex - currentCharIndex);
                            for (int j = currentCharIndex; j < foundIndex; j++) {
                                characterNodes.get(j).setStatus(TypingTextModel.CharacterStatus.MISSED);
                            }
                            characterNodes.get(foundIndex).setStatus(TypingTextModel.CharacterStatus.CORRECT);
                            // Set currentCharIndex to foundIndex, as it will be incremented after exiting this block
                            currentCharIndex = foundIndex;
                            correctCharsCounter++;
                        } else {
                            currentCharNode.setStatus(TypingTextModel.CharacterStatus.INCORRECT);
                            incorrectCharsCounter++;
                        }
                    }
                } else {
                    currentWord.addExtraCharacter(newCharacter.charAt(0));
                    extraCharsCounter++;
                }
                currentCharIndex++;
            }
            notifyObserver();
        }
    }



    // Gettery dla statystyk
    public int getCorrectCharsCount() {
        return correctCharsCounter;
    }

    public int getIncorrectCharsCount() {
        return incorrectCharsCounter;
    }

    public int getExtraCharsCount() {
        return extraCharsCounter;
    }

    public int getMissedCharsCount() {
        return missedCharsCounter;
    }

    public int getWordsCount() {
        return typedWordsCounter;
    }
    public enum CharacterStatus {
        CORRECT, INCORRECT, EXTRA, MISSING, NOT_TYPED, MISSED;
    }
}