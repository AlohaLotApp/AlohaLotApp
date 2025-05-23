package com.example.alohalotapp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CardDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Cards.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + CardContract.CardEntry.TABLE_NAME + " (" +
                    CardContract.CardEntry._ID + " INTEGER PRIMARY KEY," +
                    CardContract.CardEntry.COLUMN_NAME_HOLDER + " TEXT," +
                    CardContract.CardEntry.COLUMN_NAME_NUMBER + " TEXT," +
                    CardContract.CardEntry.COLUMN_NAME_EXPIRY + " TEXT)";

    public CardDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CardContract.CardEntry.TABLE_NAME);
        onCreate(db);
    }
}
