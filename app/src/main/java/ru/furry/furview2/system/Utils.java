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
}
