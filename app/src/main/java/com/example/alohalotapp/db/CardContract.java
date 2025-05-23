package com.example.alohalotapp.db;

import android.provider.BaseColumns;

public final class CardContract {
    private CardContract() {}

    public static class CardEntry implements BaseColumns {
        public static final String TABLE_NAME = "cards";
        public static final String COLUMN_NAME_HOLDER = "holder";
        public static final String COLUMN_NAME_NUMBER = "number";
        public static final String COLUMN_NAME_EXPIRY = "expiry";
    }
}
