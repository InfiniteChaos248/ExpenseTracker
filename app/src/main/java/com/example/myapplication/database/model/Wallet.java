package com.example.myapplication.database.model;

import java.sql.Timestamp;

public class Wallet {

    private Integer id;
    private String name;
    private Float amount;
    private Boolean active;

    public static final String TABLE_NAME = "wallets";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_ACTIVE = "active";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_AMOUNT + " REAL, " + COLUMN_ACTIVE + " INTEGER DEFAULT 1)";

    public Wallet() {}

    public Wallet(String name, Float amount) {
        this.name = name;
        this.amount = amount;
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

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public String toString() { return name; }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
