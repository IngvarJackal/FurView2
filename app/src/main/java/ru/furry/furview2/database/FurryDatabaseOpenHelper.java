package ru.furry.furview2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;

public class FurryDatabaseOpenHelper extends SQLiteOpenHelper {

    private static String DB_NAME = "furryDB";
    public static String DB_PATH;
    public static final int DB_VERSION = 1;
    private Context myContext;
    private Boolean dbReady = false;

    public FurryDatabaseOpenHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        DB_PATH = myContext.getDatabasePath(DB_NAME).toString();
    }

    public Boolean isReady() {
        return dbReady;
    }

    private boolean checkDataBase(String path) {
        boolean checkDB = false;
        try {
            String myPath = path;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
        return checkDB;
    }

    class InitDatabaseTask extends AsyncTask<SQLiteDatabase, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {
            SQLiteDatabase sqLiteDatabase = sqLiteDatabases[0];
            Log.d("fgsfds", "creating database...");
            sqLiteDatabase.execSQL("create table images (imageId integer primary key autoincrement," +
                    "searchQuery text," +
                    "description text," +
                    "score integer," +
                    "rating text," +
                    "fileUrl text," +
                    "fileExt text," +
                    "pageUrl text," +
                    "author text," +
                    "createdAt text," +
                    "sources text," +
                    "downloadedAt text," +
                    "md5 text," +
                    "fileName text," +
                    "fileSize integer," +
                    "fileWidth integer," +
                    "fileHeight integer," +
                    ");");
            sqLiteDatabase.execSQL("create table tags (tagId integer primary key autoincrement, tagName text);");
            sqLiteDatabase.execSQL("create table taggings (imageId integer, tagId integer, primary key(imageId, tagId));");

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            dbReady = true;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        boolean dbExist = checkDataBase(DB_PATH);
        if (!dbExist) {
            new InitDatabaseTask().execute(sqLiteDatabase);
        } else {
            dbReady = true;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
