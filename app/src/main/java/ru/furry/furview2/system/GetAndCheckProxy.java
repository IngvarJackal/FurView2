package ru.furry.furview2.system;

import android.util.Log;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;

import ru.furry.furview2.InitialScreen;


final public class GetAndCheckProxy { //public ???

    private static List<Proxy> proxies = new ArrayList();


    public final static Proxy getProxy() {
        if (InitialScreen.useProxy)
        {
            Proxy returnedProxy = null;
            if (proxies.size()<1) {
/*
                GetProxyList getManyProxies = new GetProxyList();
                proxies = getManyProxies.GetingListOfProxies();

                Log.d("fgsfds", "This message before getting proxy ... not good.");
                Log.d("fgsfds", "Receive proxies: " + proxies.size());
                RenewProxy check = new RenewProxy();
                proxies = check.renew(proxies);
                Log.d("fgsfds", "Proxies after checking: " + proxies.size());
                returnedProxy = proxies.get(0);
*/
                return returnedProxy;
            }
            else{
                return proxies.get(0);
            }
        }
        else return null;


    }

}
