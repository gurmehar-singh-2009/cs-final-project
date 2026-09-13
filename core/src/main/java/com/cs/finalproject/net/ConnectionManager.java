package com.cs.finalproject.net;

import com.badlogic.gdx.Gdx;
import com.github.satori87.gdx.webrtc.WebRTCClients;
import com.github.satori87.gdx.webrtc.WebRTCConfiguration;
import com.github.satori87.gdx.webrtc.WebRTCServer;
import com.github.satori87.gdx.webrtc.WebRTCServerListener;

//import com.github.tommyettinger.

public class ConnectionManager {
    private WebRTCServer server;
    private WebRTCConfiguration webRTCConfiguration;

    public ConnectionManager(String ip) {
        webRTCConfiguration = new WebRTCConfiguration();
        webRTCConfiguration.signalingServerUrl = "ws" + ip + ":8080";

        server = WebRTCClients.newServer(webRTCConfiguration, new WebRTCServerListener() {
            @Override
            public void onStarted(int i) {
                System.out.println("SERVER STARTED");
            }

            @Override
            public void onClientConnected(final int clientId) {
                System.out.println("CLIENT " + clientId + " CONNECTED");
            }

            @Override
            public void onClientDisconnected(final int clientId) {
                System.out.println("CLIENT " + clientId + " DISCONNECTED");
            }

            @Override
            public void onClientMessage(int i, byte[] bytes, boolean b) {
                Gdx.app.postRunnable(new Runnable() {
                    @Override
                    public void run() {
                        // stupid stupid java
//                        var message =
                    }
                });
            }

            @Override
            public void onError(String s) {

            }
        });
    }
}
