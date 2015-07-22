package ru.furry.furview2.proxy;

public class ProxyItem {

    public String ip;
    public int port;
    public double uptime;
    public String coutry;

    //constructor
    public ProxyItem(String ip, int port, double uptime, String coutry) {
        this.ip = ip;
        this.port = port;
        this.uptime = uptime;
        this.coutry = coutry;
    }

    //constructor
    public ProxyItem() {

    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setUptime(double uptime) {
        this.uptime = uptime;
    }

    public double getUptime() {
        return uptime;
    }

    public void setCoutry(String coutry) {
        this.coutry = coutry;
    }

    public String getCoutry() {
        return coutry;
    }

}