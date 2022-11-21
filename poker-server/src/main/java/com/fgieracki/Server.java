package com.fgieracki;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

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

                    if (key.isAcceptable() && usersCount < 4 && !gameStarted) {
                        processAcceptEvent(mySocket, key);
                    } else if (key.isAcceptable()) {
                        SocketChannel client = mySocket.accept();
                        client.write(ByteBuffer.wrap("Game has already started".getBytes()));
                        client.close();
                    }

                    else if (key.isReadable()) {
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
        myClient.register(selector, SelectionKey.OP_READ);
        System.out.println(connectedUsers.get(myClient) + " has joined the game. Players: "
                + Integer.toString(usersCount)+"/4");
        String message=connectedUsers.get(myClient) + " has joined the game. \nPlayers: "
                + Integer.toString(usersCount)+"/4\n";
        sendToAllUsers(myClient, message);
        serverToUser(myClient, "Welcome to the poker game! \n" +
                "Type '!ready <starting chips>' to start the game.");
    }

    private static void processReadEvent(SelectionKey key)
            throws IOException {
        // create a ServerSocketChannel to read the request
        SocketChannel myClient = (SocketChannel) key.channel();

//         Set up out 1k buffer to read data into
        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
//        clientResponse.flip();
        myClient.read(clientResponse);
        String data = new String(clientResponse.array()).trim();

        if (data.length() > 0) {
            System.out.println(connectedUsers.get(key.channel())+ ": " + data);
            validateCommands(myClient, data);
        }
    }

    private static int getPlayerId(SocketChannel myClient){
        //get player id from map
        String playerName = connectedUsers.get(myClient);
        return Integer.parseInt(playerName.substring(playerName.length() - 1)) - 1;
    }

    private static SocketChannel getUserByPlayerId(int playerId){
        //get player id from map
        String playerName = "Player " + Integer.toString(playerId+1);
        for (Map.Entry<SocketChannel, String> entry : connectedUsers.entrySet()) {
            if (entry.getValue().equals(playerName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void validateCommands(SocketChannel Author, String command){
        int playerId = getPlayerId(Author);
        if(command.startsWith("!ready") && !gameStarted) {
            int chips = 0;
            //check if chips number is valid
            try{
                chips = Integer.parseInt(command.substring(7));
            } catch (NumberFormatException e){
                serverToUser(Author, "Invalid number of chips! Try again.");
                return;
            }
            if(chips > 20){
                game.getPlayer(playerId).setChips(chips);
                game.getPlayer(playerId).setReady(true);
                serverToUser(Author, "You are ready to play!");
                sendToAllUsers(Author, "Player " + Integer.toString(playerId+1) + " is ready to play!");
                try {
                    tryToStartGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                serverToUser(Author, "Invalid number of chips! Try again.");
            }

            if(command.startsWith("!hand")){
                serverToUser(Author, game.getPlayer(playerId).getHand());
            }

            if(command.startsWith("!info")){
                serverToUser(Author, getPlayerInfo(playerId));
            }

            if(command.startsWith("!turn")){
                serverToUser(Author, "Current turn: Player " + Integer.toString(game.getPlayerTurn()+1) );
            }

            if(command.startsWith("!play")){
                if(game.getPlayerTurn() != getPlayerId(Author)){
                    serverToUser(Author, "It's not your turn!");
                }
                else{
                    //extract words from command
                    String[] words = command.split(" ");
                    playerMove(playerId, words);
                }
            }
        }

    }

    private static void playerMove(int playerId, String[] words){
        if(words.length == 1){
            serverToUser(getUserByPlayerId(playerId), "Invalid command! Try again.");
        }
        else{
            if(words[1].equals("check")){
                game.bet(playerId, Game.Decision.CHECK, 0);
            }
            else if(words[1].equals("fold")){
                game.bet(playerId, Game.Decision.FOLD, 0);
            }
            else if(words[1].equals("call")){
                if(game.getHighestBet() - game.getPlayerPot(playerId) < game.getPlayerChips(playerId)){
                    game.bet(playerId, Game.Decision.CALL, 0);
                }
                else{
                    serverToUser(getUserByPlayerId(playerId), "You don't have to call!");
                }
                game.bet(playerId, Game.Decision.CALL, 0);
            }
            else if(words[1].equals("raise")){
                if(words.length == 3){
                    try{
                        int raise = Integer.parseInt(words[2]);
                        if(raise > game.getHighestBet()
                                && ( raise - game.getPlayerPot(playerId) <= game.getPlayerChips(playerId)) ){
                            game.bet(playerId, Game.Decision.RAISE, raise);
                        }
                        else{
                            serverToUser(getUserByPlayerId(playerId), "Invalid raise! Try again.");
                        }
                    } catch (NumberFormatException e){
                        serverToUser(getUserByPlayerId(playerId), "Invalid number! Try again.");
                    }
                }
                else{
                    serverToUser(getUserByPlayerId(playerId), "Invalid command! Try again.");
                }
            }
            else{
                serverToUser(getUserByPlayerId(playerId), "Invalid command! Try again.");
            }
        }
    }

    private static void tryToStartGame() throws InterruptedException {
        if(!gameStarted && game.checkIfPlayersAreReady() && usersCount >= 2){
            gameStarted = true;
            game.startRound();
            sendToAllUsers(null, "Game started!\n");
            sendToAllUsers(null, "Game starts from Player "
                    + Integer.toString(game.getDealerId()+1) + "\n");
            sendPlayerInfoToUsers();
            game.nextPlayerTurn();
            while(!game.playBlind(game.getSmallBlindValue())){
                game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
                serverToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())),
                        "You have been eliminated!");
                game.nextPlayerTurn();
            }
            sendToAllUsers(null, "Small blind: " + Integer.toString(game.getSmallBlindValue())
                    + " has been played by: Player " + Integer.toString(game.getPlayerTurn()+1) + "\n");
            game.nextPlayerTurn();
            while(!game.playBlind(game.getBigBlindValue())){
                game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
                serverToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())),
                        "You have been eliminated!");
                game.nextPlayerTurn();
            }
            sendToAllUsers(null, "Big blind: " + Integer.toString(game.getBigBlindValue())
                    + " has been played by: Player " + Integer.toString(game.getPlayerTurn()+1) + "\n");
            game.nextPlayerTurn();
        }
    }

    private static void sendPlayerInfoToUsers(){
        connectedUsers.forEach((key, value) -> {
            serverToUser(key, getPlayerInfo(getPlayerId(key)));
        });
    }
    private static String getPlayerInfo(int playerId){
        return "\n\nYou have " + Integer.toString(game.getPlayer(playerId).getChips()) + " chips.\n" +
                "Current game pot: " + Integer.toString(game.getPot()) + " chips.\n" +
                "Your current pot: " + game.getPlayerPot(playerId) + "\n" +
                "Your Hand: " + game.getPlayer(playerId).getHand() + "\n";
    }

    private static void sendToAllUsers(SocketChannel messageAuthor, String message){
        String finalMessage ="" + message + "\n";
        connectedUsers.forEach((x, value)-> {
            try {
                ByteBuffer serverResponse = ByteBuffer.wrap(finalMessage.getBytes());
                if(x!=messageAuthor) {
                    x.write(serverResponse);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void serverToUser(SocketChannel user, String message){
        String finalMessage= message + "\n";
        try {
            ByteBuffer serverResponse = ByteBuffer.wrap(finalMessage.getBytes());
            user.write(serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}