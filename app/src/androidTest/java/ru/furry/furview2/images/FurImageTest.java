package ru.furry.furview2.images;

import android.os.Parcel;
import android.test.AndroidTestCase;

import junit.framework.TestCase;

import org.joda.time.DateTime;

import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import static ru.furry.furview2.system.Utils.assertImageEquality;

public class FurImageTest extends AndroidTestCase {

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

    public void testSerialization() throws Exception {
        Parcel parcel = Parcel.obtain();
        testImage.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        assertImageEquality(testImage, FurImage.CREATOR.createFromParcel(parcel));
    }
}