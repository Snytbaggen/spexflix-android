package se.liss.spexflix.data;

import androidx.annotation.NonNull;

public enum ShowType {
    SHOW("Föreställning"),
    EXTRA("Extramaterial"),
    OTHER("Övrigt");

    private String name;

    ShowType(String name){
        this.name = name;
    }

    @NonNull
    public String toString() {
        return name;
    }
}
