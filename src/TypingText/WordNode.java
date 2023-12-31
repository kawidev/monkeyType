package TypingText;

import java.util.LinkedList;
import java.util.List;

public class WordNode {

    private final LinkedList<CharacterNode> characterNodeLinkedList;

    public WordNode() {
        this.characterNodeLinkedList = new LinkedList<>();
    }

    public void addCharacter(char character) {
        CharacterNode newCharNode = new CharacterNode(character);
        characterNodeLinkedList.add(newCharNode);
    }

    public void addExtraCharacter(char character) {
        CharacterNode extraCharNode = new CharacterNode(character);

        extraCharNode.setStatus(TypingTextModel.CharacterStatus.EXTRA);
        characterNodeLinkedList.add(extraCharNode);
    }

    public List<CharacterNode> getCharacterNodes() {
        return characterNodeLinkedList;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (CharacterNode characterNode : characterNodeLinkedList) {
            sb.append(characterNode.getCharacter());
        }

        return sb.toString();
    }
}
