package com.example.myapplication.com.example.myapplication.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.myapplication.com.example.myapplication.database.com.example.myapplication.sqlite.model.Wallet;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "expense_tracker_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Wallet.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Wallet.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long insertNewWallet(String name, Integer amount) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Wallet.COLUMN_NAME, name);
        values.put(Wallet.COLUMN_AMOUNT, amount);

        long id = db.insert(Wallet.TABLE_NAME, null, values);

        db.close();

        return id;

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
