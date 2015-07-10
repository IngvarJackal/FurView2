package ru.furry.furview2.system;

import android.os.Handler;
import android.os.Message;
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class GetProxyList {
    InputStream is = null;
    private List<Proxy> localProxies = new ArrayList();

    public GetProxyList() {
        super();
    }

    public List<Proxy> GetingListOfProxies() {
        localProxies=null;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://api.foxtools.ru/v2/Proxy.xml";

                // Adding parameters
                Log.d("fgsfds", "Set param in GetProxyList before request");
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", "2"));
                params.add(new BasicNameValuePair("available", "1"));
                params.add(new BasicNameValuePair("free", "1"));
                params.add(new BasicNameValuePair("uptime", "2"));
                // http://api.foxtools.ru/v2/Proxy.xml?type=2&available=1&free=1&uptime=2

                //GET request
                try {
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                    Log.d("fgsfds", "Try HTTP request in GetProxyList = " + url);
                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient.execute(httpGet);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    is = httpEntity.getContent();

                    Log.d("fgsfds", "Success HTTP request in GetProxyList");

                    //                Log.d("fgsfds", "Input stream after GET request to api.foxtools.ru = " + Utils.convertStreamToString(is));

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
            }
        });
        t.start();
        return localProxies;
    }
}