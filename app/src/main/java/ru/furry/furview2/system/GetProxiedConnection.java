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

import static junit.framework.Assert.assertEquals;


final public class GetProxiedConnection {

    private static List<Proxy> proxies = new ArrayList();
    private final static int PROXY_TIMEOUT = 2000;
    public static ProxyTypes proxyType = ProxyTypes.none;

    {
        HttpsURLConnection.setDefaultSSLSocketFactory(new NoSSLv3Factory());
    }

    public static HttpsURLConnection getProxiedConnection(URL url) throws IOException {
        HttpsURLConnection conn = null;
        //Checking using proxy
        switch (proxyType) {
            case foxtools:
                if (proxies.size() < 1) {
                    Log.d("fgsfds", "Start getting proxies.");
                    GetListProxies();
                    Log.d("fgsfds", "Set proxy in connection.");
                    conn = SetAndCheck(url);
                } else {
                    Log.d("fgsfds", "Set proxy in connection.");
                    conn = SetAndCheck(url);
                }
                break;
            case manual:
                conn = (HttpsURLConnection) url.openConnection();
                break;
            case antizapret:
                conn = (HttpsURLConnection) url.openConnection();
                break;
            case opera:
                conn = (HttpsURLConnection) url.openConnection();
                break;
            case none:
                conn = (HttpsURLConnection) url.openConnection();
                break;
        }
        return conn;
    }

    private static HttpsURLConnection SetAndCheck(URL testUrl) {
        HttpsURLConnection testConn = null;
        //testing HTTPS Connection
        while (testConn == null) {
            try {
                Log.d("fgsfds", "Try testing proxy " + proxies.get(0).address().toString());
                HttpsURLConnection urlConn = (HttpsURLConnection) testUrl.openConnection(proxies.get(0));
                urlConn.setConnectTimeout(PROXY_TIMEOUT);
                urlConn.connect();
                assertEquals(HttpsURLConnection.HTTP_OK, urlConn.getResponseCode());
                testConn = urlConn;
                Log.d("fgsfds", "A good proxy is found.");
            } catch (Exception e) {
                proxies.remove(0);
                Log.d("fgsfds", "Bad proxy. Not response after " + PROXY_TIMEOUT / 1000 + " sec. Removed. Proxies left: " + proxies.size());
                e.printStackTrace();
            }
        }
        return testConn;
    }

    private static void GetListProxies() {
        InputStream is = null;
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

            //Parsing XML
            Log.d("fgsfds", "Start DOM parser");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document dom = builder.parse(is);
            Element root = dom.getDocumentElement();
            NodeList items = root.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                ProxyItem proxyElement = new ProxyItem();
                Node item = items.item(i);
                NodeList properties = item.getChildNodes();
                for (int j = 0; j < properties.getLength(); j++) {
                    Node property = properties.item(j);
                    String name = property.getNodeName();
                    if (name.equalsIgnoreCase("ip")) {
                        proxyElement.setIp(property.getFirstChild().getNodeValue());
                    } else if (name.equalsIgnoreCase("port")) {
                        proxyElement.setPort(Integer.valueOf(property.getFirstChild().getNodeValue()));
                    } else if (name.equalsIgnoreCase("uptime")) {
                        proxyElement.setUptime(Double.valueOf(property.getFirstChild().getNodeValue()));
                    } else if (name.equalsIgnoreCase("country")) {
                        NodeList countryElements = property.getChildNodes();
                        for (int k = 0; k < countryElements.getLength(); k++) {
                            Node countryElement = countryElements.item(k);
                            String countryName = countryElement.getNodeName();
                            if (countryName.equalsIgnoreCase("iso3166a2")) {
                                proxyElement.setCoutry(countryElement.getFirstChild().getNodeValue());
                            }
                        }
                    }
                }
                //Checking for country
                if (!proxyElement.getCoutry().equals("RU")) {
                    //break; // if need only 1 proxy
                    proxies.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyElement.getIp(), proxyElement.getPort())));
                }
            }
            is.close();
            Log.d("fgsfds", "Proxies found: " + proxies.size());

        } catch (IOException e) {
            Log.d("fgsfds", "Fail HTTP request in GetProxyList");
            e.printStackTrace();
        } catch (Exception e) {
            Log.d("fgsfds", "Can't parse proxies.");
            e.printStackTrace();
        }
    }

}
