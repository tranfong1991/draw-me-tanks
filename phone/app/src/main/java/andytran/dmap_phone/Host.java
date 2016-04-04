package andytran.dmap_phone;

/**
 * Created by Andy Tran on 4/4/2016.
 */
public class Host {
    private String name;
    private String ip;
    private int port;

    public Host(String name){
        this.name = name;
        this.ip = null;
        this.port = 0;
    }

    public Host(String name, String ip, int port){
        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public String getName() {
        return name;
    }

    public int getPort() {
        return port;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        Host host = (Host) o;
        return this.name.equals(host.name);
    }
}
