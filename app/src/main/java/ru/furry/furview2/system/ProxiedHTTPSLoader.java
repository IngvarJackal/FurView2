package ru.furry.furview2.system;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ProxiedHTTPSLoader {
    public static HttpsURLConnection openPage(String url) {
        try {
            return (HttpsURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            Utils.printError(e);
            return null;
        }
    }

    public static HttpsURLConnection openPage(URL url) {
        try {
            return (HttpsURLConnection) url.openConnection();
        } catch (IOException e) {
            Utils.printError(e);
            return null;
        }
    }
}
