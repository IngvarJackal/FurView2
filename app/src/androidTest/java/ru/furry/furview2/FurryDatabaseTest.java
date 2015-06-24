package ru.furry.furview2;

import android.test.AndroidTestCase;

import org.joda.time.DateTime;

import java.lang.Override;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.furry.furview2.database.FurryDatabase;
import ru.furry.furview2.images.FurImage;
import ru.furry.furview2.images.FurImageBuilder;
import ru.furry.furview2.images.Rating;
import ru.furry.furview2.system.AsyncDatabaseResponseHandlerGUI;

public class FurryDatabaseTest extends AndroidTestCase {

    private FurryDatabase database;
    private List<FurImage> dbImages;
    private FurImage testImage = new FurImageBuilder()
            .setSearchQuery("ururu :3")
            .setDescription("ur ur ur ur")
            .setScore(15)
            .setRating(Rating.SAFE)
            .setFileUrl("https://ururu.ru")
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
            .setPreviewWidth(300)
            .setPreviewHeight(400)
            .setRootPath("/")
            .createFurImage()
            .setFilePath("/ingvarjackal.jpeg")
            .setLocalScore(21)
            .setLocalTags(new ArrayList<String>(Arrays.asList("localtag1", "localtag2", "localtag3")));

    private void testImageEquality(FurImage img1, FurImage img2) {
        assertTrue(img1.getSearchQuery() == img2.getSearchQuery() || img1.getSearchQuery().equals(img2.getSearchQuery()));
        assertTrue(img1.getDescription().equals(img2.getDescription()) || img1.getDescription().equals(img2.getDescription()));
        assertTrue(img1.getScore() == img2.getScore());
        assertTrue(img1.getRating() == img2.getRating() || img1.getRating().equals(img2.getRating()));
        assertTrue(img1.getFileUrl() == img2.getFileUrl() || img1.getFileUrl().equals(img2.getFileUrl()));
        assertTrue(img1.getFileExt() == img2.getFileExt() || img1.getFileExt().equals(img2.getFileExt()));
        assertTrue(img1.getPageUrl() == img2.getPageUrl() || img1.getPageUrl().equals(img2.getPageUrl()));
        assertTrue(img1.getAuthor() == img2.getAuthor() || img1.getAuthor().equals(img2.getAuthor()));
        assertTrue(img1.getCreatedAt() == img2.getCreatedAt() || img1.getCreatedAt().equals(img2.getCreatedAt()));
        assertTrue(img1.getSources() == img2.getSources() || img1.getSources().equals(img2.getSources()));
        assertTrue(img1.getTags() == img2.getTags() || img1.getTags().equals(img2.getTags()));
        assertTrue(img1.getArtists() == img2.getArtists() || img1.getArtists().equals(img2.getArtists()));
        // assertTrue(img1.getDownloadedAt() == img2.getDownloadedAt() || img1.getDownloadedAt().equals(img2.getDownloadedAt())); // gets rid of milliseconds -- nobody care!
        assertTrue(img1.getMd5() == img2.getMd5() || img1.getMd5().equals(img2.getMd5()));
        assertTrue(img1.getFileName() == img2.getFileName() || img1.getFileName().equals(img2.getFileName()));
        assertTrue(img1.getFileSize() == img2.getFileSize());
        assertTrue(img1.getFileWidth() == img2.getFileWidth());
        assertTrue(img1.getFileHeight() == img2.getFileHeight());
        // assertTrue(img1.getPreviewHeight() == img2.getPreviewHeight()); // Deprecated
        // assertTrue(img1.getPreviewWidth() == img2.getPreviewWidth()); // Deprecated
        // assertTrue(img1.getRootPath() == img2.getRootPath() || img1.getRootPath().equals(img2.getRootPath())); // Deprecated
        assertTrue(img1.getFilePath() == img2.getFilePath() || img1.getFilePath().equals(img2.getFilePath()));
        assertTrue(img1.getID() == img2.getID());
        assertTrue(img1.getLocalScore() == img2.getLocalScore() || img1.getLocalScore().equals(img2.getLocalScore()));
        assertTrue(img1.getLocalTags() == img2.getLocalTags() || img1.getLocalTags().equals(img2.getLocalTags()));
    }

//    @Override
//    public void setUp() throws Exception {
//        super.setUp();
//        database = new FurryDatabase(new AsyncDatabaseResponseHandlerGUI() {
//            @Override
//            public void blockInterfaceForDBResponse() {
//
//            }
//
//            @Override
//            public void unblockInterfaceForDBResponse() {
//
//            }
//
//            @Override
//            public void retrieveDBResponse(List<FurImage> images) {
//
//            }
//        }, this.getContext());
//        database.create(testImage);
//    }
//
//    public void testSearchByTags() throws Exception {
//        List<FurImage> images = database.getImages("tag10", database.getWritableDatabase());
//        assertTrue(images.size() == 0);
//        images = database.searchByTags("").get();
//        assertTrue(images.size() == 1);
//        images = database.searchByTags("-tag10").get();
//        assertTrue(images.size() == 1);
//        images = database.searchByTags("~tag1").get();
//        assertTrue(images.size() == 1);
//        images = database.searchByTags("~tag1 ~tag2 -tag10 tag3").get();
//        assertTrue(images.size() == 1);
//    }
//
//    public void testSearchByMD5() throws Exception {
//        FurImage image = FurryDatabase.searchByMD5(testImage.getMd5()).get();
//        testImageEquality(image, testImage);
//    }
//
//    public void testUpdate() throws Exception {
//        //TODO: write test case
//    }
}