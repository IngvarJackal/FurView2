package ru.furry.furview2.proxy;

import ru.furry.furview2.R;

public enum ProxyTypes {
    manual(R.string.proxy3),
    foxtools(R.string.proxy2),
    antizapret(R.string.proxy1),
    none(R.string.proxy4);


    private int value ;

    ProxyTypes ( int value )
    {
        this.value = value ;
    }

    public int getProxyStringId() {
        return value;
    }
}
