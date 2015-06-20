package ru.furry.furview2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FurryDatabase {

    public static final FurryDatabase INSTANCE = new FurryDatabase();
    private static FurryDatabaseOpenHelper dbHelper;
    private static SQLiteDatabase db;

    private FurryDatabase() {}

    public static void init(Context context) {
        dbHelper = new FurryDatabaseOpenHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public static FurryDatabaseOpenHelper getDbHelper() {
        return dbHelper;
    }

    public static SQLiteDatabase getWritableDatabase() {
        return db;
    }
}