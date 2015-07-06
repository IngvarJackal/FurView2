// Class to store global variables
package ru.furry.furview2;

import android.app.Application;
import android.util.Log;

import java.net.InetSocketAddress;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import ru.furry.furview2.system.ProxyItem;

public class GlobalData extends Application{
    private List<ProxyItem> proxies;
    private ProxyItem currentProxyItem;
    private int numOfWorkingProxy;
    private Proxy currentProxy;

    public GlobalData()
    {
        currentProxyItem=null;
        numOfWorkingProxy=0;
        currentProxy=null;
    }

    //proxies
    public void setProxies(List<ProxyItem> incomingProxies)
    {
        proxies=incomingProxies;
    }
    public List<ProxyItem> getProxies() {
        return proxies;
    }

    //currentProxyItem
    public void setCurrentProxyItem(ProxyItem incomingProxy) {
        currentProxyItem = incomingProxy;
    }
    public void setCurrentProxyItem(int numOfIncomingProxy) {
        currentProxyItem = proxies.get(numOfIncomingProxy);
    }
    public void setCurrentProxyItem(String ipAdress,int port,double uptime,String country) {
        ProxyItem tmp = new ProxyItem(ipAdress,port,uptime,country);
        currentProxyItem = tmp;
    }
    public ProxyItem getCurrentProxyItem() {
        return currentProxyItem;
    }

    //numOfWorkingProxy
    public void setNumOfWorkingProxy(int incomingNumOfWorkingProxy) {
        numOfWorkingProxy = incomingNumOfWorkingProxy;
    }
    public int getNumOfWorkingProxy() {
        return numOfWorkingProxy;
    }

    //currentProxy
    public void setCurrentProxy(Proxy IncomingCurrentProxy) {
        currentProxy = IncomingCurrentProxy;
    }
    public void setCurrentProxy(int number) {
        currentProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxies.get(number).getIp(), proxies.get(number).getPort()));
    }
    public void setCurrentProxy(String ipAdress,int port) {
        currentProxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ipAdress, port));
    }
    public Proxy getCurrentProxy() {
        return currentProxy;
    }
}


    /*
    *Example of use:
    *   GlobalData globalData;
    *   globalData = (GlobalData) context.getApplicationContext();
    *   ProxyItem pi = globalData.getCurrentProxy();
    */
