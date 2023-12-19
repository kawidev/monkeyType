package util;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable {

    protected List<Observer> observers;

    public Observable() {
        this.observers = new ArrayList<>();
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }
    public void unregisterObserver(Observer observer) {
        if(observers.contains(observer)) {
            observers.remove(observer);
        }
    }

    protected abstract void notifyObserver();
}
