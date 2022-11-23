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
    private static final int BUFFER_SIZE = 1024;
    private static Selector selector = null;
    //mapa wszystkich userow z ich unikatowymi kanalami
    private static Map<SocketChannel, String> connectedUsers = new HashMap<>();
    static int usersCount = 0;

    static boolean gameStarted = false;
    static boolean firstBettingRound = false;
    static boolean drawRound = false;

    static boolean secondBettingRound = false;

    private static String notYourTurnWarning = "It's not your turn!";
    private static String uselessPlayerString = "Player: ";
    private static String uselessCurrentTurnPlayer = "Current turn: Player ";
    private static String uselessInvalidCommand = "Invalid command! Try again!";
    private static Game game;

    static Logger logger
            = Logger.getLogger(
            Server.class.getName());

    public static void main(String[] args) {

        logger.log(Level.INFO, "starting server");

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

            boolean running = true;
            game = new Game(10);

            while (running) {
                selector.select();
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> i = selectedKeys.iterator();

                while (i.hasNext()) {
                    SelectionKey key = i.next();

                    if (key.isAcceptable() && usersCount < 4 && !gameStarted) {
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
        } catch (IOException | InterruptedException e) {
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


    private static void processAcceptEvent(ServerSocketChannel mySocket) throws IOException {
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
                + Integer.toString(usersCount) + "/4";
        logger.log(Level.INFO, message);
        message = connectedUsers.get(myClient) + " has joined the game. \nPlayers: "
                + Integer.toString(usersCount) + "/4\n";
        sendToAllUsers(myClient, message);
        sendToUser(myClient, "Welcome to the poker game! \n" +
                "Type '!ready <starting chips>' to start the game.");
    }

    private static void processReadEvent(SelectionKey key)
            throws IOException, InterruptedException {
        SocketChannel myClient = (SocketChannel) key.channel();

        ByteBuffer clientResponse = ByteBuffer.allocate(BUFFER_SIZE);
        myClient.read(clientResponse);
        String data = new String(clientResponse.array()).trim();

        if (data.length() > 0) {
            String message = connectedUsers.get(myClient) + ": " + data;
            logger.log(Level.INFO, message);
            validateCommands(myClient, data);
        }
    }

    private static void validateCommands(SocketChannel client, String command) {
        int playerId = getPlayerId(client);
        String[] words = command.split(" ");
        if (command.startsWith("!ready") && !gameStarted) {
            handleReadyCommand(client, words);
        } else if (command.startsWith("!hand")) {
            sendToUser(client, game.getPlayer(playerId).getHandToString());
        } else if (command.startsWith("!info")) {
            sendToUser(client, getPlayerInfo(playerId));
        } else if (command.startsWith("!turn")) {
            sendToUser(client, uselessCurrentTurnPlayer + Integer.toString(game.getPlayerTurn() + 1));
        } else if (command.startsWith("!bet") && (firstBettingRound)) {
            handleBetForFirstRoundCommand(client, words);

        } else if (command.startsWith("!draw") && drawRound && (game.getDrawCounter() != game.playersPlaying())) {
            handleDrawingRound(client, words);
        } else if (command.startsWith("!bet") && (secondBettingRound)) {
            handleBetForSecondRoundCommand(client, words);
        }

    }
    private static int getPlayerId(SocketChannel myClient) {
        //get player id from map
        String playerName = connectedUsers.get(myClient);
        return Integer.parseInt(playerName.substring(playerName.length() - 1)) - 1;
    }

    private static SocketChannel getUserByPlayerId(int playerId) {
        //get player id from map
        String playerName = uselessPlayerString + Integer.toString(playerId + 1);
        for (Map.Entry<SocketChannel, String> entry : connectedUsers.entrySet()) {
            if (entry.getValue().equals(playerName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void handleReadyCommand(SocketChannel myClient, String[] command) {
        int playerId = getPlayerId(myClient);
        int chips = 0;
        try {
            chips = Integer.parseInt(command[1]);
        } catch (NumberFormatException e) {
            sendToUser(myClient, "Invalid number of chips! Try again.");
            return;
        }
        if (chips > 20) {
            game.getPlayer(playerId).setChips(chips);
            game.getPlayer(playerId).setReady(true);
            sendToUser(myClient, "You are ready to play!");
            sendToAllUsers(myClient, uselessPlayerString
                    + Integer.toString(playerId + 1) + " is ready to play!");
            startGame();

        } else {
            sendToUser(myClient, "Invalid number of chips! Try again.");
        }
    }


    private static void handleBetForFirstRoundCommand(SocketChannel myClient, String[] command) {
        if (game.getPlayerTurn() != getPlayerId(myClient)) {
            sendToUser(myClient, notYourTurnWarning);
        } else {
            playerBet(myClient, command);
            if (game.playersPlaying() == 1) {
                handleSingleWinner();
            }
            if (game.getPlayerTurn() == game.getLastPlayerAction()) {
                firstBettingRound = false;
                game.setPlayerTurn(game.getDealer() - 1);
                sendToAllUsers(null, "First betting round finished!\nStarting drawing round...\n\n" + uselessCurrentTurnPlayer + Integer.toString(game.getPlayerTurn() + 1));
                sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())), "It's your turn! Type '!draw <card numbers>' to draw cards.");
                drawRound = true;
            }
        }
    }

    private static void handleDrawingRound(SocketChannel myClient, String[] command) {
        if (game.getPlayerTurn() != getPlayerId(myClient)) {
            sendToUser(myClient, notYourTurnWarning);
        } else {
            playerDraw(myClient, command);
            if (game.getDrawCounter() == game.playersPlaying()) {
                drawRound = false;
                game.setPlayerTurn(game.getDealer() - 1);
                sendToAllUsers(myClient, "Drawing round finished!\nStarting second betting round...");
                sendToAllUsers(myClient, uselessCurrentTurnPlayer + Integer.toString(game.getPlayerTurn() + 1));
                secondBettingRound = true;
                game.setLastPlayerAction((game.getDealer() - 1 + usersCount) % usersCount);
            }
        }
    }

    private static void handleSingleWinner() {
        sendToAllUsers(null, "\n\n\nPlayer " + Integer.toString(game.getPlayerTurn() + 1) + " won the game!\n\n\n");
        game.getWinner();
        gameStarted = false;
        firstBettingRound = false;
        drawRound = false;
        secondBettingRound = false;
        startGame();
    }

    private static void handleBetForSecondRoundCommand(SocketChannel client, String[] command) {
        if (game.getPlayerTurn() != getPlayerId(client)) {
            sendToUser(client, notYourTurnWarning);
        } else {
            playerBet(client, command);

            if (game.playersPlaying() == 1) {
                handleSingleWinner();
            }

            if (game.getPlayerTurn() == game.getLastPlayerAction()) {
                secondBettingRound = false;
                game.setPlayerTurn(game.getDealer() - 1);
                sendToAllUsers(client, "Second betting round finished!\nChecking winner...");
                int winner = game.getWinner();
                sendToAllUsers(client, "\n\n\n" + uselessPlayerString + Integer.toString(winner + 1) + " won the game!\n\n\n");
            }
        }
    }

    private static void playerDraw(SocketChannel author, String[] words) {
        int playerId = getPlayerId(author);
        if (words.length > 5) {
            sendToUser(author, "Too many cards to draw! Try again.\nMaximum cards to draw is 4.");
            return;
        }
        for (int i = 1; i < words.length; i++) {
            try {
                int cardId = Integer.parseInt(words[i]);
                if (cardId > 5 || cardId < 1) {
                    if (cardId == 0) {
                        sendToUser(author, "Skipped drawing!");
                    }
                    sendToUser(author, "Invalid card number! Try again.\nCard number must be between 1 and 5.");
                    return;
                }
            } catch (NumberFormatException e) {
                sendToUser(author, "Invalid card number! Try again.\nCard number must be between 1 and 5.");
                return;
            }
        }
        for (int i = 1; i < words.length; i++) {
            int cardId = Integer.parseInt(words[i]);
            game.getPlayer(playerId).replaceCard(cardId - 1, game.getDeck().getTopCard());
        }
        game.addDraw();
        sendToAllUsers(author, uselessPlayerString + Integer.toString(playerId + 1) + " has drawn " + Integer.toString(words.length - 1) + " cards.");
        sendToUser(author, getPlayerInfo(playerId));
        nextTurn();
    }

    private static void handleRaiseBet(SocketChannel author, int playerId, String[] words) {
        if (words.length == 3) {
            try {
                int raise = Integer.parseInt(words[2]);
                if (raise > 2 * game.getHighestBet()
                        && (raise - game.getPlayerPot(playerId) <= game.getPlayerChips(playerId))) {
                    game.bet(playerId, Game.Decision.RAISE, raise);
                    sendToAllUsers(author, uselessPlayerString + Integer.toString(playerId + 1) + " raised to " + Integer.toString(raise) + ".");
                    nextTurn();
                } else {
                    sendToUser(author, "Invalid raise! Try again.");
                }
            } catch (NumberFormatException e) {
                sendToUser(author, "Invalid number! Try again.");
            }
        } else {
            sendToUser(author, uselessInvalidCommand);
        }
    }

    private static void playerBet(SocketChannel author, String[] words) {
        int playerId = getPlayerId(author);
        if (words.length == 1) {
            sendToUser(author, uselessInvalidCommand);
        } else {
            switch (words[1]) {
                case "check":
                    game.bet(playerId, Game.Decision.CHECK, 0);
                    sendToAllUsers(author, uselessPlayerString + Integer.toString(playerId + 1) + " checked.");
                    nextTurn();
                    break;
                case "fold":
                    game.bet(playerId, Game.Decision.FOLD, 0);
                    sendToAllUsers(author, uselessPlayerString + Integer.toString(playerId + 1) + " folded.");
                    nextTurn();
                    break;
                case "call":
                    if (game.getHighestBet() - game.getPlayerPot(playerId) < game.getPlayerChips(playerId)) {
                        game.bet(playerId, Game.Decision.CALL, 0);
                        sendToAllUsers(author, uselessPlayerString + Integer.toString(playerId + 1) + " called.");
                        nextTurn();
                    } else {
                        sendToUser(author, "You don't have enough chips to call!");
                    }
                    break;
                case "raise":
                    handleRaiseBet(author, playerId, words);
                    break;
                case "allin":
                    game.bet(playerId, Game.Decision.ALL_IN, 0);
                    sendToAllUsers(author, uselessPlayerString + Integer.toString(playerId + 1) + " went all in.");
                    nextTurn();
                    break;
                default:
                    sendToUser(author, uselessInvalidCommand);
                    break;
            }
        }
    }

    private static void startGame() {
        if (!gameStarted && game.checkIfPlayersAreReady() && usersCount >= 2) {
            gameStarted = true;
            game.startRound();
            firstBettingRound = true;
            secondBettingRound = false;
            drawRound = false;
            sendToAllUsers(null, "Game started!\n");
            sendPlayerInfoToUsers();
            sendToAllUsers(getUserByPlayerId(game.getDealer()), "Dealer: Player "
                    + Integer.toString(game.getDealer() + 1) + "\n");
            sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getDealer())), "Game starts from you!\n");

            nextTurn();

            playSmallBlind();
            nextTurn();
            playBigBlind();
            nextTurn();
        }
    }


    private static void playSmallBlind() {
        while (!game.playBlind(game.getSmallBlindValue())) {
            game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
            sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())),
                    "You have been eliminated!");
            nextTurn();
        }
        sendToAllUsers(null, "Small blind: " + Integer.toString(game.getSmallBlindValue())
                + " has been played by: Player " + Integer.toString(game.getPlayerTurn() + 1) + "\n");
    }

    private static void playBigBlind() {
        while (!game.playBlind(game.getBigBlindValue())) {
            game.bet(game.getPlayerTurn(), Game.Decision.FOLD, 0);
            sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())),
                    "You have been eliminated!");
            nextTurn();
        }
        sendToAllUsers(null, "Big blind: " + Integer.toString(game.getBigBlindValue())
                + " has been played by: Player " + Integer.toString(game.getPlayerTurn() + 1) + "\n");
    }

    private static void nextTurn() {
        logger.log(Level.INFO, "Game started!");
        game.nextPlayerTurn();
        sendToAllUsers(getUserByPlayerId(game.getPlayerTurn()), uselessCurrentTurnPlayer
                + Integer.toString(game.getPlayerTurn() + 1) + "\n");
        sendToUser(Objects.requireNonNull(getUserByPlayerId(game.getPlayerTurn())), "Your turn!");
    }

    private static void sendPlayerInfoToUsers() {
        connectedUsers.forEach((key, value) -> {
            sendToUser(key, getPlayerInfo(getPlayerId(key)));
        });
    }

    private static String getPlayerInfo(int playerId) {
        return "\n\nYou have " + Integer.toString(game.getPlayer(playerId).getChips()) + " chips.\n" +
                "Current game pot: " + Integer.toString(game.getPot()) + " chips.\n" +
                "Your current pot: " + game.getPlayerPot(playerId) + "\n" +
                "Your Hand: " + game.getPlayer(playerId).getHandToString() + "\n";
    }

    private static void sendToAllUsers(SocketChannel messageAuthor, String message) {
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

    private static void sendToUser(SocketChannel user, String message) {
        String finalMessage = message + "\n";
        try {
            ByteBuffer serverResponse = ByteBuffer.wrap(finalMessage.getBytes());
            user.write(serverResponse);
        } catch (IOException e) {
            logger.log(Level.WARNING, e.getMessage());

        }
    }


}