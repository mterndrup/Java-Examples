package com.parsers;

import java.io.Serializable;

public class Item implements Serializable {

    private String title;
    private String description;

    public Item() {
        setTitle(null);
        setDescription(null);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
