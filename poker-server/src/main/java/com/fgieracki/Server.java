package com.fgieracki;

import java.io.Console;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;

public class Server {
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    //mapa wszystkich userow z ich unikatowymi kanalami
    public static Map<SocketChannel,String> connectedUsers = new HashMap<>();
    static int usersCount = 0;

    static boolean gameStarted=false;
    static boolean firstBettingRound =false;
    static boolean drawRound=false;

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
        sendToUser(myClient, "Welcome to the poker game! \n" +
                "Type '!ready <starting chips>' to start the game.");
    }

    private static void processReadEvent(SelectionKey key)
            throws IOException {
        SocketChannel myClient = (SocketChannel) key.channel();

        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
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
        String[] words = command.split(" ");
        if(command.startsWith("!ready") && !gameStarted) {
            int chips = 0;
            //check if chips number is valid
            try{
                chips = Integer.parseInt(command.substring(7));
            } catch (NumberFormatException e){
                sendToUser(Author, "Invalid number of chips! Try again.");
                return;
            }
            if(chips > 20){
                game.getPlayer(playerId).setChips(chips);
                game.getPlayer(playerId).setReady(true);
                sendToUser(Author, "You are ready to play!");
                sendToAllUsers(Author, "Player " + Integer.toString(playerId+1) + " is ready to play!");
                try {
                    startGame();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else{
                sendToUser(Author, "Invalid number of chips! Try again.");
            }
        } else if(command.startsWith("!hand")){
            sendToUser(Author, game.getPlayer(playerId).getHand());
        } else if(command.startsWith("!info")){
            sendToUser(Author, getPlayerInfo(playerId));
        } else if(command.startsWith("!turn")){
            sendToUser(Author, "Current turn: Player " + Integer.toString(game.getPlayerTurn()+1) );
        } else if(command.startsWith("!bet") && firstBettingRound){
            if(game.getPlayerTurn() != getPlayerId(Author)){
                sendToUser(Author, "It's not your turn!");
            } else{
                playerBet(Author, words);

                if(game.getPlayerTurn() == game.getLastPlayerAction()){
                    firstBettingRound = false;
                    game.setPlayerTurn(game.getDealer()-1);
                    sendToAllUsers(Author, "First betting round finished!\nStarting drawing round...");
                    sendToAllUsers(Author, "Current turn: Player " + Integer.toString(game.getPlayerTurn()+1) );
                    drawRound = true;
                }
            }
        }
        else if(command.startsWith("!help")){
            //TODO: add help
        }

    }


    private static void playerBet(SocketChannel author, String[] words){
        int playerId = getPlayerId(author);
        if(words.length == 1){
            sendToUser(author, "Invalid command! Try again.");
        }
        else{
            switch (words[1]) {
                case "check":
                    game.bet(playerId, Game.Decision.CHECK, 0);
                    sendToAllUsers(author, "Player " + Integer.toString(playerId+1) + " checked.");
                    nextTurn();
                    break;
                case "fold":
                    game.bet(playerId, Game.Decision.FOLD, 0);
                    sendToAllUsers(author, "Player " + Integer.toString(playerId+1) + " folded.");
                    nextTurn();
                    break;
                case "call":
                    if (game.getHighestBet() - game.getPlayerPot(playerId) < game.getPlayerChips(playerId)) {
                        game.bet(playerId, Game.Decision.CALL, 0);
                        sendToAllUsers(author, "Player " + Integer.toString(playerId+1) + " called.");
                        nextTurn();
                    } else {
                        sendToUser(author, "You don't have enough chips to call!");
                    }
                    break;
                case "raise":
                    if (words.length == 3) {
                        try {
                            int raise = Integer.parseInt(words[2]);
                            if (raise > game.getHighestBet()
                                    && (raise - game.getPlayerPot(playerId) <= game.getPlayerChips(playerId))) {
                                game.bet(playerId, Game.Decision.RAISE, raise);
                                sendToAllUsers(author, "Player " + Integer.toString(playerId+1) + " raised to " + Integer.toString(raise) + ".");
                                nextTurn();
                            } else {
                                sendToUser(author, "Invalid raise! Try again.");
                            }
                        } catch (NumberFormatException e) {
                            sendToUser(author, "Invalid number! Try again.");
                        }
                    } else {
                        sendToUser(author, "Invalid command! Try again.");
                    }
                    break;
                case "allin":
                    game.bet(playerId, Game.Decision.ALL_IN, 0);
                    sendToAllUsers(author, "Player " + Integer.toString(playerId+1) + " went all in.");
                    nextTurn();
                    break;
                default:
                    sendToUser(author, "Invalid command! Try again.");
                    break;
            }
        }
    }

    private static void startGame() throws InterruptedException {
        if(!gameStarted && game.checkIfPlayersAreReady() && usersCount >= 2){
            gameStarted = true;
            game.startRound();
            firstBettingRound = true;
            sendToAllUsers(null, "Game started!\n");
            sendPlayerInfoToUsers();
            sendToAllUsers(getUserByPlayerId(game.getDealerId()), "Dealer: Player "
                    + Integer.toString(game.getDealerId()+1) + "\n");
            sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getDealerId())), "Game starts from you!\n");

            nextTurn();

            playSmallBlind();
            nextTurn();
            playBigBlind();
            nextTurn();
        }
    }



    private static void playSmallBlind(){
        while(!game.playBlind(game.getSmallBlindValue())){
            game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
            sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())),
                    "You have been eliminated!");
            nextTurn();
        }
        sendToAllUsers(null, "Small blind: " + Integer.toString(game.getSmallBlindValue())
                + " has been played by: Player " + Integer.toString(game.getPlayerTurn()+1) + "\n");
    }

    private static void playBigBlind(){
        while(!game.playBlind(game.getBigBlindValue())){
            game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
            sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())),
                    "You have been eliminated!");
            nextTurn();
        }
        sendToAllUsers(null, "Big blind: " + Integer.toString(game.getBigBlindValue())
                + " has been played by: Player " + Integer.toString(game.getPlayerTurn()+1) + "\n");
    }

    private static void nextTurn(){
        System.out.println("Game started!");
        game.nextPlayerTurn();
        sendToAllUsers(getUserByPlayerId(game.getPlayerTurn()), "Current turn: Player "
                + Integer.toString(game.getPlayerTurn()+1) + "\n");
        sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())), "Your turn!");
    }
    private static void sendPlayerInfoToUsers(){
        connectedUsers.forEach((key, value) -> {
            sendToUser(key, getPlayerInfo(getPlayerId(key)));
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

    private static void sendToUser(SocketChannel user, String message){
        String finalMessage= message + "\n";
        try {
            ByteBuffer serverResponse = ByteBuffer.wrap(finalMessage.getBytes());
            user.write(serverResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}