package ru.furry.furview2.system;

public class ProxyItem {

    public String ip;
    public int port;
    public String uptime;
    public String coutry;

        //constructor
        public ProxyItem(String ip, int port, String uptime, String coutry) {
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

    public void setPort(int port) {
        this.port = port;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public void setCoutry(String coutry) {
        this.coutry = coutry;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getUptime() {
        return uptime;
    }

    public String getCoutry() {
        return coutry;
    }

}

