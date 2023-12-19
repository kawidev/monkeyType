package Language;

import util.Observable;
import util.Observer;

import java.util.ArrayList;
import java.util.List;

public class LanguageModel extends Observable {

    private List<Observer> observers = new ArrayList<>();

    private DictionaryService dictionaryService;



    private List<String> availableLanguages;

    private String currentLanguage;

    public LanguageModel() {
        this.dictionaryService = new DictionaryService();
        this.availableLanguages = dictionaryService.getAvailableLanguages();
        this.currentLanguage = "polish";

        /*Model jezykowy wczytuje liste dostepnych jezykow
        * Komponent widoku odpowiedzialny za widok menu jezykowego wraz z dostepnymi
        * opcjami będzie obserwował pole modelu*/
        for(String lang : availableLanguages) {
            System.out.println(lang);
        }
        System.out.println("konsola");
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
