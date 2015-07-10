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

    /**
     * @param url has to be already encoded
     * @return
     * @throws IOException
     */
    public static HttpsURLConnection openPage(String url) throws IOException {
        return (HttpsURLConnection) new URL(url).openConnection(GetAndCheckProxy.getProxy());
    }

    public static HttpsURLConnection openPage(URL url) throws IOException {
        return (HttpsURLConnection) url.openConnection(GetAndCheckProxy.getProxy());
    }
}
