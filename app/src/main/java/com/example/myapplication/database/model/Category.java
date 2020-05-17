package com.example.myapplication.database.model;

import java.sql.Timestamp;

public class Category {

    private Integer id;
    private String name;
    private Integer type;
    private Boolean active;

    public static final String TABLE_NAME = "categories";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_ACTIVE = "active";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_TYPE + " INTEGER, "  + COLUMN_ACTIVE + " INTEGER DEFAULT 1)";

    public Category() {}

    public Category(String name, Integer type) {
        this.name = name;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String toString() { return name; }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
