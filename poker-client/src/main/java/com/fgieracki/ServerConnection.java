package com.fgieracki;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;


//Watek czytania danych z serwera
public class ServerConnection implements Runnable {
    private final SocketChannel serverSocket;
    protected static final int BUFFER_SIZE = 1024;

    static Logger logger
            = Logger.getLogger(
            ServerConnection.class.getName());

    ServerConnection(SocketChannel socket) {
        this.serverSocket = socket;
    }

    @Override
    public void run() {
            while (true) {
                ByteBuffer serverResponse = ByteBuffer.allocate(BUFFER_SIZE);

                int test = 0;
                try {
                    test = serverSocket.read(serverResponse);
                }
                catch (Exception e) {
                    logger.log(Level.SEVERE, "Server disconnected!");
                    return;
                }


                if (test == -1) {
                    logger.log(Level.SEVERE, "Server disconnected");
                    break;
                }

                serverResponse.flip();
                String data = new String(serverResponse.array()).trim();
                String msg = String.format("Server: %s", data);
                logger.log(Level.INFO, msg);

            }
    }
}