package Language;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DictionaryService {

    public List<String> getAvailableLanguages() {
        try {
            URL url = getClass().getClassLoader().getResource("dictionary");
            if (url == null) {
                throw new IOException("Dictionary directory not found");
            }
            File dir = new File(url.toURI());
            File[] dictionaryFiles = dir.listFiles((d, name) -> name.endsWith(".txt"));

            assert dictionaryFiles != null;
            return Arrays.stream(dictionaryFiles)
                    .map(file -> {
                        String nameWithoutExtension = file.getName().replace(".txt", "");
                        return Character.toUpperCase(nameWithoutExtension.charAt(0)) + nameWithoutExtension.substring(1);
                    })
                    .collect(Collectors.toList());
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("Error loading dictionary files", e);
        }
    }



}
