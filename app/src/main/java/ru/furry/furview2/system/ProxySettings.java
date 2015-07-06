package ru.furry.furview2.system;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxySettings {

    static private List<Proxy> proxies = new ArrayList<>(Arrays.asList(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("112.25.10.146", 55336)))); // it's working... for now
    // see http://api.foxtools.ru/v2/Proxy.xml?type=2&available=1&free=1&uptime=2

    public static Proxy getProxy() {
        return proxies.get(0);
    }

    public static Proxy getLastProxy() {
        return proxies.get(0);
    }
}