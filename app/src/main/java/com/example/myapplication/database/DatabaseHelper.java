package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.MainActivity;
import com.example.myapplication.database.model.ActivityLog;
import com.example.myapplication.database.model.Wallet;
import com.example.myapplication.database.model.Category;
import com.example.myapplication.exception.AppException;
import com.example.myapplication.utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "expense_tracker_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.i("DB", "creating " + Wallet.TABLE_NAME + " table");
        sqLiteDatabase.execSQL(Wallet.CREATE_TABLE);
        Log.i("DB", "creating " + Category.TABLE_NAME + " table");
        sqLiteDatabase.execSQL(Category.CREATE_TABLE);
        Log.i("DB", "creating " + ActivityLog.TABLE_NAME + " table");
        sqLiteDatabase.execSQL(ActivityLog.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Wallet.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Category.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ActivityLog.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private ContentValues getLogContentValues(Integer type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        ContentValues values = new ContentValues();
        values.put(ActivityLog.COLUMN_TYPE, type);
        values.put(ActivityLog.COLUMN_LOG_DATE, dateFormat.format(new Date()));
        values.put(ActivityLog.COLUMN_LOG_TIME, timeFormat.format(new Date()));
        return values;
    }

    public void clearDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DELETE FROM " + Wallet.TABLE_NAME + ";");
        db.execSQL("DELETE FROM " + Category.TABLE_NAME + ";");
        db.execSQL("DELETE FROM " + ActivityLog.TABLE_NAME + ";");
        db.execSQL("DELETE FROM SQLITE_SEQUENCE;");
    }

    public String saveDbAsJson() {

        List<Category> categories = getAllCategories(false);
        List<Wallet> wallets = getAllWallets(false);
        List<ActivityLog> logs = fetchLogs(true);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("categories", categories);
        mapping.put("wallets", wallets);
        mapping.put("logs", logs);
        Gson gson = new Gson();
        String json = gson.toJson(mapping);
        return json;

    }

    public void importDatafromJSON(JsonObject json) throws AppException {
        SQLiteDatabase db = this.getWritableDatabase();
        Gson GSON = new Gson();

        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM " + Wallet.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + Category.TABLE_NAME + ";");
            db.execSQL("DELETE FROM " + ActivityLog.TABLE_NAME + ";");
            db.execSQL("DELETE FROM SQLITE_SEQUENCE;");

            JsonArray wallets = json.getAsJsonArray("wallets");
            for (JsonElement walletElement : wallets) {
                Wallet wallet = GSON.fromJson(walletElement, Wallet.class);
                insertWallet(db, wallet);
            }
            JsonArray categories = json.getAsJsonArray("categories");
            for (JsonElement categoryElement : categories) {
                Category category = GSON.fromJson(categoryElement, Category.class);
                insertCategory(db, category);
            }
            JsonArray logs = json.getAsJsonArray("logs");
            for (JsonElement logElement : logs) {
                ActivityLog log = GSON.fromJson(logElement, ActivityLog.class);
                insertLog(db, log);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            // Exception => don't set transaction as successful
            Log.e("DatabaseHelper", e.getMessage(), e);
            throw new AppException(101, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }

    private void insertWallet(SQLiteDatabase db, Wallet wallet) {
        ContentValues values = new ContentValues();
        values.put(Wallet.COLUMN_ID, wallet.getId());
        values.put(Wallet.COLUMN_NAME, wallet.getName());
        values.put(Wallet.COLUMN_AMOUNT, wallet.getAmount());
        values.put(Wallet.COLUMN_ACTIVE, wallet.getActive());
        db.insert(Wallet.TABLE_NAME, null, values);
    }

    private void insertCategory(SQLiteDatabase db, Category category) {
        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_ID, category.getId());
        values.put(Category.COLUMN_NAME, category.getName());
        values.put(Category.COLUMN_TYPE, category.getType());
        values.put(Category.COLUMN_ACTIVE, category.getActive());
        db.insert(Category.TABLE_NAME, null, values);
    }

    private void insertLog(SQLiteDatabase db, ActivityLog log) {
        ContentValues values = new ContentValues();
        values.put(ActivityLog.COLUMN_ID, log.getId());
        values.put(ActivityLog.COLUMN_LOG_DATE, log.getLogDate());
        values.put(ActivityLog.COLUMN_LOG_TIME, log.getLogTime());
        values.put(ActivityLog.COLUMN_AMOUNT, log.getAmount());
        values.put(ActivityLog.COLUMN_CATEGORY, log.getCategory());
        values.put(ActivityLog.COLUMN_NEW, log.getCategoryS());
        values.put(ActivityLog.COLUMN_TYPE, log.getType());
        values.put(ActivityLog.COLUMN_WALLET_1, log.getWallet());
        values.put(ActivityLog.COLUMN_WALLET_2, log.getWalletS());
        values.put(ActivityLog.COLUMN_COMMENTS, log.getComments());
        db.insert(ActivityLog.TABLE_NAME, null, values);
    }

    public List<ActivityLog> fetchLogs(Boolean allTypes) {

        String selection = allTypes ? null : "type in (1,2,3)";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(ActivityLog.TABLE_NAME, new String[]{
                ActivityLog.COLUMN_ID,
                ActivityLog.COLUMN_LOG_DATE,
                ActivityLog.COLUMN_LOG_TIME,
                ActivityLog.COLUMN_AMOUNT,
                ActivityLog.COLUMN_CATEGORY,
                ActivityLog.COLUMN_NEW,
                ActivityLog.COLUMN_TYPE,
                ActivityLog.COLUMN_WALLET_1,
                ActivityLog.COLUMN_WALLET_2,
                ActivityLog.COLUMN_COMMENTS
        }, selection, null, null, null, null);
        cursor.moveToFirst();
        List<ActivityLog> logList = new ArrayList<>();
        ActivityLog log;
        while (!cursor.isAfterLast()) {
            log = new ActivityLog(cursor);
            logList.add(log);
            cursor.moveToNext();
        }
        cursor.close();
        return logList;

    }

    public long insertNewLogForNewWallet(Float amount, Integer walletId, String walletName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_ADD_WALLET);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_NEW, walletName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        return id;

    }

    public long insertNewWallet(String name, Float amount) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Wallet.COLUMN_NAME, name);
        values.put(Wallet.COLUMN_AMOUNT, amount);

        Long id = db.insert(Wallet.TABLE_NAME, null, values);

        insertNewLogForNewWallet(amount, id.intValue(), name);

        return id;

    }

    public long insertNewLogForEditWallet(Integer walletId, String newWalletName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_MODIFY_WALLET);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_NEW, newWalletName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        return id;

    }

    public int updateWalletName(Integer id, String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Wallet.COLUMN_NAME, name);

        int rows = db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(id)});

        insertNewLogForEditWallet(id, name);

        return rows;

    }

    public List<Wallet> getAllWallets(Boolean active) {

        List<Wallet> wallets = new ArrayList<>();

        String activeWalletQuery = "SELECT * FROM " + Wallet.TABLE_NAME + " WHERE " + Wallet.COLUMN_ACTIVE + " = 1 ORDER BY " + Wallet.COLUMN_ID;
        String allWalletQuery = "SELECT * FROM " + Wallet.TABLE_NAME + " ORDER BY " + Wallet.COLUMN_ID;
        String query = active ? activeWalletQuery : allWalletQuery;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Wallet wallet = new Wallet();
                wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
                wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
                wallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
                wallet.setActive(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ACTIVE)) == 1);

                wallets.add(wallet);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return wallets;

    }

    public long insertNewLogForWalletTransfer(Integer logId, Integer walletId1, Integer walletId2, Float amount, String comments, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_WALLET_TRANSFER);
        if (logId != null) {
            values.put(ActivityLog.COLUMN_ID, logId);
        }
        values.put(ActivityLog.COLUMN_WALLET_1, walletId1);
        values.put(ActivityLog.COLUMN_WALLET_2, walletId2);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);
        values.put(ActivityLog.COLUMN_COMMENTS, comments != null ? comments : "");
        if (date != null) {
            values.put(ActivityLog.COLUMN_LOG_DATE, date);
        }
        if (time != null) {
            values.put(ActivityLog.COLUMN_LOG_TIME, time);
        }

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        return id;

    }

    public String walletTransfer(Integer logId, Integer wallet1, Integer wallet2, Float amount, String comments, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet fromWallet = null;
        Wallet toWallet = null;
        ContentValues values;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(wallet1)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            fromWallet = new Wallet();
            fromWallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            fromWallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            fromWallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }
        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(wallet2)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            toWallet = new Wallet();
            toWallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            toWallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            toWallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if (fromWallet == null || toWallet == null) {
            return "Error while fetching wallet information";
        }

        if (amount > fromWallet.getAmount()) {
            return "Insufficient funds";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, fromWallet.getAmount() - amount);

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(wallet1)});

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, toWallet.getAmount() + amount);

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(wallet2)});

        insertNewLogForWalletTransfer(logId, wallet1, wallet2, amount, comments, date, time);

        return "New Wallet Transfer logged successfully";

    }

    public String deleteWalletTransfer(ActivityLog log) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet fromWallet = null;
        Wallet toWallet = null;
        ContentValues values;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWallet())});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            fromWallet = new Wallet();
            fromWallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            fromWallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            fromWallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }
        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWalletS())});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            toWallet = new Wallet();
            toWallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            toWallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            toWallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if (fromWallet == null || toWallet == null) {
            return "Error while fetching wallet information";
        }

        if (log.getAmount() > toWallet.getAmount()) {
            return "Insufficient funds";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, fromWallet.getAmount() + log.getAmount());

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWallet())});

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, toWallet.getAmount() - log.getAmount());

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWalletS())});

        db.delete(ActivityLog.TABLE_NAME, ActivityLog.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getId())});

        return "Wallet Transfer log deleted successfully";

    }

    public long insertNewLogForNewIncome(Integer logId, Integer walletId, Integer categoryId, Float amount, String comments, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (logId != null) {
            values.put(ActivityLog.COLUMN_ID, logId);
        }
        values.put(ActivityLog.COLUMN_TYPE, Constants.LOG_TYPE_NEW_INCOME);
        values.put(ActivityLog.COLUMN_LOG_DATE, date);
        values.put(ActivityLog.COLUMN_LOG_TIME, time);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);
        values.put(ActivityLog.COLUMN_COMMENTS, comments);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        return id;

    }

    public String newIncome(Integer logId, Integer walletId, Integer categoryId, Float amount, String comments, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet wallet = null;
        ContentValues values;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(walletId)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            wallet = new Wallet();
            wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            wallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if (wallet == null) {
            return "Error while retrieving wallet information";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, wallet.getAmount() + amount);

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(walletId)});

        insertNewLogForNewIncome(logId, walletId, categoryId, amount, comments, date, time);

        return "New income logged successfully";

    }

    public String deleteIncome(ActivityLog log) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet wallet = null;
        ContentValues values;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWallet())});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            wallet = new Wallet();
            wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            wallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if (wallet == null) {
            return "Error while retrieving wallet information";
        }

        if (log.getAmount() > wallet.getAmount()) {
            return "Insufficient funds";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, wallet.getAmount() - log.getAmount());

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWallet())});

        db.delete(ActivityLog.TABLE_NAME, ActivityLog.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getId())});

        return "income log deleted successfully";

    }

    public long insertNewLogForNewExpense(Integer logId, Integer walletId, Integer categoryId, Float amount, String comments, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        if (logId != null) {
            values.put(ActivityLog.COLUMN_ID, logId);
        }
        values.put(ActivityLog.COLUMN_TYPE, Constants.LOG_TYPE_NEW_EXPENSE);
        values.put(ActivityLog.COLUMN_LOG_DATE, date);
        values.put(ActivityLog.COLUMN_LOG_TIME, time);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);
        values.put(ActivityLog.COLUMN_COMMENTS, comments);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        return id;

    }

    public String newExpense(Integer logId, Integer walletId, Integer categoryId, Float amount, String comments, String date, String time) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet wallet = null;
        ContentValues values;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(walletId)});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            wallet = new Wallet();
            wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            wallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if (wallet == null) {
            return "Error while retrieving wallet information";
        }

        if (amount > wallet.getAmount()) {
            return "Insufficient funds";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, wallet.getAmount() - amount);

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(walletId)});

        insertNewLogForNewExpense(logId, walletId, categoryId, amount, comments, date, time);

        return "New expense logged successfully";

    }

    public String deleteExpense(ActivityLog log) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet wallet = null;
        ContentValues values;
        int rows;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWallet())});
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            wallet = new Wallet();
            wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            wallet.setAmount(cursor.getFloat(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if (wallet == null) {
            return "Error while retrieving wallet information";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, wallet.getAmount() + log.getAmount());

        db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getWallet())});

        db.delete(ActivityLog.TABLE_NAME, ActivityLog.COLUMN_ID + " = ?", new String[]{Integer.toString(log.getId())});

        return "expense log deleted successfully";

    }

    public String deleteActivityLog(ActivityLog log) {
        String response = "";
        if (log.getType().equals(Constants.LOG_TYPE_NEW_INCOME)) {
            // deleting income log
            response = deleteIncome(log);
        } else if (log.getType().equals(Constants.LOG_TYPE_NEW_EXPENSE)) {
            // deleting expense log
            response = deleteExpense(log);
        } else if (log.getType().equals(Constants.LOG_TYPE_WALLET_TRANSFER)) {
            // deleting wallet transfer log
            response = deleteWalletTransfer(log);
        } else {
            response = "invalid activity type";
        }
        return response;
    }

    public List<Category> getAllCategories(Boolean active) {

        List<Category> categories = new ArrayList<>();

        String activeCategoryQuery = "SELECT * FROM " + Category.TABLE_NAME + " WHERE " + Category.COLUMN_ACTIVE + " = 1 ORDER BY " + Category.COLUMN_ID;
        String allCategoryQuery = "SELECT * FROM " + Category.TABLE_NAME + " ORDER BY " + Category.COLUMN_ID;
        String query = active ? activeCategoryQuery : allCategoryQuery;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(Category.COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(Category.COLUMN_NAME)));
                category.setType(cursor.getInt(cursor.getColumnIndex(Category.COLUMN_TYPE)));
                category.setActive(cursor.getInt(cursor.getColumnIndex(Category.COLUMN_ACTIVE)) == 1);

                categories.add(category);
            } while (cursor.moveToNext());
        }
        cursor.close();

        return categories;

    }

    public long insertNewLogForNewCategory(Integer categoryId, String categoryName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_ADD_CATEGORY);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_NEW, categoryName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        return id;

    }

    public long insertNewCategory(String name, Integer type) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME, name);
        values.put(Category.COLUMN_TYPE, type);

        Long id = db.insert(Category.TABLE_NAME, null, values);

        insertNewLogForNewCategory(id.intValue(), name);

        return id;

    }

    public long insertNewLogForEditCategory(Integer categoryId, String newCategoryName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_MODIFY_CATEGORY);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_NEW, newCategoryName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        return id;

    }

    public int updateCategoryName(Integer id, String name) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME, name);

        int rows = db.update(Category.TABLE_NAME, values, Category.COLUMN_ID + " = ?", new String[]{Integer.toString(id)});

        insertNewLogForEditCategory(id, name);

        return rows;

    }

    //for AndroidDatabaseManager
    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"message"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        }
//        catch(SQLException sqlEx){
//            Log.d("printing exception", sqlEx.getMessage());
//            //if any exceptions are triggered save the error message to cursor an return the arraylist
//            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
//            alc.set(1,Cursor2);
//            return alc;
//        }
        catch (Exception ex) {
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + ex.getMessage()});
            alc.set(1, Cursor2);
            return alc;
        }
    }

}
