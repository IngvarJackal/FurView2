package ru.furry.furview2.system;

import android.content.Context;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ProxiedBaseImageDownloader extends BaseImageDownloader {

    protected final Proxy proxy;

    public ProxiedBaseImageDownloader(Context context) {
        super(context);
        this.proxy = null;
    }

    public ProxiedBaseImageDownloader(Context context, Proxy proxy) {
        super(context);
        this.proxy = proxy;
    }

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3Factory());
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn;
        if (proxy != null) {
            conn = (HttpURLConnection) new URL(encodedUrl).openConnection(proxy);
        }
        else {
            conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        }
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        return conn;
    }

}
