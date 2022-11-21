package com.fgieracki;

import java.util.ArrayList;

import static java.lang.Math.max;

public class Game {
    private Deck deck;
    private final ArrayList<Player> players;
    private final int[] playerPots = new int[4];
    private int pot;
    private int playerTurn;

    private int maxBet;

    private int lastPlayerAction;
    private final int ante;
    private int dealer;
    public enum Decision {FOLD, CALL, RAISE, CHECK, ALL_IN}
    private final Decision[] playerDecisions = new Decision[4];



    public Game(int newAnte){
        deck = new Deck();
        players = new ArrayList<>();
        pot = 0;
        playerTurn = 0;
        ante = newAnte;
        dealer = -1;
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

    private boolean checkIfPlayerIsPlaying(int playerNumber){
        if(playerDecisions[playerNumber] == Decision.FOLD || playerDecisions[playerNumber] == Decision.CHECK){
            return false;
        }
        return true;
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

    public int getLastPlayerAction(){
        return lastPlayerAction;
    }
    public void addPlayer(){
        players.add(new Player());
    }

    public int getSmallBlindValue(){
        return 10;
    }

    public int getBigBlindValue(){
        return 20;
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

    public int getDealerId(){
        return dealer;
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
        do {
            playerTurn = (playerTurn + 1) % players.size();
        } while(!checkIfPlayerIsPlaying(playerTurn) && playersPlaying() > 1);

    }
    public boolean checkIfPlayersAreReady(){
        for (Player player : players) {
            if(!player.isReady){
                return false;
            }
        }
        return true;
    }
    public HandValue getPlayerHandValue(int playerId){
        return players.get(playerId).getHandValue();
    }





    public void startRound(){
        prepareGame();
        dealCards();
        takeAntes();
    }

    private void takeAntes(){
        for(int i = 0; i < players.size(); i++){
            players.get(i).removeChips(ante);
            playerPots[i] += ante;
            pot += ante;
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

    public void bet(int playerId, Decision decision, int bet){
        switch (decision) {
            case CALL -> {
                playerDecisions[playerId] = Decision.CALL;
                addPot(maxBet - playerPots[playerId]);
                players.get(playerId).removeChips(maxBet - playerPots[playerId]);
                playerPots[playerId] = maxBet;
//                lastPlayerAction = playerId;
            }
            case RAISE -> {
                playerDecisions[playerId] = Decision.RAISE;
                addPot(bet - playerPots[playerId]);
                players.get(playerId).removeChips(bet - playerPots[playerId]);
                lastPlayerAction = playerId;
            }
            case CHECK -> {
                playerDecisions[playerId] = Decision.CHECK;
            }
            case ALL_IN -> {
                playerDecisions[playerId] = Decision.ALL_IN;
                addPot(players.get(playerId).getChips());
                playerPots[playerId] += players.get(playerId).getChips();
                players.get(playerId).removeChips(players.get(playerId).getChips());
                lastPlayerAction = playerId;
            }
            default -> {
                playerDecisions[playerId] = Decision.FOLD;
            }


        }
    }

    public void setPlayerTurn(int playerTurn){
        this.playerTurn = playerTurn;
    }

    public int getDealer(){
        return dealer;
    }

    public int getHighestBet(){
        return maxBet;
    }

    public boolean playBlind(int value){
        if(players.get(playerTurn).getChips() < value - playerPots[playerTurn]){
            playerDecisions[playerTurn] = Decision.FOLD;
            return false;
        }
        players.get(playerTurn).removeChips(value - playerPots[playerTurn]);
        playerPots[playerTurn] = value;
        pot += value - playerPots[playerTurn];
        maxBet = max(maxBet, value);
        lastPlayerAction = playerTurn;
        return true;
    }


    public int playersPlaying(){
        int playersPlaying = 0;
        for (int i = 0; i < players.size(); i++) {
            if(playerDecisions[i] != Decision.FOLD && playerDecisions[i] != Decision.CHECK){
                playersPlaying++;
            }
        }
        return playersPlaying;
    }
}
