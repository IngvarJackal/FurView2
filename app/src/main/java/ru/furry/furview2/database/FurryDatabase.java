package ru.furry.furview2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.system.Utils;

import static ru.furry.furview2.system.Utils.joinList;
import static ru.furry.furview2.system.Utils.reduceMD5;

public class FurryDatabase {

    public static final FurryDatabase INSTANCE = new FurryDatabase();

    private static String DB_NAME = "furryDB";
    private static int DB_VERSION = 13;
    private static FurryDatabaseOpenHelper dbHelper;
    private static SQLiteDatabase db;

    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARATOR = "|";
    private static final int RADIX = 36;

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

    private static  String codeRating(Rating rating) {
        String sRating;
        switch (rating) {
            case SAFE:
                sRating = "s";
                break;
            case QUESTIONABLE:
                sRating = "q";
                break;
            case EXPLICIT:
                sRating = "e";
                break;
            default:
                sRating = "na";
                break;
        }
        return sRating;
    }

    private static Rating decodeRating(String sRating) {
        Rating rating;
        switch (sRating) {
            case "s":
                rating = Rating.SAFE;
                break;
            case "q":
                rating = Rating.QUESTIONABLE;
                break;
            case "e":
                rating = Rating.EXPLICIT;
                break;
            default:
                rating = Rating.NA;
                break;
        }
        return rating;
    }

    private static String codeDateTime(DateTime date) {
        return DATETIME_FORMAT.print(date);
    }

    private static DateTime decodeDateTime(String sDate) {
        return DATETIME_FORMAT.parseDateTime(sDate);
    }

    private static ContentValues codeImage(FurImage image) {
        ContentValues values = new ContentValues();

        values.put("imageID", reduceMD5(image.getMd5()));
        values.put("searchQuery", image.getSearchQuery());
        values.put("description", image.getDescription());
        values.put("score", image.getScore());
        values.put("rating", codeRating(image.getRating()));
        values.put("fileUrl", image.getFileUrl());
        values.put("fileExt", image.getFileExt());
        values.put("pageUrl", image.getPageUrl());
        values.put("author", image.getAuthor());
        values.put("createdAt", codeDateTime(image.getCreatedAt()));
        values.put("sources", joinList(image.getSources(), SEPARATOR));
        values.put("downloadedAt", codeDateTime(image.getDownloadedAt()));
        values.put("md5", image.getMd5().toString(RADIX));
        values.put("fileName", image.getFileName());
        values.put("fileSize", image.getFileSize());
        values.put("fileWidth", image.getFileWidth());
        values.put("fileHeight", image.getFileHeight());
        values.put("filePath", image.getFilePath());


        return values;
    }

    private static void insertTags(List<String> tags, long imageID) {
        for (String tag : tags) {
            long tagID = reduceMD5(Utils.getMD5(tag.getBytes(Charset.forName("UTF-8"))));

            ContentValues values = new ContentValues();
            values.put("tagId", tagID);
            values.put("tagName", tag);
            db.insert("tags", null, values);

            ContentValues values2 = new ContentValues();
            values2.put("tagId", tagID);
            values2.put("imageId", imageID);
            db.insert("taggings", null, values2);
        }
    }

    public static void storeImage(FurImage image) {
        db.insert("images", null, codeImage(image));
        insertTags(image.getTags(), reduceMD5(image.getMd5()));
    }


    /**
     * The method doesn't delete tags; just adds them if needed!
     *
     * @param image
     */
    public static void updateImage(FurImage image) {
        storeImage(image);
    }

    public static void deleteImage(FurImage image) {
//        String[] md5Halfed = {Long.toString(reduceMD5(image.getMd5()))};
//        db.delete("images", "imageID = ?", md5Halfed);
        throw new Error("DELETE operation isn't implemented in FurryDatabase.java yet.");
    }


    private static String parseQuery(String rawQuery) {
        // TODO implement
        return null;
    }

    public static FurImage getImages(String query) {
        // TODO implement
        return null;
    }


}