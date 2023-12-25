package TypingText;

import java.util.LinkedList;
import java.util.List;

public class WordNode {

    private LinkedList<CharacterNode> characterNodeLinkedList;

    public WordNode() {
        this.characterNodeLinkedList = new LinkedList<>();
    }

    public void addCharacter(char character) {
        CharacterNode newCharNode = new CharacterNode(character);
        characterNodeLinkedList.add(newCharNode);
    }

    // Method to add extra characters that are not part of the original word
    public void addExtraCharacter(char character) {
        CharacterNode extraCharNode = new CharacterNode(character);
        // You might want to set a different status for the extra character to distinguish it
        extraCharNode.setStatus(TypingTextModel.CharacterStatus.EXTRA);
        characterNodeLinkedList.add(extraCharNode);
    }

    public List<CharacterNode> getCharacterNodes() {
        return characterNodeLinkedList;
    }
}
