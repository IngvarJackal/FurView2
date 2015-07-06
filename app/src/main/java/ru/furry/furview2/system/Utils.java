package ru.furry.furview2.system;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import ru.furry.furview2.images.FurImage;

import static junit.framework.Assert.assertTrue;

public class Utils {

    private static MessageDigest md5;
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(8);

    static {
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }

    public static BigInteger getMD5(byte[] file) {
        md5.update(file, 0, file.length);
        return new BigInteger(1, md5.digest());
    }

    public static long reduceMD5(BigInteger integer) {
        return integer.longValue();
    }

    public static class Tuple<X, Y> {
        public final X x;
        public final Y y;
        public Tuple(X x, Y y) {
            this.x = x;
            this.y = y;
        }
    }

    public static void printError(Exception e) {
        printError(e, "furry error");
    }

    public static void printError(Exception e, String tag) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        String exceptionAsString = sw.toString();
        Log.e("furry error", exceptionAsString);
    }

    public static boolean deleteDirectory(File path) {
        if( path.exists() ) {
            File[] files = path.listFiles();
            if (files == null) {
                return true;
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        return( path.delete() );
    }

    public static String getStringFromInputStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null)
            sb.append(line);
        return sb.toString();
    }

    public static String getIP() {
        return getIP(null);
    }

    public static String getIP(Proxy proxy) {
        String s = null;
        try {
            URL url = new URL("https://wtfismyip.com/text");
            HttpURLConnection connection;
            if (proxy != null) {
                connection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }
            connection.connect();
            s = Utils.convertStreamToString(connection.getInputStream());
        } catch (IOException e) {
            Utils.printError(e);
        }
        return s;
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static String unescapeUnicode(String unicode) {
        if (unicode.contains("\\u")) {
            unicode = unicode.replace("\\", "");
            StringBuilder sb = new StringBuilder();
            String[] arr = unicode.split("u");
            for(int i = 1; i < arr.length; i++){
                int hexVal = Integer.parseInt(arr[i], 16);
                sb.append((char)hexVal);
            }
            return sb.toString();
        } else {
            return unicode;
        }
    }

    public static String joinList(List<? extends Object> list, String separator) {
        switch (list.size()) {
            case 0: return "";
            case 1: return list.get(0).toString();
            default:
                StringBuilder sb = new StringBuilder();
                for (Object element : list.subList(0, list.size() - 1)) {
                    sb.append(element.toString());
                    sb.append(separator);
                }
                sb.append(list.get(list.size() - 1));
                return sb.toString();
        }
    }

    public static void assertImageEquality(FurImage img1, FurImage img2) {
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
        assertTrue(img1.getPreviewUrl() == img2.getPreviewUrl() || img1.getPreviewUrl().equals(img2.getPreviewUrl()));
    }
}
