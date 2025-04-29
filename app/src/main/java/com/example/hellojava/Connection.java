package com.example.hellojava;

public class Connection {
    private final String ip;
    private final int port;

    public Connection(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}