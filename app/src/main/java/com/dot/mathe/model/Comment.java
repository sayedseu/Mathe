package com.dot.mathe.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Comment {
    @PrimaryKey
    public int id;
    public String text;

    public Comment(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

}
