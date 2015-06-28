package ru.furry.furview2.system;

import android.content.Context;
import android.net.Uri;

import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

public class ProxiedBaseImageDownloader extends BaseImageDownloader {

    protected final Proxy proxy;
    protected SSLSocketFactory NoSSLv3Factory;

    public ProxiedBaseImageDownloader(Context context) {
        super(context);
        this.proxy = null;
    }

    private void init() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslcontext = SSLContext.getInstance("TLSv1");
        sslcontext.init(null, null, null);
        NoSSLv3Factory = new NoSSLv3SocketFactory(sslcontext.getSocketFactory());
    }

    public ProxiedBaseImageDownloader(Context context, Proxy proxy) {
        super(context);
        this.proxy = proxy;
        try {
            init();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            Utils.printError(e);
        }
    }

    @Override
    protected HttpURLConnection createConnection(String url, Object extra) throws IOException {
        String encodedUrl = Uri.encode(url, ALLOWED_URI_CHARS);
        HttpURLConnection conn;
        if (proxy != null) {
            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
            conn = (HttpURLConnection) new URL(encodedUrl).openConnection(proxy);
        }
        else {
            HttpsURLConnection.setDefaultSSLSocketFactory(NoSSLv3Factory);
            conn = (HttpURLConnection) new URL(encodedUrl).openConnection();
        }
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        return conn;
    }

}
