package ru.furry.furview2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class FurryDatabase {

    public static final FurryDatabase INSTANCE = new FurryDatabase();

    private static String DB_NAME = "furryDB";
    public static int DB_VERSION = 12;
    private static FurryDatabaseOpenHelper dbHelper;
    private static SQLiteDatabase db;

    private FurryDatabase() {}

    public static void init(Context context) {
        dbHelper = new FurryDatabaseOpenHelper(context, DB_NAME, null, DB_VERSION);
        db = dbHelper.getWritableDatabase();
    }

    public static FurryDatabaseOpenHelper getDbHelper() {
        return dbHelper;
    }

    public static SQLiteDatabase getWritableDatabase() {
        return db;
    }
}