package com.example.alohalotapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cards.db";

    private static final String SQL_CREATE_CARD_ENTRIES =
            "CREATE TABLE " + CardContract.CardEntry.TABLE_NAME + " (" +
                    CardContract.CardEntry._ID + " INTEGER PRIMARY KEY," +
                    CardContract.CardEntry.COLUMN_NAME_HOLDER + " TEXT," +
                    CardContract.CardEntry.COLUMN_NAME_NUMBER + " TEXT," +
                    CardContract.CardEntry.COLUMN_NAME_EXPIRY + " TEXT)";

    private static final String SQL_CREATE_WALLET_TABLE =
            "CREATE TABLE wallet (balance INTEGER)";

    public CardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CARD_ENTRIES);
        db.execSQL(SQL_CREATE_WALLET_TABLE);
        db.execSQL("INSERT INTO wallet (balance) VALUES (0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CardContract.CardEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS wallet");
        onCreate(db);
    }
}
