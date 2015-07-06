package ru.furry.furview2.system;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ru.furry.furview2.GlobalData;
import ru.furry.furview2.InitialScreen;
import ru.furry.furview2.MainActivity;


public class RenewProxy extends AsyncTask<Void, Void, Boolean> {
    Context context;
    InputStream is = null;
    GlobalData globalData;
    Boolean state = false;
    static final int TIMEOUT = 3000;

    public RenewProxy(Context context) {
        super();
        this.context = context;
        globalData = (GlobalData) context.getApplicationContext();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        return cheking();
    }

    public Boolean cheking() {
        String url = "https://e621.net/post/index.xml?limit=1";

        Log.d("fgsfds", "Start testing proxies ("+globalData.getProxies().size()+" items)");

        int i;
        int currentNumOfWorkingProxy=globalData.getNumOfWorkingProxy();
        for (i=currentNumOfWorkingProxy;i<globalData.getProxies().size()-1;i++){
//        for (i=0;i<1;i++){
            try {
                Log.d("fgsfds", "Try testing proxy "+i+" "+globalData.getCurrentProxyItem().getIp());

                URL testUrl = new URL("https://e621.net/post/index.xml?limit=1");
                //globalData.setCurrentProxy("221.182.62.115", 9999);

                Proxy testingProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(globalData.getProxies().get(i).getIp(), globalData.getProxies().get(i).getPort()));
                HttpsURLConnection huc = (HttpsURLConnection) testUrl.openConnection(testingProxy);
                HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3Factory());
                huc.setConnectTimeout(TIMEOUT);
                huc.setRequestMethod("GET");
                try {
                    //huc.connect();
                    InputStream input = huc.getInputStream();
                    Log.d("fgsfds", "Good proxy");
                    Log.d("fgsfds", "Input stream after RenewProxy = " + Utils.convertStreamToString(input));
                    globalData.setNumOfWorkingProxy(i);
                    globalData.setCurrentProxy(i);
                    globalData.setCurrentProxyItem(i);
                    state=true;
                    break;
                }
                catch (SocketTimeoutException e)
                {Log.d("fgsfds", "Bad proxy"); }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        return state;
    }

    @Override
    protected void onPostExecute(Boolean state) {
        return;
    }

}