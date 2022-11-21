package com.fgieracki;

import java.util.ArrayList;

public class Game {
    private Deck deck;
    private final ArrayList<Player> players;
    private final int[] playerPots = new int[4];
    private int pot;
    private int playerTurn;

    private int maxBet;

    private int lastPlayerAction;
    private int ante;
    private int smallBlindValue = 10;
    private int bigBlindValue = 20;
    private int dealer;
    private enum Decision {FOLD, CALL, RAISE, CHECK}
    private final Decision[] playerDecisions = new Decision[4];



    public Game(int newAnte){
        deck = new Deck();
        players = new ArrayList<>();
        pot = 0;
        playerTurn = 0;
        ante = newAnte;
        dealer = 0;
    }


    //Pot management methods

    public void setPot(int pot){
        this.pot = pot;
    }

    public int getPot(){
        return pot;
    }

    public void addPot(int pot){
        this.pot += pot;
    }

    public void removePot(int pot){
        this.pot -= pot;
    }


    //Player management methods
    /**
     * @function addPlayers() - adds a player to the game
     * @param amount - player amount to be added
     */
    public void addPlayers(int amount){
        for (int i = 0; i < amount; i++) {
            players.add(new Player());
        }
    }

    public int getPlayerAmount(){
        return players.size();
    }

    public void addPlayer(){
        players.add(new Player());
    }

    public void addPlayer(Player player){
        players.add(player);
    }

    public Player getPlayer(int index){
        return players.get(index);
    }

    public int getPlayerPot(int index){
        return playerPots[index];
    }
//    public void showPlayers(){
//        for (Player player : players) {
//            player.showHand();
//        }
//    }

//    public void showPlayersChips(){
//        for (int i = 0; i < players.size(); i++) {
//            System.out.println("Player " + i + " has " + players.get(i).getChips() + " chips");
//        }
//    }

    public void setPlayerChips(int playerId, int chips){
        players.get(playerId).setChips(chips);
    }

    public void setPlayersChips(int chips){
        for (Player player : players) {
            player.setChips(chips);
        }
    }

    public void addPlayersChips(int chips){
        for (Player player : players) {
            player.addChips(chips);
        }
    }

    public int getPlayerChips(int playerId){
        return players.get(playerId).chips;
    }

    public void removePlayersChips(int chips){
        for (Player player : players) {
            player.removeChips(chips);
        }
    }

    public int getPlayerTurn(){
        return playerTurn;
    }

    public void nextPlayerTurn(){
        playerTurn = (playerTurn + 1) % players.size();
    }
    public boolean checkIfPlayersAreReady(){
        for (Player player : players) {
            if(!player.isReady){
                return false;
            }
        }
        return true;
    }


//    public void showPlayersHandValue(){
//        for (int i = 0; i < players.size(); i++) {
//            System.out.println("Player " + i + " has " + players.get(i).getHandValue().countHandValue() + " hand value");
//        }
//    }

    public HandValue getPlayerHandValue(int playerId){
        return players.get(playerId).getHandValue();
    }





    public void startRound(){
        prepareGame();
        dealCards();
        takeAntes();
    }

    private void takeAntes(){
        for (Player player : players) {
            player.removeChips(ante);
            addPot(ante);
        }
    }

    private void dealCards(){
        for (int i = 0; i < players.size(); i++) {
            playerPots[i] = 0;
            players.get(i).clearHand();
            players.get(i).getFiveCards(deck);
            playerDecisions[i] = null;
        }
    }

    private void prepareGame(){
        dealer = (dealer + 1) % players.size();
        lastPlayerAction = (dealer + players.size() - 1) % players.size();
        maxBet = 0;
        deck = new Deck();
        deck.shuffle();
        pot = 0;
        playerTurn = dealer;
    }

    public void play(){
        smallBlind();
        bigBlind();
        while(playerTurn != lastPlayerAction && playersPlaying() > 1){
            if( playerDecisions[playerTurn] != Decision.FOLD &&
                    playerDecisions[playerTurn] != Decision.CHECK)
                bet(playerTurn);
            playerTurn = (playerTurn + 1) % players.size();
        }

        //compare hands
        //iterator through playing players

        //show winner
        //give chips


        //betting round

    }

    public void bet(int playerId, Decision decision, int bet){
        switch (decision) {
            case CALL -> {
                playerDecisions[playerId] = Decision.CALL;
                addPot(maxBet - playerPots[playerId]);
                playerPots[playerId] = maxBet;
                lastPlayerAction = playerId;
            }
            case RAISE -> {
                playerDecisions[playerId] = Decision.RAISE;
            }
            case CHECK -> {
                playerDecisions[playerId] = Decision.CHECK;
            }
            default -> {
                playerDecisions[playerId] = Decision.FOLD;
            }


        }
    }

    private void bet(int playerId){
        int decision = 0;
        int bet = 0;
        int playerChips = players.get(playerId).getChips();
        System.out.println("Actual bet: " + maxBet);
        System.out.println("Player " + playerId + " has " + playerChips + " chips");
        System.out.println("Player " + playerId + " has " + playerPots[playerId] + " in the pot");
        System.out.println("Player " + playerId + " has " + players.get(playerId).getHandValue().countHandValue() + " hand value");
        System.out.println("Player " + playerId + " turn");
        System.out.println("Player " + playerId + " possible decisions: ");
        System.out.println("1. Fold");
        System.out.println("2. Call");
        System.out.println("3. Raise");
        System.out.println("4. Check");
        System.out.println("Player " + playerId + " decision: ");

        switch (decision) {
            case 1 -> playerDecisions[playerId] = Decision.FOLD;
            case 2 -> {
                playerDecisions[playerId] = Decision.CALL;
                lastPlayerAction = playerId;
                players.get(playerId).removeChips(maxBet - playerPots[playerId]);
                playerPots[playerId] = maxBet;
            }
            case 3 -> {
                playerDecisions[playerId] = Decision.RAISE;
                System.out.println("Player " + playerId + " raise: ");
                bet = Integer.parseInt(System.console().readLine());
                while (bet < 2 * maxBet || bet > playerChips) {
                    System.out.println("Wrong input");
                    System.out.println("Player " + playerId + " raise: ");
                    bet = Integer.parseInt(System.console().readLine());
                }
                players.get(playerId).removeChips(bet - playerPots[playerId]);
                playerPots[playerId] += bet;
                lastPlayerAction = playerId;
            }
            case 4 -> playerDecisions[playerId] = Decision.CHECK;
            default -> playerDecisions[playerId] = Decision.FOLD;
        }
    }

    private void playerDecision(int playerId){
        int decision = Integer.parseInt(System.console().readLine());
        while(decision < 1 || decision > 4){
            System.out.println("Wrong input, enter valid decision");
            System.out.println("Player " + playerId + " decision: ");
            decision = Integer.parseInt(System.console().readLine());
        }
        switch (decision){
            case 1 -> playerDecisions[playerId] = Decision.FOLD;
            case 2 -> {
                if(players.get(playerId).getChips() >= maxBet - playerPots[playerId]){
                    playerDecisions[playerId] = Decision.CALL;
                    lastPlayerAction = playerId;
                    players.get(playerId).removeChips(maxBet - playerPots[playerId]);
                    playerPots[playerId] = maxBet;
                }
                else{
                    System.out.println("Player " + playerId + " doesn't have enough chips to call");
                    playerDecision(playerId);
                }
            }
            case 3 -> {
                playerDecisions[playerId] = Decision.RAISE;
                System.out.println("Player " + playerId + " raise: ");
                int bet = Integer.parseInt(System.console().readLine());
                while(bet < 2 * maxBet || bet > players.get(playerId).getChips()){
                    System.out.println("Wrong input");
                    System.out.println("Player " + playerId + " raise: ");
                    bet = Integer.parseInt(System.console().readLine());
                }
                players.get(playerId).removeChips(bet - playerPots[playerId]);
                playerPots[playerId] += bet;
                lastPlayerAction = playerId;
            }

        }
    }

    public boolean smallBlind(){
        playerTurn = (playerTurn + 1) % players.size();
        if(players.get(playerTurn).getChips() < smallBlindValue){
            playerDecisions[playerTurn] = Decision.FOLD;
            return false;
        }
        players.get(playerTurn).removeChips(smallBlindValue);
        playerPots[playerTurn] += smallBlindValue;
        pot += smallBlindValue;
        maxBet = smallBlindValue;
        return true;
    }

    public boolean bigBlind(){
        playerTurn = (playerTurn + 1) % players.size();
        if(players.get(playerTurn).getChips() < bigBlindValue){
            playerDecisions[playerTurn] = Decision.FOLD;
            return false;
        }
        players.get(playerTurn).removeChips(bigBlindValue);
        playerPots[playerTurn] += bigBlindValue;
        pot += bigBlindValue;
        maxBet = bigBlindValue;
        return true;
    }

    private int playersPlaying(){
        int playersPlaying = 0;
        for (int i = 0; i < players.size(); i++) {
            if(playerDecisions[i] != Decision.FOLD && playerDecisions[i] != Decision.CHECK){
                playersPlaying++;
            }
        }
        return playersPlaying;
    }


//    public void showDeck(){
//        deck.showDeck();
//    }



}
