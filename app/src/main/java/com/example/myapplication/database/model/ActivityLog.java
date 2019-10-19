package com.example.myapplication.database.model;

public class ActivityLog {

    private String logDate;
    private String logTime;
    private Float amount;
    private Integer category;
    private Integer categoryS;
    private Integer type;
    private Integer wallet;
    private Integer walletS;
    private String comments;

    public static final String TABLE_NAME = "logs";
    public static final String COLUMN_LOG_DATE = "log_date";
    public static final String COLUMN_LOG_TIME = "log_time";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_NEW = "new";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_WALLET_1 = "wallet1";
    public static final String COLUMN_WALLET_2 = "wallet2";
    public static final String COLUMN_COMMENTS = "comments";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_LOG_DATE + " TEXT, " +
            COLUMN_LOG_TIME + " TEXT, " +
            COLUMN_AMOUNT + " REAL, " +
            COLUMN_CATEGORY + " INTEGER, " +
            COLUMN_NEW + " TEXT, " +
            COLUMN_TYPE + " INTEGER, " +
            COLUMN_WALLET_1 + " INTEGER, " +
            COLUMN_WALLET_2 + " INTEGER, " +
            COLUMN_COMMENTS + " TEXT" +
            ")";

    public ActivityLog() {}

    public String getLogDate() {
        return logDate;
    }

    public void setLogDate(String logDate) {
        this.logDate = logDate;
    }

    public String getLogTime() {
        return logTime;
    }

    public void setLogTime(String logTime) {
        this.logTime = logTime;
    }

    public Float getAmount() {
        return amount;
    }

    public void setAmount(Float amount) {
        this.amount = amount;
    }

    public Integer getCategory() {
        return category;
    }

    public void setCategory(Integer category) {
        this.category = category;
    }

    public Integer getCategoryS() {
        return categoryS;
    }

    public void setCategoryS(Integer categoryS) {
        this.categoryS = categoryS;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public Integer getWalletS() {
        return walletS;
    }

    public void setWalletS(Integer walletS) {
        this.walletS = walletS;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
}
