package ru.furry.furview2.system;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


public class RenewProxy {

    Context context;
//    GlobalData globalData;
    Boolean state = false;
    static final int TIMEOUT = 3000;
//    Handler handler;
    private List<Proxy> proxies = new ArrayList();

/*
    public RenewProxy(Handler incomingHandler, Context incomingContext) {
        super();
        this.handler = incomingHandler;
        this.context = incomingContext;
        globalData = (GlobalData) context.getApplicationContext();
    }
*/

    public List<Proxy> renew(List<Proxy> incomingProxies) {
        proxies=incomingProxies;

        Log.d("fgsfds", "Start testing proxies (" + proxies.size() + " items)");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int i;
                for (i=0;i<proxies.size()-1;i++){
                    try {
                        Log.d("fgsfds", "Try testing proxy " + i);

                        URL testUrl = new URL("https://e621.net/post/index.xml?limit=1");

//                        Proxy testingProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(globalData.getProxies().get(i).getIp(), globalData.getProxies().get(i).getPort()));
                        HttpsURLConnection huc = (HttpsURLConnection) testUrl.openConnection(proxies.get(i));
                        HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3Factory());
                        huc.setConnectTimeout(TIMEOUT);
                        huc.setRequestMethod("GET");
                        try {
                            //huc.connect();
                            InputStream input = huc.getInputStream();
                            Log.d("fgsfds", "Input stream after RenewProxy = " + Utils.convertStreamToString(input));
                            Log.d("fgsfds", "Good proxy");
                            input.close();
//                            globalData.setNumOfWorkingProxy(i);
//                            globalData.setCurrentProxy(i);
//                            globalData.setCurrentProxyItem(i);
//                            state=true;
//                            break;
                        }
                        //catch (SocketTimeoutException e)
                        //{Log.d("fgsfds", "Bad proxy"); }
                        catch (Exception e)
                        {
                            Log.d("fgsfds", "Bad proxy. Not response after "+TIMEOUT/1000+" sec. Removed.");
                            proxies.remove(i);
                        }
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

            }
        });
        t.start();

        return proxies;
    }


}