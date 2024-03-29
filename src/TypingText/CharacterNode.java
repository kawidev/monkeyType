package TypingText;
import TypingText.TypingTextModel.CharacterStatus;

class CharacterNode {
    char character;
    CharacterStatus status;
    private CharacterNode next;

    public CharacterNode(char character) {
        this.character = character;
        this.status = CharacterStatus.NOT_TYPED;
        this.next = null;
    }

    public CharacterNode(char character, CharacterStatus status) {
        this.character = character;
        this.status = status;
    }

    public char getCharacter() {
        return character;
    }

    public CharacterStatus getStatus() {
        return status;
    }

    public void setStatus(CharacterStatus characterStatus) {
        this.status = characterStatus;
    }
}