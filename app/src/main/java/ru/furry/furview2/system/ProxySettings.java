package ru.furry.furview2.system;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;

public class ProxySettings {

    static private ArrayList<Proxy> proxies = new ArrayList<Proxy>();

    {
        proxies.add(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("186.67.46.230", 8080))); // it's working... for now
        // see http://foxtools.ru/Proxy?al=False&am=False&ah=True&ahs=True&http=False&https=True
    }

    public static Proxy getProxy() {
        return proxies.get(0);
    }

    public static Proxy getLastProxy() {
        return proxies.get(0);
    }
}