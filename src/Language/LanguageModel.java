package Language;

import util.Observable;
import util.Observer;

import java.util.ArrayList;
import java.util.List;

public class LanguageModel extends Observable {

    private final List<Observer> observers = new ArrayList<>();

    private final List<String> availableLanguages;

    private String currentLanguage;

    public LanguageModel() {
        DictionaryService dictionaryService = new DictionaryService();
        this.availableLanguages = dictionaryService.getAvailableLanguages();
        this.currentLanguage = null;
    }

    public void notifyObserver() {
        for (Observer observer : observers) {
            observer.update(availableLanguages);
        }
    }

    public void setLanguage(String language) {
        this.currentLanguage = language;
    }

    public String getCurrentLanguage() {
        return currentLanguage;
    }

    public List<String> getAvailableLanguages() {
        return availableLanguages;
    }
}
