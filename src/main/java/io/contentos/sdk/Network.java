package io.contentos.sdk;

import java.util.Random;

public class Network {
    private final String host;
    private final int port;
    private final String name;

    public Network(String host, int port, String name) {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public Wallet wallet() {
        return new Wallet(this.host, this.port, this.name, false);
    }

    private static final String[] mainNodes = {
            "grpc.contentos.io",
    };

    private static final String[] testNodes = {
            "34.195.63.116",
            "34.193.131.213",
            "34.193.58.34",
    };

    public static final Network Main = new Network(
            mainNodes[new Random().nextInt(mainNodes.length)],
            8888,
            "main");

    public static final Network Test = new Network(
            testNodes[new Random().nextInt(testNodes.length)],
            8888,
            "test");
}
