package ru.furry.furview2.system;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

public class ProxiedHTTPSLoader {
    private static final String CHARSET = "UTF-8";

    {
        HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3Factory());
    }

    public static HttpsURLConnection openPage(String url) throws IOException {
        return (HttpsURLConnection) new URL(URLEncoder.encode(url, CHARSET)).openConnection();
    }

    public static HttpsURLConnection openPage(URL url) throws IOException {
        return (HttpsURLConnection) url.openConnection();
    }
}
