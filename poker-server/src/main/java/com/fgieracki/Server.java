package com.fgieracki;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    //mapa wszystkich userow z ich unikatowymi kanalami
    public static Map<SocketChannel,String> connectedUsers = new HashMap<>();
    static int usersCount = 0;

    static boolean gameStarted=false;

    private static Game game;

    public static void main(String[] args) {
        System.out.println("starting server");
        try {
            InetAddress hostIP = InetAddress.getLocalHost();
            int port = 9999;


            selector = Selector.open();
            ServerSocketChannel mySocket = ServerSocketChannel.open();
            ServerSocket serverSocket = mySocket.socket();
            InetSocketAddress address = new InetSocketAddress(hostIP, port);
            serverSocket.bind(address);

            mySocket.configureBlocking(false);
            int ops = mySocket.validOps();
            mySocket.register(selector, ops, null);

            System.out.println(String.format("Server started!\nlistening to connections on %s:%d...",
                    hostIP.getHostAddress(), port));

            boolean running = true;

            game = new Game(10);


            while (running) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();


                    if (key.isAcceptable() && usersCount < 4 && gameStarted == false) {
                        processAcceptEvent(mySocket, key);
                    } else if (key.isReadable() && usersCount >= 1) {
                        processReadEvent(key);
                    }
                    i.remove();
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

//    else if (key.isReadable()) {
//        processReadEvent(key); // process read event
//
//    }

    private static void processAcceptEvent(ServerSocketChannel mySocket,
                                           SelectionKey key) throws IOException {
        usersCount+=1;
        game.addPlayer();

        // Accept the connection and make it non-blocking
        SocketChannel myClient = mySocket.accept();
        myClient.configureBlocking(false);
        //add accepted user socket to map to differ users
        connectedUsers.put(myClient, "Player " + Integer.toString(usersCount));

        // Register interest in reading this channel
        myClient.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
        System.out.println(connectedUsers.get(myClient) + " has joined the game. Players: " + Integer.toString(usersCount)+"/4");
        String message=connectedUsers.get(myClient) + " has joined the game. \n Players: " + Integer.toString(usersCount)+"/4\n";
        sendToAllUsers(myClient, message);
        serverToUser(myClient, "Welcome to the poker game! \n Type '!ready <starting chips>' to start the game.\n");

    }

    private static void processReadEvent(SelectionKey key)
            throws IOException {
        // create a ServerSocketChannel to read the request
        SocketChannel myClient = (SocketChannel) key.channel();

        // Set up out 1k buffer to read data into
        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
        myClient.read(clientResponse);
        String data = new String(clientResponse.array()).trim();
        if (data.length() > 0) {
            System.out.println(connectedUsers.get(key.channel())+ ": " + data);
            validateCommands(myClient, data);
//            if(data.startsWith("/all")){
//                sendToAllUsers(myClient, data.substring(4));
//            }
//            if (data.equalsIgnoreCase("!exit")) {
//                myClient.close();
//                System.out.println("closing server");
//            }
//            clientResponse.clear();
        }


    }

    private static int getPlayerId(SocketChannel myClient){
        //get player id from map
        String playerName = connectedUsers.get(myClient);
        return Integer.parseInt(playerName.substring(playerName.length() - 1)) - 1;
    }

    private static void validateCommands(SocketChannel Author, String command){
        int playerId = getPlayerId(Author);
        if(command.startsWith("!ready") && !gameStarted) {
            int chips = 0;
            //check if chips number is valid
            try{
                chips = Integer.parseInt(command.substring(7));
            } catch (NumberFormatException e){
                serverToUser(Author, "Invalid number of chips! Try again.\n");
                return;
            }
            if(chips > 20){
                game.getPlayer(playerId).setChips(chips);
                game.getPlayer(playerId).setReady(true);
                serverToUser(Author, "You are ready to play!");
//                sendToAllUsers(Author, "Player " + Integer.toString(playerId) + " is ready to play!");
                tryToStartGame();
            }
            else{
                serverToUser(Author, "Invalid number of chips! Try again.\n");
            }
        }

    }

    private static void tryToStartGame(){
        if(gameStarted == false && game.checkIfPlayersAreReady()){
            gameStarted = true;
            game.startRound();
            sendToAllUsers(null, "Game started!\n");


        }
    }

    private static String getPlayerInfo(int playerId){
        return "You have " + Integer.toString(game.getPlayer(playerId).getChips()) + " chips.\n" +
                "Your current pot: " + game.getPlayerPot(playerId) + "\n" +
                "Your Hand: " + game.getPlayer(playerId).getHand() + "\n";
    }

    private static void sendToAllUsers(SocketChannel messageAuthor, String message){



        String finalMessage ="Server: " + message;
        connectedUsers.forEach((x, value)-> {
            try {
                ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
                serverResponse.clear();
                serverResponse.put(finalMessage.getBytes());
                serverResponse.flip();
                if(x!=messageAuthor) {
                    x.write(serverResponse);
                }
//                serverResponse.clear();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static void serverToUser(SocketChannel user, String message){
        String finalMessage= "\n" + message + "\n";
        System.out.println(finalMessage);
        try {
            ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
            serverResponse.put(finalMessage.getBytes());
            serverResponse.flip();
            user.write(serverResponse);
            serverResponse.clear();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private static void serverToAll(String message){
//        String finalMessage=message+"\n /all to speak to all users\n";
//        connectedUsers.forEach((x, value)-> {
//            try {
//                ByteBuffer serverResponse= ByteBuffer.allocate(BUFFER_SIZE);
//                serverResponse.put(finalMessage.getBytes());
//                serverResponse.flip();
//                x.write(serverResponse);
//
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        });
//
//    }


}