package ru.furry.furview2.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;

import ru.furry.furview2.R;
import ru.furry.furview2.system.Utils;

public class FurryDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 45;
    public static final String DB_NAME = "furry_db";
    private static String DB_PATH = "/data/data/ru.furry.furview2/databases/";

    private Boolean dbReady = true;
    private AsyncTask<SQLiteDatabase, Void, Boolean> initDB;
    private AsyncTask<SQLiteDatabase, Void, Boolean> clearDB;
    private Context context;

    public FurryDatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
        try {
            createDataBase();
        } catch (IOException e) {
            Utils.printError(e);
        }
    }

    private void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if (dbExist) {
            //do nothing - database already exist
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                Utils.printError(e);
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
        }

        if (checkDB != null) {
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        Log.d("fgsfds", "copy embedded  database...");
        InputStream myInput = context.getResources().openRawResource(R.raw.furry_db);

        String outFileName = DB_PATH + DB_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();

        onCreate(getWritableDatabase());
    }

    /**
     * This is thread-blocking method!
     *
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Boolean isReady() throws ExecutionException, InterruptedException {
        if (!dbReady) {
            if (initDB != null) {
                dbReady = initDB.get();
            } else {
                dbReady = clearDB.get();
            }
        }
        return dbReady;
    }

    protected static void initDatabase(SQLiteDatabase sqLiteDatabase) {

        Log.d("fgsfds", "clearing database...");
        sqLiteDatabase.execSQL("drop table if exists images;");
        sqLiteDatabase.execSQL("drop table if exists tags;");
        sqLiteDatabase.execSQL("drop table if exists taggings;");
        sqLiteDatabase.execSQL("drop table if exists logins;");

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
        //sqLiteDatabase.execSQL("create table aliases (aliasId integer primary key autoincrement, a text, b text);");
    }

    class InitDatabaseTask extends AsyncTask<SQLiteDatabase, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {
            SQLiteDatabase sqLiteDatabase = sqLiteDatabases[0];
            Log.d("fgsfds", "init database...");

            FurryDatabaseOpenHelper.initDatabase(sqLiteDatabase);

            return true;
        }
    }

    class ReInitDatabaseTask extends AsyncTask<SQLiteDatabase, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {
            SQLiteDatabase sqLiteDatabase = sqLiteDatabases[0];


            //sqLiteDatabase.execSQL("drop table if exists aliases;");


            try {
                copyDataBase();
            } catch (IOException e) {
                Utils.printError(e);
            }

            Log.d("fgsfds", "init database...");
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
