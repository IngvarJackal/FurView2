package ru.furry.furview2.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.system.AsyncDatabaseResponseHandler;
import ru.furry.furview2.system.AsyncDatabaseResponseHandlerGUI;
import ru.furry.furview2.system.Utils;

import static ru.furry.furview2.system.Utils.joinList;
import static ru.furry.furview2.system.Utils.reduceMD5;

public class FurryDatabase implements AsyncDatabaseResponseHandler {

    private static String DB_NAME = "furryDB";
    private static int DB_VERSION = 19;
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SEPARATOR = "qpzao";
    private static final int RADIX = 36;

    private FurryDatabaseOpenHelper dbHelper;
    protected SQLiteDatabase database;
    private AsyncDatabaseResponseHandlerGUI parentThread;

    public FurryDatabase(AsyncDatabaseResponseHandlerGUI guiThread, Context context) {
        this.parentThread = guiThread;
        dbHelper = new FurryDatabaseOpenHelper(context, DB_NAME, null, DB_VERSION);
        database = dbHelper.getWritableDatabase();
    }

    public FurryDatabaseOpenHelper getDbHelper() {
        return dbHelper;
    }

    public SQLiteDatabase getWritableDatabase() {
        return database;
    }

    private static String codeRating(Rating rating) {
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

        values.put("imageId", image.getID());
        values.put("searchQuery", image.getSearchQuery());
        values.put("description", image.getDescription());
        values.put("score", image.getScore());
        values.put("localScore", image.getLocalScore());
        values.put("rating", codeRating(image.getRating()));
        values.put("fileUrl", image.getFileUrl());
        values.put("fileExt", image.getFileExt());
        values.put("pageUrl", image.getPageUrl());
        values.put("author", image.getAuthor());
        values.put("artists", ((image.getArtists() != null) && (image.getArtists().size()>0)) ? joinList(image.getArtists(), SEPARATOR) : "");
        values.put("createdAt", codeDateTime(image.getCreatedAt()));
        values.put("sources", ((image.getSources() != null) && (image.getSources().size()>0)) ? joinList(image.getSources(), SEPARATOR) : "");
        values.put("downloadedAt", codeDateTime(image.getDownloadedAt()));
        values.put("md5", image.getMd5().toString(RADIX));
        values.put("fileName", image.getFileName());
        values.put("fileSize", image.getFileSize());
        values.put("fileWidth", image.getFileWidth());
        values.put("fileHeight", image.getFileHeight());
        values.put("filePath", image.getFilePath());
        values.put("tags", ((image.getTags() != null) && (image.getTags().size()>0)) ? joinList(image.getTags(), SEPARATOR) : "");
        values.put("localTags", ((image.getLocalTags() != null) && (image.getLocalTags().size()>0)) ? joinList(image.getLocalTags(), SEPARATOR) : "");

        return values;
    }

    private static FurImage decodeImage(Cursor cursor) {
        return new FurImageBuilder()
                .setSearchQuery(cursor.getString(cursor.getColumnIndex("searchQuery")))
                .setDescription(cursor.getString(cursor.getColumnIndex("description")))
                .setScore(cursor.getInt(cursor.getColumnIndex("score")))
                .setRating(decodeRating(cursor.getString(cursor.getColumnIndex("rating"))))
                .setFileUrl(cursor.getString(cursor.getColumnIndex("fileUrl")))
                .setFileName(cursor.getString(cursor.getColumnIndex("fileName")))
                .setFileExt(cursor.getString(cursor.getColumnIndex("fileExt")))
                .setPageUrl(cursor.getString(cursor.getColumnIndex("pageUrl")))
                .setAuthor(cursor.getString(cursor.getColumnIndex("author")))
                .setArtists(Arrays.asList(cursor.getString(cursor.getColumnIndex("artists")).split(SEPARATOR)))
                .setCreatedAt(decodeDateTime(cursor.getString(cursor.getColumnIndex("createdAt"))))
                .setSources(Arrays.asList(cursor.getString(cursor.getColumnIndex("sources")).split(SEPARATOR)))
                .setDownloadedAt(decodeDateTime(cursor.getString(cursor.getColumnIndex("downloadedAt"))))
                .setMd5(new BigInteger(cursor.getString(cursor.getColumnIndex("md5")), RADIX))
                .setFileName(cursor.getString(cursor.getColumnIndex("fileName")))
                .setFileSize(cursor.getInt(cursor.getColumnIndex("fileSize")))
                .setFileWidth(cursor.getInt(cursor.getColumnIndex("fileWidth")))
                .setFileHeight(cursor.getInt(cursor.getColumnIndex("fileHeight")))
                .setTags(Arrays.asList(cursor.getString(cursor.getColumnIndex("tags")).split(SEPARATOR)))
                .createFurImage()
                .setFilePath(cursor.getString(cursor.getColumnIndex("filePath")))
                .setLocalScore(cursor.getInt(cursor.getColumnIndex("localScore")))
                .setLocalTags(Arrays.asList(cursor.getString(cursor.getColumnIndex("localTags")).split(SEPARATOR)));
    }

    private static void insertTags(List<String> tags, long imageId, SQLiteDatabase db) {
        for (String tag : tags) {
            long tagId = reduceMD5(Utils.getMD5(tag.getBytes(Charset.forName("UTF-8"))));

            ContentValues values = new ContentValues();
            values.put("tagId", tagId);
            values.put("tagName", tag);
            db.insert("tags", null, values);

            ContentValues values2 = new ContentValues();
            values2.put("tagId", tagId);
            values2.put("imageId", imageId);
            db.insert("taggings", null, values2);
        }
    }

    protected static void storeImage(FurImage image, SQLiteDatabase db) {
        db.insert("images", null, codeImage(image));
        if (image.getTags() != null) {
            insertTags(image.getTags(), reduceMD5(image.getMd5()), db);
        }
        if (image.getLocalTags() != null) {
            insertTags(image.getLocalTags(), reduceMD5(image.getMd5()), db);
        }
    }

    private static Utils.Tuple<String, String[]> constructQuery(String query) {
        query = query.replaceAll("\\s", " ");
        if (query.replace(" ", "").equals("")) {
            return new Utils.Tuple<>("select * from images", new String[0]);
        }

        String[] tags = query.split(" ");
        List<String> arguments = new ArrayList<>(tags.length);

        List<String> not = new ArrayList<>();
        List<String> or = new ArrayList<>();
        List<String> and = new ArrayList<>();
        SpecialTags specTags = new SpecialTags();

        for (String tag : tags) {
            if (tag.contains("rating:")) {
                specTags.rating = tag;
            } else if (tag.startsWith("-")) {
                not.add(tag.substring(1));
            } else if (tag.startsWith("~")) {
                or.add(tag.substring(1));
            } else {
                and.add(tag);
            }
        }

        StringBuilder sqlQuery = new StringBuilder();
        sqlQuery.append("select i.* ");
        sqlQuery.append("from images i, tags t, taggings tg ");
        sqlQuery.append("where tg.tagId == t.tagId and tg.imageId == i.imageId ");

        // SPECIAL TAGS
        if (specTags.rating != null) {
            sqlQuery.append("and i.rating ");
            if (specTags.rating.startsWith("-"))
                sqlQuery.append("not");
            sqlQuery.append(" in (?) ");
            arguments.add(specTags.rating.replaceAll("-?rating:", "").replace("safe", "s").replace("questionable", "q").replace("explicit", "e"));
        }


        // NOT
        if (not.size() > 0) {
            sqlQuery.append("and i.imageId not in (");
            sqlQuery.append("select ii.imageId from images ii, tags tt, taggings tgtg ");
            sqlQuery.append("where tgtg.tagId == tt.tagId and tgtg.imageId == ii.imageId ");
            sqlQuery.append("and (tt.tagName in (");
            if (not.size() - 2 > 0) {
                for (String tag : not.subList(0, not.size() - 1)) {
                    arguments.add(tag);
                    sqlQuery.append("?, '");
                }
                arguments.add(not.get(not.size() - 1));
                sqlQuery.append("?))) ");
            } else {
                arguments.add(not.get(0));
                sqlQuery.append("?))) ");
            }
        }


        // OR
        if (or.size() > 0) {
            sqlQuery.append("and i.imageId in (");
            sqlQuery.append("select iii.imageId from images iii, tags ttt, taggings tgtgtg ");
            sqlQuery.append("where tgtgtg.tagId == ttt.tagId and tgtgtg.imageId == iii.imageId ");
            sqlQuery.append("and (ttt.tagName in (");
            if (or.size() - 2 > 0) {
                for (String tag : or.subList(0, or.size() - 1)) {
                    arguments.add(tag);
                    sqlQuery.append("?, '");
                }
                arguments.add(or.get(or.size() - 1));
                sqlQuery.append("?))) ");
            } else {
                arguments.add(or.get(0));
                sqlQuery.append("?))) ");
            }
        }

        // AND
        if (and.size() > 0) {
            sqlQuery.append("and (t.tagName in (");
            if (and.size() - 2 > 0) {
                for (String tag : and.subList(0, and.size() - 1)) {
                    arguments.add(tag);
                    sqlQuery.append("?, '");
                }
                arguments.add(and.get(and.size() - 1));
                sqlQuery.append("?)) ");
            } else {
                arguments.add(and.get(0));
                sqlQuery.append("?)) ");
            }
            sqlQuery.append("group by i.imageId ");
            sqlQuery.append("having count (t.tagId) = ").append(and.size());
        } else {
            sqlQuery.append("group by i.imageId");
        }


        return new Utils.Tuple<>(sqlQuery.toString(), arguments.toArray(new String[arguments.size()]));
    }

    protected static ArrayList<FurImage> getImages(String query, SQLiteDatabase db) {

        Utils.Tuple<String, String[]> tQuery = constructQuery(query);

        String sqlQuery = tQuery.x;
        String[] arguments = tQuery.y;

        Cursor cursor = db.rawQuery(sqlQuery, arguments);

        ArrayList<FurImage> results = new ArrayList<>();
        while (cursor.moveToNext()) {
            results.add(decodeImage(cursor));
        }

        return results;
    }

    protected static List<FurImage> getImageByMD5(BigInteger md5, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from images where imageId == ?", new String[] {Long.toString(Utils.reduceMD5(md5))});
        FurImage image = null;
        if (cursor.moveToNext()) {
            image = decodeImage(cursor);
        }
        return new ArrayList<>(Arrays.asList(image));
    }

    public void create(FurImage image) {
        new AsyncCreate().execute(image);
    }

    class AsyncCreate extends AsyncTask<FurImage, Void, Void> {

        @Override
        protected Void doInBackground(FurImage... furImages) {
            storeImage(furImages[0], database);
            return null;
        }
    }

    public void search(String query) {
        // TODO add search by: rating, score, localscore, downloaddate, creationdate, artist
        parentThread.blockInterfaceForDBResponse();
        new AsyncSearchTags().execute(new Utils.Tuple<String, AsyncDatabaseResponseHandler>(query, this));
    }

    class AsyncSearchTags extends AsyncTask<Utils.Tuple<String, AsyncDatabaseResponseHandler>, Void, List<FurImage>> {

        private AsyncDatabaseResponseHandler delegate;

        @Override
        protected List<FurImage> doInBackground(Utils.Tuple<String, AsyncDatabaseResponseHandler>... tuples) {
            delegate = tuples[0].y;
            return getImages(tuples[0].x, database);
        }

        @Override
        protected void onPostExecute(List<FurImage> images) {
            delegate.processDBResponse(images);
        }
    }

    public void searchByMD5(BigInteger md5) {
        parentThread.blockInterfaceForDBResponse();
        new AsyncSearchMD5().execute(new Utils.Tuple<BigInteger, AsyncDatabaseResponseHandler>(md5, this));
    }

    class AsyncSearchMD5 extends AsyncTask<Utils.Tuple<BigInteger, AsyncDatabaseResponseHandler>, Void, List<FurImage>> {

        private AsyncDatabaseResponseHandler delegate;

        @Override
        protected List<FurImage> doInBackground(Utils.Tuple<BigInteger, AsyncDatabaseResponseHandler>... tuples) {
            delegate = tuples[0].y;
            return getImageByMD5(tuples[0].x, database);
        }

        @Override
        protected void onPostExecute(List<FurImage> images) {
            delegate.processDBResponse(images);
        }
    }

    @Override
    public void processDBResponse(List<FurImage> images) {
        parentThread.retrieveDBResponse(images);
        parentThread.unblockInterfaceForDBResponse();
    }

    /**
     * The method doesn't delete tags; just adds them if needed!
     *
     * @param image
     */
    public void update(FurImage image) {
        create(image);
    }

}