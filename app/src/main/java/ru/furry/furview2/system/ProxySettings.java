package ru.furry.furview2.system;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProxySettings {

    static private List<Proxy> proxies = new ArrayList<>(Arrays.asList(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("183.63.149.117", 80)))); // it's working... for now
    // see http://foxtools.ru/Proxy?al=False&am=False&ah=True&ahs=True&http=False&https=True

    public static Proxy getProxy() {
        return proxies.get(0);
    }

    public static Proxy getLastProxy() {
        return proxies.get(0);
    }
}