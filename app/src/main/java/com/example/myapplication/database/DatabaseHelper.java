package com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.database.model.ActivityLog;
import com.example.myapplication.database.model.Wallet;
import com.example.myapplication.database.model.Category;
import com.example.myapplication.utils.Constants;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    private ContentValues getLogContentValues(Integer type){
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
        SimpleDateFormat timeFormat = new SimpleDateFormat(Constants.TIME_FORMAT);
        ContentValues values = new ContentValues();
        values.put(ActivityLog.COLUMN_TYPE, type);
        values.put(ActivityLog.COLUMN_LOG_DATE, dateFormat.format(new Date()));
        values.put(ActivityLog.COLUMN_LOG_TIME, timeFormat.format(new Date()));
        return values;
    }

    public long insertNewLogForNewWallet(Integer amount, Integer walletId, String walletName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_ADD_WALLET);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_NEW, walletName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        db.close();

        return id;

    }

    public long insertNewWallet(String name, Integer amount) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Wallet.COLUMN_NAME, name);
        values.put(Wallet.COLUMN_AMOUNT, amount);

        Long id = db.insert(Wallet.TABLE_NAME, null, values);

        db.close();

        insertNewLogForNewWallet(amount, id.intValue(), name);

        return id;

    }

    public long insertNewLogForEditWallet(Integer walletId, String newWalletName){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_MODIFY_WALLET);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_NEW, newWalletName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        db.close();

        return id;

    }

    public int updateWalletName(Integer id, String name){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Wallet.COLUMN_NAME, name);
        values.put(Wallet.COLUMN_UPDATE_TS, new Timestamp(System.currentTimeMillis()).toString());

        int rows = db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(id)});

        insertNewLogForEditWallet(id, name);

        return rows;

    }

    public List<Wallet> getAllWallets() {

        List<Wallet> wallets = new ArrayList<>();

        String query = "SELECT * FROM " + Wallet.TABLE_NAME + " WHERE " + Wallet.COLUMN_ACTIVE + " = 1 ORDER BY " + Wallet.COLUMN_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Wallet wallet = new Wallet();
                wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
                wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
                wallet.setUpdateTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_UPDATE_TS))));
                wallet.setAmount(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));

                wallets.add(wallet);
            } while (cursor.moveToNext());
        }
        db.close();

        return wallets;

    }

    public long insertNewLogForWalletTransfer(Integer walletId1, Integer walletId2, Integer amount){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_WALLET_TRANSFER);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId1);
        values.put(ActivityLog.COLUMN_WALLET_2, walletId2);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        db.close();

        return id;

    }

    public String walletTransfer(Integer wallet1, Integer wallet2, Integer amount) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet fromWallet = null;
        Wallet toWallet = null;
        ContentValues values;
        int rows;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(wallet1)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            fromWallet = new Wallet();
            fromWallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            fromWallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            fromWallet.setAmount(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }
        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(wallet2)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            toWallet = new Wallet();
            toWallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            toWallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            toWallet.setAmount(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if(fromWallet == null || toWallet == null) {
            return "Error while fetching wallet information";
        }

        if(amount > fromWallet.getAmount()){
            return "Insufficient funds";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, fromWallet.getAmount() - amount);
        values.put(Wallet.COLUMN_UPDATE_TS, new Timestamp(System.currentTimeMillis()).toString());

        rows = db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(wallet1)});

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, toWallet.getAmount() + amount);
        values.put(Wallet.COLUMN_UPDATE_TS, new Timestamp(System.currentTimeMillis()).toString());

        rows = db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(wallet2)});

        insertNewLogForWalletTransfer(wallet1, wallet2, amount);

        return "Transfer successful";

    }

    public long insertNewLogForNewIncome(Integer walletId, Integer categoryId, Integer amount, String comments){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_NEW_INCOME);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);
        values.put(ActivityLog.COLUMN_COMMENTS, comments);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        db.close();

        return id;

    }

    public String newIncome(Integer walletId, Integer categoryId, Integer amount, String comments){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet wallet = null;
        ContentValues values;
        int rows;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(walletId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            wallet = new Wallet();
            wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            wallet.setAmount(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if(wallet == null){
            return "Error while retrieving wallet information";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, wallet.getAmount() + amount);
        values.put(Wallet.COLUMN_UPDATE_TS, new Timestamp(System.currentTimeMillis()).toString());

        rows = db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(walletId)});

        insertNewLogForNewIncome(walletId, categoryId, amount, comments);

        return "New income logged successfully";

    }

    public long insertNewLogForNewExpense(Integer walletId, Integer categoryId, Integer amount, String comments){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_NEW_EXPENSE);
        values.put(ActivityLog.COLUMN_WALLET_1, walletId);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_AMOUNT, amount);
        values.put(ActivityLog.COLUMN_COMMENTS, comments);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        db.close();

        return id;

    }

    public String newExpense(Integer walletId, Integer categoryId, Integer amount, String comments){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor;
        Wallet wallet = null;
        ContentValues values;
        int rows;

        cursor = db.rawQuery("SELECT * FROM " + Wallet.TABLE_NAME + " where " + Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(walletId)});
        if(cursor.getCount() > 0){
            cursor.moveToFirst();
            wallet = new Wallet();
            wallet.setId(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_ID)));
            wallet.setName(cursor.getString(cursor.getColumnIndex(Wallet.COLUMN_NAME)));
            wallet.setAmount(cursor.getInt(cursor.getColumnIndex(Wallet.COLUMN_AMOUNT)));
        }

        if(wallet == null){
            return "Error while retrieving wallet information";
        }

        if(amount > wallet.getAmount()){
            return "Insufficient funds";
        }

        values = new ContentValues();
        values.put(Wallet.COLUMN_AMOUNT, wallet.getAmount() - amount);
        values.put(Wallet.COLUMN_UPDATE_TS, new Timestamp(System.currentTimeMillis()).toString());

        rows = db.update(Wallet.TABLE_NAME, values, Wallet.COLUMN_ID + " = ?", new String[] {Integer.toString(walletId)});

        insertNewLogForNewExpense(walletId, categoryId, amount, comments);

        return "New expense logged successfully";

    }

    public List<Category> getAllCategories() {

        List<Category> categories = new ArrayList<>();

        String query = "SELECT * FROM " + Category.TABLE_NAME + " WHERE " + Category.COLUMN_ACTIVE + " = 1 ORDER BY " + Category.COLUMN_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Category category = new Category();
                category.setId(cursor.getInt(cursor.getColumnIndex(Category.COLUMN_ID)));
                category.setName(cursor.getString(cursor.getColumnIndex(Category.COLUMN_NAME)));
                category.setUpdateTs(Timestamp.valueOf(cursor.getString(cursor.getColumnIndex(Category.COLUMN_UPDATE_TS))));
                category.setType(cursor.getInt(cursor.getColumnIndex(Category.COLUMN_TYPE)));

                categories.add(category);
            } while (cursor.moveToNext());
        }
        db.close();

        return categories;

    }

    public long insertNewLogForNewCategory(Integer categoryId, String categoryName) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_ADD_CATEGORY);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_NEW, categoryName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        db.close();

        return id;

    }

    public long insertNewCategory(String name, Integer type) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME, name);
        values.put(Category.COLUMN_TYPE, type);

        Long id = db.insert(Category.TABLE_NAME, null, values);

        db.close();

        insertNewLogForNewCategory(id.intValue(), name);

        return id;

    }

    public long insertNewLogForEditCategory(Integer categoryId, String newCategoryName){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getLogContentValues(Constants.LOG_TYPE_MODIFY_CATEGORY);
        values.put(ActivityLog.COLUMN_CATEGORY, categoryId);
        values.put(ActivityLog.COLUMN_NEW, newCategoryName);

        long id = db.insert(ActivityLog.TABLE_NAME, null, values);

        db.close();

        return id;

    }

    public int updateCategoryName(Integer id, String name){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Category.COLUMN_NAME, name);
        values.put(Category.COLUMN_UPDATE_TS, new Timestamp(System.currentTimeMillis()).toString());

        int rows = db.update(Category.TABLE_NAME, values, Category.COLUMN_ID + " = ?", new String[] {Integer.toString(id)});

        insertNewLogForEditCategory(id, name);

        return rows;

    }

    //for AndroidDatabaseManager
    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
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
        catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }

}
