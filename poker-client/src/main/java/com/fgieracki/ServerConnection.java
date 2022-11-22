package com.fgieracki;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;


//Watek czytania danych z serwera
public class ServerConnection implements Runnable {
    private final SocketChannel serverSocket;
    private static final int BUFFER_SIZE = 1024;

    static Logger logger
            = Logger.getLogger(
            ServerConnection.class.getName());

    ServerConnection(SocketChannel socket) {
        this.serverSocket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ByteBuffer serverResponse = ByteBuffer.allocate(BUFFER_SIZE);

                int test = serverSocket.read(serverResponse);

                if (test == -1) {
                    logger.log(Level.INFO, "Server disconnected");
                    break;
                }

                serverResponse.flip();
                String data = new String(serverResponse.array()).trim();
                String msg = String.format("Server: %s", data);
                logger.log(Level.INFO, msg);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}