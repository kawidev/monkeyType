package Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class DictionaryCache {
    private static final int WORDS_COUNT = 50;
    private Map<String, List<String>> cache = new HashMap<>();

    public List<String> getWords(String language) {
        // Sprawdź, czy słownik dla danego języka jest w pamięci podręcznej
        if (!cache.containsKey(language)) {
            // Jeśli nie, wczytaj go i dodaj do cache
            List<String> allWordsFromDict;
            try {
                // Użyj ClassLoadera do wczytania zasobów
                InputStream is = getClass().getClassLoader().getResourceAsStream("dictionary/" + language.toLowerCase() + ".txt");
                if (is == null) {
                    throw new IOException("Resource not found: " + "dictionary/" + language.toLowerCase() + ".txt");
                }
                allWordsFromDict = new BufferedReader(new InputStreamReader(is))
                        .lines().collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Collections.shuffle(allWordsFromDict, new Random());
            cache.put(language, allWordsFromDict);
        }
        // Zwróć mutowalną kopię listy słów
        return new ArrayList<>(cache.get(language).stream().limit(WORDS_COUNT).collect(Collectors.toList()));
    }

}
