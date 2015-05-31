package ru.furry.furview2.system;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    private static MessageDigest md5;

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

    public class Tuple<X, Y> {
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
}
