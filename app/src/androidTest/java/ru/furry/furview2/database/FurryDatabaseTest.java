package ru.furry.furview2.database;

import android.test.AndroidTestCase;

import org.joda.time.DateTime;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.system.AsyncDatabaseResponseHandlerGUI;

import static ru.furry.furview2.system.Utils.assertImageEquality;

public class FurryDatabaseTest extends AndroidTestCase {

    private FurryDatabase database;
    private List<FurImage> dbImages;
    private FurImage testImage = new FurImageBuilder()
            .setSearchQuery("ururu :3")
            .setDescription("ur ur ur ur")
            .setScore(15)
            .setRating(Rating.SAFE)
            .setFileUrl("https://ururu.ru/ururu.jpeg")
            .setPreviewUrl("https://ururu.ru/preview/ururu.jpeg")
            .setFileExt("jpeg")
            .setPageUrl(null)
            .setAuthor("IngvarJackal")
            .setCreatedAt(new DateTime(2014, 6, 7, 5, 13, 59))
            .setSources(new ArrayList<>(Arrays.asList("https://ururu1.ru", "https://ururu2.ru")))
            .setTags(new ArrayList<>(Arrays.asList("tag1", "tag2", "tag3", "tag4", "tag5", "tag6", "tag7", "tag8")))
            .setArtists(new ArrayList<>(Arrays.asList("artist1", "artist2")))
            .setDownloadedAt(new DateTime())
            .setMd5(new BigInteger("b1946ac92492d2347c6235b4d2611184", 36))
            .setFileName("ingvarjackal")
            .setFileSize(1024)
            .setFileWidth(800)
            .setFileHeight(600)
            .createFurImage()
            .setFilePath(null)
            .setLocalScore(21)
            .setLocalTags(new ArrayList<String>(Arrays.asList("localtag1", "localtag2", "localtag3")));



    @Override
    public void setUp() throws Exception {
        super.setUp();
        database = new FurryDatabase(new AsyncDatabaseResponseHandlerGUI() {
            @Override
            public void blockInterfaceForDBResponse() {

            }

            @Override
            public void unblockInterfaceForDBResponse() {

            }

            @Override
            public void retrieveDBResponse(List<FurImage> images) {

            }
        }, this.getContext());
        database.storeImage(testImage, database.getWritableDatabase());
    }

    public void testSearch() throws Exception {
        List<FurImage> images = database.getImages("tag10", database.getWritableDatabase());
        assertTrue(images.size() == 0);
        images = database.getImages("", database.getWritableDatabase());
        assertTrue(images.size() == 1);
        images = database.getImages("-tag10", database.getWritableDatabase());
        assertTrue(images.size() == 1);
        images = database.getImages("~tag1", database.getWritableDatabase());
        assertTrue(images.size() == 1);
        images = database.getImages("~tag1 ~tag2 -tag10 tag3", database.getWritableDatabase());
        assertTrue(images.size() == 1);
        images = database.getImages("~tag1 ~tag2 -tag10 tag3 rating:s", database.getWritableDatabase());
        assertTrue(images.size() == 1);
        images = database.getImages("~tag1 ~tag2 -tag10 tag3 -rating:s", database.getWritableDatabase());
        assertTrue(images.size() == 0);
        images = database.getImages("~tag1 ~tag2 -tag10 tag3 -rating:e", database.getWritableDatabase());
        assertTrue(images.size() == 1);
    }

    public void testSearchByMD5() throws Exception {
        List<FurImage> images = database.getImageByMD5(testImage.getMd5(), database.getWritableDatabase());
        assertImageEquality(images.get(0), testImage);
    }

    public void testDeletion() throws Exception {
        database.deleteImage(testImage.getMd5(), database.getWritableDatabase());
        List<FurImage> images = database.getImageByMD5(testImage.getMd5(), database.getWritableDatabase());
        assertTrue(images.size() == 0);
        database.storeImage(testImage, database.getWritableDatabase());
    }

    public void testUpdate() throws Exception {
        //TODO: write test case
    }
}