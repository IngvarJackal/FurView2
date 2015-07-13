package ru.furry.furview2.system;

import android.content.Context;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;

public class ProxiedBaseImageDownloader extends BaseImageDownloader {

    public ProxiedBaseImageDownloader(Context context) {
        super(context);
    }
    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn = null;
        try {
            conn = ProxiedHTTPSLoader.openPage(encodedUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        return conn;
    }

}
