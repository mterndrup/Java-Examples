package com.parsers;

import java.io.Serializable;

public class Channel implements Serializable {

    private Items items;
    private String title;
    private String link;
    private String description;
    private String lastBuildDate;
    private String language;

    public Channel() {
        setItems(null);
        setTitle(null);
        // set every field to null in the constructor
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public Items getItems() {
        return items;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
    // rest of the class looks similar so just setters and getters
}