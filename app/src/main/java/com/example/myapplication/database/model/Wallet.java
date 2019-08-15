package com.example.myapplication.database.model;

import java.sql.Timestamp;

public class Wallet {

    private Integer id;
    private String name;
    private Integer amount;
    private Timestamp updateTs;

    public static final String TABLE_NAME = "wallets";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_UPDATE_TS = "updateTS";
    public static final String COLUMN_ACTIVE = "active";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_AMOUNT + " INTEGER, " + COLUMN_UPDATE_TS + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " + COLUMN_ACTIVE + " INTEGER DEFAULT 1)";

    public Wallet() {}

    public Wallet(String name, Integer amount) {
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

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Timestamp getUpdateTs() {
        return updateTs;
    }

    public void setUpdateTs(Timestamp updateTs) {
        this.updateTs = updateTs;
    }

    public String toString() { return name; }

}
