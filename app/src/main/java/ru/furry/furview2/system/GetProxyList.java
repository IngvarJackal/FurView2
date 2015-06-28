package ru.furry.furview2.system;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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

import ru.furry.furview2.system.ProxyItem;
import ru.furry.furview2.InitialScreen;

public class GetProxyList extends AsyncTask<Void, Void, List<ProxyItem>> {
    Context context;
    InputStream is = null;
    String xmlRespond;

    public GetProxyList(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<ProxyItem> doInBackground(Void... params) {
        return loadXML();
    }

    public List<ProxyItem> loadXML() {
        String url = "http://api.foxtools.ru/v2/Proxy.xml";

        // Adding parameters
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("type", "2"));
        params.add(new BasicNameValuePair("available", "1"));
        params.add(new BasicNameValuePair("free", "1"));
        params.add(new BasicNameValuePair("uptime", "2"));
        Log.d("fgsfds", "Set param in GetProxyList before request");

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
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
            Log.d("fgsfds", "proxys found: " + proxyElements.size());
            //Switch fastern proxy and proxy(0)
            if (proxyElements.size()>0)
            {
                ProxyItem tmp = proxyElements.get(0);
                proxyElements.set(0, proxyElements.get(fasternNum));
                proxyElements.set(fasternNum, tmp);
            }
            Log.d("fgsfds", "Fastern proxy is: "+proxyElements.get(0).getUptime()+" from "+proxyElements.get(0).getCoutry());
        } catch (Exception e) {
            //throw new RuntimeException(e);
            e.printStackTrace();
        }
        return proxyElements;
    }

    @Override
    protected void onPostExecute(List<ProxyItem> proxyElements) {
        ((InitialScreen) context).ProxiList(proxyElements);
    }

}