package util;

import TypingText.TypingTextView;

import java.util.List;

public interface Observer<T> {
    void update(T object);
}
