package ru.furry.furview2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import ru.furry.furview2.R;
import ru.furry.furview2.system.Utils;

public class FurryDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 45;
    public static final String DB_NAME = "furry_db";

    private Boolean dbReady = true;
    private AsyncTask<SQLiteDatabase, Void, Boolean> initDB;
    private AsyncTask<SQLiteDatabase, Void, Boolean> clearDB;
    private Context context;

    public FurryDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    private void initAliases() throws IOException {

        Log.d("fgsfds", "init aliases...");

        SQLiteDatabase db = getWritableDatabase();

        InputStream is = context.getResources().openRawResource(R.raw.aliases);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = reader.readLine();
        try {
            db.beginTransaction();
            db.execSQL("create table aliases (aliasId integer primary key autoincrement, a text, b text, unique(a, b));");
            while (line != null) {
                String[] ab = line.split(" -> ");
                Log.d("fgsfds", "inserting values " + Arrays.toString(ab));
                ContentValues contentValues = new ContentValues(2);
                contentValues.put("a", ab[0]);
                contentValues.put("b", ab[1]);
                db.insertWithOnConflict("aliases", null, contentValues, SQLiteDatabase.CONFLICT_IGNORE);
                line = reader.readLine();
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }


    }

    protected static void initDatabase(SQLiteDatabase sqLiteDatabase) {

        Log.d("fgsfds", "clearing and init database...");
        sqLiteDatabase.execSQL("drop table if exists images;");
        sqLiteDatabase.execSQL("drop table if exists tags;");
        sqLiteDatabase.execSQL("drop table if exists taggings;");
        sqLiteDatabase.execSQL("drop table if exists logins;");
        sqLiteDatabase.execSQL("drop table if exists blacklist;");

        sqLiteDatabase.execSQL("create table images (imageId integer primary key on conflict replace," +
                "searchQuery text," +
                "description text," +
                "artists text," +
                "score integer," +
                "localScore integer," +
                "rating text," +
                "fileUrl text," +
                "previewUrl text," +
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
                "filePath text," +
                "tags text," +
                "localTags text," +
                "deleted text," +
                "unique (imageId)" +
                ");");
        sqLiteDatabase.execSQL("create table tags (tagId integer primary key on conflict ignore, tagName text, unique (tagId));");
        sqLiteDatabase.execSQL("create table taggings (imageId integer, tagId integer, primary key(imageId, tagId) on conflict ignore, unique (imageId, tagId));");
        sqLiteDatabase.execSQL("create table logins (loginId integer primary key autoincrement, resource text, login text, password text);");
        sqLiteDatabase.execSQL("create table blacklist (tagId integer primary key autoincrement, tag text, unique(tag));");
    }

    class InitDatabaseTask extends AsyncTask<SQLiteDatabase, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {
            SQLiteDatabase sqLiteDatabase = sqLiteDatabases[0];

            try {
                initAliases();
            } catch (IOException e) {
                Utils.printError(e);
            }

            FurryDatabaseOpenHelper.initDatabase(sqLiteDatabase);

            return true;
        }
    }

    class ReInitDatabaseTask extends AsyncTask<SQLiteDatabase, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {
            SQLiteDatabase sqLiteDatabase = sqLiteDatabases[0];

            FurryDatabaseOpenHelper.initDatabase(sqLiteDatabase);

            return true;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        dbReady = false;
        initDB = new InitDatabaseTask().execute(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i < i1) {
            dbReady = false;
            clearDB = new ReInitDatabaseTask().execute(sqLiteDatabase);
        }
    }
}
