package ru.furry.furview2.system;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import ru.furry.furview2.proxy.GetProxiedConnection;

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
            return GetProxiedConnection.getProxiedConnection(new URL(url));
    }

    public static HttpsURLConnection openPage(URL url) throws IOException {
            return GetProxiedConnection.getProxiedConnection(url);
    }
}
