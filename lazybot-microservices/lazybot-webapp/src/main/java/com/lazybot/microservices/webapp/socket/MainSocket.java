package com.lazybot.microservices.webapp.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.HandshakeData;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;

@Service
public class MainSocket {
    private static final Logger log = LoggerFactory.getLogger(MainSocket.class);
    private Socket socket;

    public MainSocket(SocketIOServer server) throws URISyntaxException {
        this.socket = IO.socket("http://localhost:9093");
        this.socket.connect();
        server.addConnectListener(onConnected());
        server.addEventListener("chat", String.class, this::chat);
    }

    private ConnectListener onConnected() {
        System.out.println("Un client vient de se connecter :o");
        return client -> {
            HandshakeData handshakeData = client.getHandshakeData();
            log.debug("Client[{}] - Connected to chat module through '{}'", client.getSessionId().toString(), handshakeData.getUrl());
        };
    }

    public void chat(SocketIOClient client, String message, AckRequest ackSender) throws Exception {
        socket.emit("test");
        System.out.println(message);
    }
}