package com.dot.mathe.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RowData {
    @PrimaryKey
    public int id;
    public String data;

    public RowData(int id, String data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return data;
    }

}
