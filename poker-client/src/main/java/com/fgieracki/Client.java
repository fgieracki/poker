package com.fgieracki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    static Logger logger
            = Logger.getLogger(
            Client.class.getName());

    public static void main(String[] args) {

        logger.log(Level.INFO, "Starting server...");
        try {
            int port = 9999;
            InetAddress hostIP = InetAddress.getLocalHost();
            InetSocketAddress myAddress =
                    new InetSocketAddress(hostIP, port);
            SocketChannel myClient = SocketChannel.open(myAddress);

            BufferedReader keyboardReader = new BufferedReader(new InputStreamReader(System.in));
            String msg = String.format("Trying to connect to %s:%d...",
                    myAddress.getHostName(), myAddress.getPort());
            logger.log(Level.INFO, msg);
            ServerConnection server = new ServerConnection(myClient);
            //tworzymy watek czytania z serwera
            new Thread(server).start();
            while (true) {
                String inputString = keyboardReader.readLine();
                ByteBuffer myBuffer = ByteBuffer.wrap(inputString.getBytes());
                myClient.write(myBuffer);
                if (inputString.equals("exit")) break;
            }


            logger.log(Level.INFO, "Closing Client connection...");
            myClient.close();
        } catch (IOException e) {
            logger.log(Level.INFO, e.getMessage());
        }
    }


}