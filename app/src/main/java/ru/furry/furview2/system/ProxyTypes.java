package ru.furry.furview2.system;

import ru.furry.furview2.R;

public enum ProxyTypes {
    manual(R.string.proxy4),
    foxtools(R.string.proxy1),
    opera(R.string.proxy2),
    antizapret(R.string.proxy3),
    none(R.string.proxy5);


    private int value ;

    ProxyTypes ( int value )
    {
        this.value = value ;
    }

    public int getProxyStringId() {
        return value;
    }
}
