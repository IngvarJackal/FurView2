package ru.furry.furview2.system;

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
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ru.furry.furview2.InitialScreen;


final public class GetAndCheckProxy { //public ???

    private static List<Proxy> proxies = new ArrayList();


    public final static Proxy getProxy(){
        if (InitialScreen.useProxy)
        {
            if (proxies.size()<1) {
                Log.d("fgsfds", "Start getting proxies. Current num of proxies is: " + proxies.size());
                proxies=GetListProxies();
                Log.d("fgsfds", "Start checking proxies. Current num of proxies is: " + proxies.size());
                proxies=CheckListProxies(proxies);
                Log.d("fgsfds", "Proxies checked. Current num of proxies is: " + proxies.size());
                return proxies.get(0);
            }
            else{
                Log.d("fgsfds", "Start checking proxies. Current num of proxies is: " + proxies.size());
                proxies=CheckListProxies(proxies);
                Log.d("fgsfds", "Proxies checked. Current num of proxies is: " + proxies.size());
                return proxies.get(0);
            }
        }
        else return null;


    }

    private static List<Proxy> CheckListProxies(List<Proxy> incomingProxies) {
        final int TIMEOUT = 3000;
        List<Proxy> localProxies = new ArrayList();
        localProxies=incomingProxies;

        int i;
        for (i=0;i<localProxies.size()-1;i++){
            try {
                Log.d("fgsfds", "Try testing proxy " + i + " "+ localProxies.get(i).address().toString());

                URL testUrl = new URL("https://e621.net/post/index.xml?limit=1");

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
                    huc.disconnect();
                    break;
                }
                //catch (SocketTimeoutException e)
                //{Log.d("fgsfds", "Bad proxy"); }
                catch (Exception e)
                {
                    localProxies.remove(i);
                    Log.d("fgsfds", "Bad proxy. Not response after " + TIMEOUT / 1000 + " sec. Removed. Proxies left: " +localProxies.size());
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
        return localProxies;
    }

    private static List<Proxy> GetListProxies()
    {
        InputStream is = null;
        List<Proxy> localProxies = new ArrayList();
        String url = "http://api.foxtools.ru/v2/Proxy.xml";

        // Adding parameters
        Log.d("fgsfds", "Set param in GetProxyList before request");
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        parameters.add(new BasicNameValuePair("type", "2"));
        parameters.add(new BasicNameValuePair("available", "1"));
        parameters.add(new BasicNameValuePair("free", "1"));
        parameters.add(new BasicNameValuePair("uptime", "2"));
        // http://api.foxtools.ru/v2/Proxy.xml?type=2&available=1&free=1&uptime=2

        //GET request
        try {
            DefaultHttpClient httpClient = new DefaultHttpClient();
            String paramString = URLEncodedUtils.format(parameters, "utf-8");
            url += "?" + paramString;
            Log.d("fgsfds", "Try HTTP request in GetProxyList = " + url);
            HttpGet httpGet = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            is = httpEntity.getContent();

            Log.d("fgsfds", "Success HTTP request in GetProxyList");

            //Log.d("fgsfds", "Input stream after GET request to api.foxtools.ru = " + Utils.convertStreamToString(is));

        } catch (UnsupportedEncodingException e) {
            Log.d("fgsfds", "Fail HTTP request in GetProxyList");
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.d("fgsfds", "Fail HTTP request in GetProxyList");
            e.printStackTrace();
        } catch (IOException e) {
            Log.d("fgsfds", "Fail HTTP request in GetProxyList");
            e.printStackTrace();
        }

        //Parsing XML
        Log.d("fgsfds", "Start DOM parser");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(is);
            Element root = dom.getDocumentElement();
            NodeList items = root.getElementsByTagName("item");
            for (int i=0;i<items.getLength();i++){
                ProxyItem proxyElement = new ProxyItem();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();
                for (int j=0;j<properties.getLength();j++){
                    Node property = properties.item(j);
                    String name = property.getNodeName();
                    if (name.equalsIgnoreCase("ip")){
                        proxyElement.setIp(property.getFirstChild().getNodeValue());
                    } else if (name.equalsIgnoreCase("port")){
                        proxyElement.setPort(Integer.valueOf(property.getFirstChild().getNodeValue()));
                    } else if (name.equalsIgnoreCase("uptime")){
                        proxyElement.setUptime(Double.valueOf(property.getFirstChild().getNodeValue()));
                    } else if (name.equalsIgnoreCase("country")){
                        NodeList countryElements = property.getChildNodes();
                        for (int k=0;k<countryElements.getLength();k++) {
                            Node countryElement = countryElements.item(k);
                            String countryName = countryElement.getNodeName();
                            if (countryName.equalsIgnoreCase("iso3166a2")){
                                proxyElement.setCoutry(countryElement.getFirstChild().getNodeValue());
                            }
                        }
                    }
                }
                //Checking for country
                if (proxyElement.getCoutry().equals("RU"))
                {}
                else{
                    //break; // if need only 1 proxy
                    localProxies.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyElement.getIp(), proxyElement.getPort())));
                }
            }
            is.close();
            Log.d("fgsfds", "Proxies found: " + localProxies.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return localProxies;
    }

}
