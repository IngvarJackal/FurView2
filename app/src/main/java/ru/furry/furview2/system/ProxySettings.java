package ru.furry.furview2.system;

import java.net.InetSocketAddress;
import java.net.Proxy;

public class ProxySettings {

    public static Proxy getProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("186.67.46.230", 8080)); // it's working... for now
        // see http://foxtools.ru/Proxy?al=False&am=False&ah=True&ahs=True&http=False&https=True
    }

}