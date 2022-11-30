package com.fgieracki;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    protected static final int BUFFER_SIZE = 1024;
    protected static Selector selector = null;
    //mapa wszystkich userow z ich unikatowymi kanalami
    protected static Map<SocketChannel, String> connectedUsers = new HashMap<>();
    static int usersCount = 0;

    static boolean gameStarted = false;
    static boolean firstBettingRound = false;
    static boolean drawRound = false;

    static boolean secondBettingRound = false;

    protected static String notYourTurnWarning = "It's not your turn!";
    protected static String uselessPlayerString = "Player: ";
    protected static String uselessCurrentTurnPlayer = "Current turn: Player ";
    protected static String uselessInvalidCommand = "Invalid command! Try again!";
    protected static Game game;

    protected static boolean running = true;

    protected static int maxPlayers = 4;

    static Logger logger
            = Logger.getLogger(
            Server.class.getName());

    public static void main(String[] args) {
        if(args.length > 0) {
            maxPlayers = Integer.parseInt(args[0]);
        }

        logger.log(Level.INFO, "starting server");
        String msg = String.format("Max players: %d", maxPlayers);
        logger.log(Level.INFO, msg);

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


            game = new Game(10);

            while (running) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();

                    if (key.isAcceptable() && usersCount < maxPlayers && !gameStarted) {
                        processAcceptEvent(mySocket);
                    } else if (key.isAcceptable()) {
                        SocketChannel client = mySocket.accept();
                        client.write(ByteBuffer.wrap("Game has already started".getBytes()));
                        client.close();
                    } else if (key.isReadable()) {
                        processReadEvent(key);
                    }
                    i.remove();
                }
            }
            mySocket.close();
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());

            Thread.currentThread().interrupt();
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());

            }
        }
    }


    protected static void processAcceptEvent(ServerSocketChannel mySocket) throws IOException {
        usersCount += 1;
        game.addPlayer();
        // Accept the connection and make it non-blocking
        SocketChannel myClient = mySocket.accept();
        myClient.configureBlocking(false);
        //add accepted user socket to map to differ users
        connectedUsers.put(myClient, uselessPlayerString + Integer.toString(usersCount));

        // Register interest in reading this channel
        myClient.register(selector, SelectionKey.OP_READ);
        String message = connectedUsers.get(myClient) + " has joined the game. Players: "
                + Integer.toString(usersCount) + "/" + maxPlayers;
        logger.log(Level.INFO, message);
        message = connectedUsers.get(myClient) + " has joined the game. \nPlayers: "
                + Integer.toString(usersCount) + "/" + maxPlayers + "\n";
        sendToAllUsers(myClient, message);
        sendToUser(myClient, "Welcome to the poker game! \n" +
                "Type '!ready <starting chips>' to start the game.");
    }

    protected static void processReadEvent(SelectionKey key) {
        SocketChannel myClient = (SocketChannel) key.channel();

        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
        try{
            myClient.read(clientResponse);
        }
        catch (IOException e){
            logger.log(Level.WARNING, e.getMessage());
            usersCount--;
            String msg = connectedUsers.get(myClient) + " has left the game. Players: "
                    + Integer.toString(usersCount) + "/"+maxPlayers;
            sendToAllUsersByPlayerId(-1, msg);
            logger.log(Level.INFO, msg);
            game.removePlayer(getPlayerId(myClient));
            connectedUsers.remove(myClient);

            //change names of connected users
            for (SocketChannel user : connectedUsers.keySet()) {
                String newPlayerName = uselessPlayerString + Integer.toString(getPlayerId(user));
                connectedUsers.replace(user, newPlayerName);
            }

            key.cancel();
            return;
        }
        String data = new String(clientResponse.array()).trim();

        if (data.length() > 0) {
            String message = connectedUsers.get(myClient) + ": " + data;
            logger.log(Level.INFO, message);
            validateCommands(getPlayerId(myClient), data);
        }
    }

    protected static void validateCommands(int playerId, String command) {
        String[] words = command.split(" ");
        if (command.startsWith("!ready") && !gameStarted) {
            handleReadyCommand(playerId, words);
        } else if (command.startsWith("!hand")) {
            sendToUserByPlayerId(playerId, game.getPlayer(playerId).getHandToString());
        } else if (command.startsWith("!info")) {
            sendToUserByPlayerId(playerId, getPlayerInfo(playerId));
        } else if (command.startsWith("!turn")) {
            sendToUserByPlayerId(playerId, uselessCurrentTurnPlayer + Integer.toString(game.getPlayerTurn() + 1));
        } else if (command.startsWith("!bet") && (firstBettingRound)) {
            handleBetForFirstRoundCommand(playerId, words);

        } else if (command.startsWith("!draw") && drawRound && (game.getDrawCounter() != game.playersPlaying())) {
            handleDrawingRound(playerId, words);
        } else if (command.startsWith("!bet") && (secondBettingRound)) {
            handleBetForSecondRoundCommand(playerId, words);
        } else if (command.startsWith("!!stop")) {
            running = false;
            sendToAllUsersByPlayerId(-1, "Game has been stopped by admin");
        }

    }
    protected static int getPlayerId(SocketChannel myClient) {
        //get player id from map
        String playerName = connectedUsers.get(myClient);
        return Integer.parseInt(playerName.substring(playerName.length() - 1)) - 1;
    }

    protected static SocketChannel getUserByPlayerId(int playerId) {
        //get player id from map
        String playerName = uselessPlayerString + Integer.toString(playerId + 1);
        for (Map.Entry<SocketChannel, String> entry : connectedUsers.entrySet()) {
            if (entry.getValue().equals(playerName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    protected static void handleReadyCommand(int playerId, String[] command) {
        int chips = 0;
        try {
            chips = Integer.parseInt(command[1]);
        } catch (NumberFormatException e) {
            sendToUserByPlayerId(playerId, "Invalid number of chips! Try again.");
            return;
        }
        if (chips > 20) {
            game.getPlayer(playerId).setChips(chips);
            game.getPlayer(playerId).setReady(true);
            sendToUserByPlayerId(playerId, "You are ready to play!");
            sendToAllUsersByPlayerId(playerId, uselessPlayerString
                    + Integer.toString(playerId + 1) + " is ready to play!");
            startGame();

        } else {
            sendToUserByPlayerId(playerId, "Invalid number of chips! Try again.");
        }
    }


    protected static void handleBetForFirstRoundCommand(int playerId, String[] command) {
        if (game.getPlayerTurn() != playerId) {
            sendToUserByPlayerId(playerId, notYourTurnWarning);
        } else {
            playerBet(playerId, command);
            if (game.playersPlaying() == 1) {
                handleSingleWinner();
            }
            if (game.getPlayerTurn() == game.getLastPlayerAction()) {
                firstBettingRound = false;
                game.nextPlayerTurn();
                sendToAllUsersByPlayerId(-1, "First betting round finished!\nStarting drawing round...");
                sendToAllUsersByPlayerId(-1, uselessCurrentTurnPlayer + Integer.toString(game.getPlayerTurn() + 1));
                sendToUserByPlayerId(game.getPlayerTurn(), "It's your turn! Type '!draw <card numbers>' to draw cards.");
                drawRound = true;
            }
        }
    }

    protected static void handleDrawingRound(int playerId, String[] command) {
        if (game.getPlayerTurn() != playerId) {
            sendToUserByPlayerId(playerId, notYourTurnWarning);
        } else {
            playerDraw(playerId, command);
            if (game.getDrawCounter() == game.playersPlaying()) {
                drawRound = false;
                game.nextPlayerTurn();
                sendToAllUsersByPlayerId(playerId, "Drawing round finished!\nStarting second betting round...");
                sendToAllUsersByPlayerId(playerId, uselessCurrentTurnPlayer + Integer.toString(game.getPlayerTurn() + 1));
                secondBettingRound = true;
                game.setLastPlayerAction((game.getDealer() - 1 + usersCount) % usersCount);
            }
        }
    }

    protected static void handleSingleWinner() {
        sendToAllUsersByPlayerId(-1, "\n\n\nPlayer " + Integer.toString(game.getPlayerTurn() + 1) + " won the game!\n\n\n");
        game.getWinner();
        gameStarted = false;
        firstBettingRound = false;
        drawRound = false;
        secondBettingRound = false;
        startGame();
    }

    protected static void handleBetForSecondRoundCommand(int playerId, String[] command) {
        if (game.getPlayerTurn() != playerId) {
            sendToUserByPlayerId(playerId, notYourTurnWarning);
        } else {
            playerBet(playerId, command);

            if (game.playersPlaying() == 1) {
                handleSingleWinner();
            }

            if (game.getPlayerTurn() == game.getLastPlayerAction()) {
                secondBettingRound = false;
                game.nextPlayerTurn();
                sendToAllUsersByPlayerId(playerId, "Second betting round finished!\nChecking winner...");
                int winner = game.getWinner();
                sendToAllUsersByPlayerId(playerId, "\n\n\n" + uselessPlayerString + Integer.toString(winner + 1) + " won the game!\n\n\n");
            }
        }
    }

    protected static void playerDraw(int playerId, String[] words) {
        if (words.length > 5) {
            sendToUserByPlayerId(playerId, "Too many cards to draw! Try again.\nMaximum cards to draw is maxPlayers.");
            return;
        }
        for (int i = 1; i < words.length; i++) {
            try {
                int cardId = Integer.parseInt(words[i]);
                if (cardId > 5 || cardId < 1) {
                    if (cardId == 0) {
                        sendToUserByPlayerId(playerId, "Skipped drawing!");
                    }
                    sendToUserByPlayerId(playerId, "Invalid card number! Try again.\nCard number must be between 1 and 5.");
                    return;
                }
            } catch (NumberFormatException e) {
                sendToUserByPlayerId(playerId, "Invalid card number! Try again.\nCard number must be between 1 and 5.");
                return;
            }
        }
        for (int i = 1; i < words.length; i++) {
            int cardId = Integer.parseInt(words[i]);
            game.getPlayer(playerId).replaceCard(cardId - 1, game.getDeck().getTopCard());
        }
        game.addDraw();
        sendToAllUsersByPlayerId(playerId, uselessPlayerString + Integer.toString(playerId + 1) + " has drawn " + Integer.toString(words.length - 1) + " cards.");
        sendToUserByPlayerId(playerId, getPlayerInfo(playerId));
        nextTurn();
    }

    protected static void handleRaiseBet(int playerId, String[] words) {
        if (words.length == 3) {
            try {
                int raise = Integer.parseInt(words[2]);
                if (raise > 2 * game.getHighestBet()
                        && (raise - game.getPlayerPot(playerId) <= game.getPlayerChips(playerId))) {
                    game.bet(playerId, Game.Decision.RAISE, raise);
                    sendToAllUsersByPlayerId(playerId, uselessPlayerString + Integer.toString(playerId + 1) + " raised to " + Integer.toString(raise) + ".");
                    nextTurn();
                } else {
                    sendToUserByPlayerId(playerId, "Invalid raise! Try again.");
                }
            } catch (NumberFormatException e) {
                sendToUserByPlayerId(playerId, "Invalid number! Try again.");
            }
        } else {
            sendToUserByPlayerId(playerId, uselessInvalidCommand);
        }
    }

    protected static void playerBet(int playerId, String[] words) {
        if (words.length == 1) {
            sendToUserByPlayerId(playerId, uselessInvalidCommand);
        } else {
            switch (words[1]) {
                case "check":
                    game.bet(playerId, Game.Decision.CHECK, 0);
                    sendToAllUsersByPlayerId(playerId, uselessPlayerString + Integer.toString(playerId + 1) + " checked.");
                    nextTurn();
                    break;
                case "fold":
                    game.bet(playerId, Game.Decision.FOLD, 0);
                    sendToAllUsersByPlayerId(playerId, uselessPlayerString + Integer.toString(playerId + 1) + " folded.");
                    nextTurn();
                    break;
                case "call":
                    if (game.getHighestBet() - game.getPlayerPot(playerId) < game.getPlayerChips(playerId)) {
                        game.bet(playerId, Game.Decision.CALL, 0);
                        sendToAllUsersByPlayerId(playerId, uselessPlayerString + Integer.toString(playerId + 1) + " called.");
                        nextTurn();
                    } else {
                        sendToUserByPlayerId(playerId, "You don't have enough chips to call!");
                    }
                    break;
                case "raise":
                    handleRaiseBet(playerId, words);
                    break;
                case "allin":
                    game.bet(playerId, Game.Decision.ALL_IN, 0);
                    sendToAllUsersByPlayerId(playerId, uselessPlayerString + Integer.toString(playerId + 1) + " went all in.");
                    nextTurn();
                    break;
                default:
                    sendToUserByPlayerId(playerId, uselessInvalidCommand);
                    break;
            }
        }
    }

    protected static void startGame() {
        if (!gameStarted && game.checkIfPlayersAreReady() && usersCount >= 2) {
            gameStarted = true;
            game.startRound();
            firstBettingRound = true;
            secondBettingRound = false;
            drawRound = false;
            sendToAllUsersByPlayerId(-1, "Game started!\n");
            sendPlayerInfoToUsers();
            sendToAllUsersByPlayerId(game.getDealer(), "Dealer: Player "
                    + Integer.toString(game.getDealer() + 1) + "\n");
            sendToUserByPlayerId(game.getDealer(), "Game starts from you!\n");

            nextTurn();

            playSmallBlind();
            nextTurn();
            playBigBlind();
            nextTurn();
        }
    }


    protected static void playSmallBlind() {
        while (!game.playBlind(game.getSmallBlindValue())) {
            game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
            sendToUserByPlayerId((game.getPlayerTurn()),
                    "You have been eliminated!");
            nextTurn();
        }
        sendToAllUsersByPlayerId(-1, "Small blind: " + Integer.toString(game.getSmallBlindValue())
                + " has been played by: Player " + Integer.toString(game.getPlayerTurn() + 1) + "\n");
    }

    protected static void playBigBlind() {
        while (!game.playBlind(game.getBigBlindValue())) {
            game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
            sendToUserByPlayerId(game.getPlayerTurn(),
                    "You have been eliminated!");
            nextTurn();
        }
        sendToAllUsersByPlayerId(-1, "Big blind: " + Integer.toString(game.getBigBlindValue())
                + " has been played by: Player " + Integer.toString(game.getPlayerTurn() + 1) + "\n");
    }

    protected static void nextTurn() {
        game.nextPlayerTurn();
        sendToAllUsersByPlayerId(game.getPlayerTurn(), uselessCurrentTurnPlayer
                + Integer.toString(game.getPlayerTurn() + 1) + "\n");
        sendToUserByPlayerId(game.getPlayerTurn(), "Your turn!");
    }

    protected static void sendPlayerInfoToUsers() {
       try {
           connectedUsers.forEach((key, value) -> {
               sendToUser(key, getPlayerInfo(getPlayerId(key)));
           });
       }
         catch (Exception e) {
              logger.log(Level.SEVERE, "Error while sending player info to users!");
         }

    }

    protected static String getPlayerInfo(int playerId) {
        return "\n\nYou have " + Integer.toString(game.getPlayer(playerId).getChips()) + " chips.\n" +
                "Current game pot: " + Integer.toString(game.getPot()) + " chips.\n" +
                "Your current pot: " + game.getPlayerPot(playerId) + "\n" +
                "Your Hand: " + game.getPlayer(playerId).getHandToString() + "\n";
    }

    protected static void sendToAllUsers(SocketChannel messageAuthor, String message) {
        String finalMessage = "" + message + "\n";
        connectedUsers.forEach((x, value) -> {
            try {
                ByteBuffer serverResponse = ByteBuffer.wrap(finalMessage.getBytes());
                if (x != messageAuthor) {
                    x.write(serverResponse);
                }
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage());

            }
        });
    }

    protected static void sendToUserByPlayerId(int playerId, String message) {
        try {
            if (playerId != -1)
                sendToUser(Objects.requireNonNull(getUserByPlayerId(playerId)), message);
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    protected static void sendToAllUsersByPlayerId(int playerId, String message) {
        try {
            if (playerId == -1)
                sendToAllUsers(null, message);
            else
                sendToAllUsers(Objects.requireNonNull(getUserByPlayerId(playerId)), message);
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, e.getMessage());
        }
    }

    protected static void sendToUser(SocketChannel user, String message) {
        String finalMessage = message + "\n";
        try {
            ByteBuffer serverResponse = ByteBuffer.wrap(finalMessage.getBytes());
            user.write(serverResponse);
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());

        }
    }


}