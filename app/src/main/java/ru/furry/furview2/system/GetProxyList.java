package ru.furry.furview2.system;


import android.content.Context;
import android.os.Handler;
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
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import ru.furry.furview2.GlobalData;

public class GetProxyList {
    Context context;
    InputStream is = null;
    String xmlRespond;
    GlobalData globalData;
    Boolean proxySearchingResult=false;
    Handler handler;

    public GetProxyList(Handler incomingHandler, Context incomingContext) {
        super();
        this.context = incomingContext;
        this.handler = incomingHandler;
        globalData = (GlobalData) context.getApplicationContext();
    }

    public Boolean GetingListOfProxies() {


        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                String url = "http://api.foxtools.ru/v2/Proxy.xml";

                // Adding parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("type", "2"));
                params.add(new BasicNameValuePair("available", "1"));
                params.add(new BasicNameValuePair("free", "1"));
                params.add(new BasicNameValuePair("uptime", "2"));
                Log.d("fgsfds", "Set param in GetProxyList before request");
                // http://api.foxtools.ru/v2/Proxy.xml?type=2&available=1&free=1&uptime=2


                //GET request
                try {
                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        String paramString = URLEncodedUtils.format(params, "utf-8");
                        url += "?" + paramString;
                        Log.d("fgsfds", "HTTP request in GetProxyList = " + url);
                        HttpGet httpGet = new HttpGet(url);

                        HttpResponse httpResponse = httpClient.execute(httpGet);
                        HttpEntity httpEntity = httpResponse.getEntity();
                        is = httpEntity.getContent();

        //                Log.d("fgsfds", "Input stream after GET request to api.foxtools.ru = " + Utils.convertStreamToString(is));

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(-1);
                }

                //Parsing XML
                Log.d("fgsfds", "Start DOM parser");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                List<ProxyItem> proxyElements = new ArrayList<ProxyItem>();
                try {
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    Document dom = builder.parse(is);
                    Element root = dom.getDocumentElement();
                    NodeList items = root.getElementsByTagName("item");
                    double fastern=2;
                    int fasternNum=0;
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
                            proxyElements.add(proxyElement);
                            //break; // if need only 1 proxy
                            if (proxyElement.getUptime()<fastern)
                                {
                                    fastern=proxyElement.getUptime();
                                    fasternNum=proxyElements.size()-1;
                                }
                        }
                    }
                    is.close();
                    Log.d("fgsfds", "Proxies found: " + proxyElements.size());

                    if (proxyElements.size()>0)
                    {
                        //Switch fastern proxy and proxy(0)
                        ProxyItem tmp = proxyElements.get(0);
                        proxyElements.set(0, proxyElements.get(fasternNum));
                        proxyElements.set(fasternNum, tmp);

                        //Set fastern proxy to the GlobalData
                        Log.d("fgsfds", "Set proxies in the globalData...");
                        globalData.setCurrentProxyItem(proxyElements.get(0));
                        globalData.setProxies(proxyElements);
                        globalData.setNumOfWorkingProxy(0);
                        globalData.setCurrentProxy(proxyElements.get(0).getIp(), proxyElements.get(0).getPort());
                        Log.d("fgsfds", "Fastern proxy is: " + proxyElements.get(0).getIp() + ":" + proxyElements.get(0).getPort() + " uptime: " + proxyElements.get(0).getUptime() + " from " + proxyElements.get(0).getCoutry());
                        handler.sendEmptyMessage(1);
                        proxySearchingResult=true;
                    }
                } catch (Exception e) {
                    //throw new RuntimeException(e);
                    handler.sendEmptyMessage(-1);
                    e.printStackTrace();
                }
            }
        });
        t.start();

        return proxySearchingResult;
    }

}