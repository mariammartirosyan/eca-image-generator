package org.imageGenerationCloud.communication;

/**
 * Instance of each IP, which is created while the network is scanned
 */
public class MSInstance {
    private String ip;
    private int port;
    private String socketType;


    public MSInstance(String ip, int port, String socketType) {
        this.ip = ip;
        this.port = port;
        this.socketType = socketType;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getSocketType() {
        return socketType;
    }
}
