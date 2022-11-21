package com.fgieracki;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;


//Watek czytania danych z serwera
public class ServerConnection implements Runnable{
    private SocketChannel serverSocket;
    //recive information
    private BufferedReader in;
    //send information
    private static final int BUFFER_SIZE = 1024;

    ServerConnection(SocketChannel socket) throws IOException {
        this.serverSocket=socket;


    }
    @Override
    public void run() {
        try {
            while(true){
                ByteBuffer serverResponse=ByteBuffer.allocate(BUFFER_SIZE);

                int test = serverSocket.read(serverResponse);

                if(test == -1){
                    System.out.println("Server disconnected");
                    break;
                }

                serverResponse.flip();
                String data = new String(serverResponse.array()).trim();
                System.out.println(String.format("Server: %s",data));
//                serverResponse.clear();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}