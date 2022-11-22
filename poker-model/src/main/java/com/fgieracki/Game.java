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
    private int drawCounter = 0;
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
        return !(playerDecisions[playerNumber] == Decision.FOLD || playerDecisions[playerNumber] == Decision.CHECK);
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

    public int[]getWinners(){
        int[] winners = handleWinners();
        splitPot(winners);
        return winners;
    }

    private void splitPot(int[] winners){
        int splitPot = pot/winners.length;
        for (int winner : winners) {
            players.get(winner).addChips(splitPot);
        }
    }

    private int[] handleWinners(){
        int[] winners = basicWinnerSelection();
        if(winners.length == 1){
            return winners;
        }
        else{
            return tieBreaker(winners);
        }
    }

    private int[] tieBreaker(int[] winners){
        int[] newWinners = new int[winners.length];
        int winnersAmount = 1;
        int bestPlayer = winners[0];
        newWinners[0] = bestPlayer;
        for(int i = 1; i < winners.length; i++){
            boolean swapped = false;
            for(int cardId = 0; cardId < 5; cardId++){
                if(players.get(bestPlayer).getHandValue().getNthHighestCard(cardId) < players.get(winners[i]).getHandValue().getNthHighestCard(cardId)){
                    swapped = true;
                    bestPlayer = winners[i];
                    winnersAmount = 0;
                    newWinners[winnersAmount] = bestPlayer;
                    winnersAmount++;
                    break;
                }
                else if(players.get(bestPlayer).getHandValue().getNthHighestCard(cardId) > players.get(winners[i]).getHandValue().getNthHighestCard(cardId)){
                    swapped = true;
                    break;
                }
            }
            if(!swapped){
                newWinners[winnersAmount] = winners[i];
                winnersAmount++;
            }
        }
        int[] finalWinners = new int[winnersAmount];
        System.arraycopy(newWinners, 0, finalWinners, 0, winnersAmount);
        return finalWinners;
    }


    private int[] basicWinnerSelection(){
        int[] winners = new int[4];
        int winnersAmount = 0;
        int maxHandValue = 0;
        for (int i = 0; i < players.size(); i++) {
            if(checkIfPlayerIsPlaying(i)){
                int handValue = players.get(i).getHandValue().countHandValue();
                if(handValue > maxHandValue){
                    maxHandValue = handValue;
                    winnersAmount = 0;
                    winners[winnersAmount] = i;
                    winnersAmount = 1;
                }
                else if(handValue == maxHandValue){
                    winners[winnersAmount] = i;
                    winnersAmount++;
                }
            }
        }
        int[] winnersFinal = new int[winnersAmount];
        System.arraycopy(winners, 0, winnersFinal, 0, winnersAmount);
        return winnersFinal;
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

    public int getDealer(){
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

    public Deck getDeck(){
        return deck;
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


    public void startRound(){
        prepareGame();
        dealCards();
        takeAntes();
    }

    public void setLastPlayerAction(int playerNumber){
        lastPlayerAction = playerNumber;
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
        drawCounter = 0;
        playerTurn = dealer;
    }

    public void bet(int playerId, Decision decision, int bet){
        switch (decision) {
            case CALL -> {
                playerDecisions[playerId] = Decision.CALL;
                addPot(maxBet - playerPots[playerId]);
                players.get(playerId).removeChips(maxBet - playerPots[playerId]);
                playerPots[playerId] = maxBet;
            }
            case RAISE -> {
                playerDecisions[playerId] = Decision.RAISE;
                addPot(bet - playerPots[playerId]);
                maxBet = bet;
                players.get(playerId).removeChips(bet - playerPots[playerId]);
                lastPlayerAction = playerId;
            }
            case CHECK ->
                playerDecisions[playerId] = Decision.CHECK;

            case ALL_IN -> {
                playerDecisions[playerId] = Decision.ALL_IN;
                addPot(players.get(playerId).getChips());
                playerPots[playerId] += players.get(playerId).getChips();
                maxBet = playerPots[playerId];
                players.get(playerId).removeChips(players.get(playerId).getChips());
                lastPlayerAction = playerId;
            }
            default ->
                playerDecisions[playerId] = Decision.FOLD;


        }
    }


    public void addDraw(){
        drawCounter++;
    }
    public int getDrawCounter(){
        return drawCounter;
    }
    public void setPlayerTurn(int playerTurn){
        this.playerTurn = playerTurn;
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
