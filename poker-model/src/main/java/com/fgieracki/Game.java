package com.fgieracki;

import java.util.ArrayList;

import static java.lang.Math.max;

/**
 * @author fgieracki
 * Game class
 * This class is responsible for the game logic
 * It contains the game state and the game logic
 *
 * @version 1.0
 */

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

    private void splitPot(int winner){
        players.get(winner).addChips(getPot());
        setPot(0);
    }


    //Player management methods
    /**
     *  addPlayers() - adds a player to the game
     * @param amount - player amount to be added
     */
    public void addPlayers(int amount){
        for (int i = 0; i < amount; i++) {
            players.add(new Player());
        }
    }

    public void addPlayer(){
        players.add(new Player());
    }

    public void removePlayer(int playerId) {
        players.remove(playerId);
    }

    /**
     *  getWinner() - returns the winner of the game
     * @return - returns the winner of the game
     */
    public int getWinner(){
        int winner = handleWinners();
        splitPot(winner);
        return winner;
    }


    /**
     *  checkIfPlayerIsPlaying() - checks if the player is still in the game
     * @param playerNumber
     * @return
     */
    protected boolean checkIfPlayerIsPlaying(int playerNumber){
        return !(playerDecisions[playerNumber] == Decision.FOLD || playerDecisions[playerNumber] == Decision.CHECK);
    }

    /**
     *  handleWinners() - handles the winners of the game
     * @return - returns the winner of the game
     */
    private int handleWinners(){
        int[] winners = basicWinnerSelection();
        if(winners.length == 1){
            return winners[0];
        }
        else{
            return tieBreaker(winners);
        }
    }

    /**
     *  tieBreaker - second selection of the potential winners,
     * bases on the highest cards in the hand
     * @param winners
     * @return - returns the winner of the game
     */
    private int tieBreaker(int[] winners){
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
        return finalTieBreaker(finalWinners);
    }


    /**
     *  finalTieBreaker - third selection of the potential winners,
     * bases on the card Suits
     * @param winners
     * @return winnerId
     */
    private int finalTieBreaker(int[] winners){
        //get the best hand by comparing suits
        int bestPlayer = winners[0];
        for(int i = 1; i < winners.length; i++){
            for(int cardId = 0; cardId < 5; cardId++){
                if(players.get(bestPlayer).getHandValue().getNthHighestCardSuit(cardId) < players.get(winners[i]).getHandValue().getNthHighestCardSuit(cardId)){
                    bestPlayer = winners[i];
                    break;
                }
                else if(players.get(bestPlayer).getHandValue().getNthHighestCardSuit(cardId) > players.get(winners[i]).getHandValue().getNthHighestCardSuit(cardId)){
                    break;
                }
            }
        }
        return bestPlayer;
    }


    /**
     *  basicWinnerSelection() -first selection of the winner of the game
     * bases on the hand value
     * @return - returns the potential winners of the game
     */
    protected int[] basicWinnerSelection(){
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


    public int getSmallBlindValue(){
        return 10;
    }

    public int getBigBlindValue(){
        return 20;
    }

    public Player getPlayer(int index){
        return players.get(index);
    }

    public int getPlayerPot(int index){
        return playerPots[index];
    }

    public int getPlayerChips(int playerId){
        return players.get(playerId).chips;
    }

    public int getDealer(){
        return dealer;
    }

    public int getPlayerTurn(){
        return playerTurn;
    }

    public Deck getDeck(){
        return deck;
    }

    /**
     *  nextPlayerTurn() - changes the player turn
     */
    public void nextPlayerTurn(){
        do {
            playerTurn = (playerTurn + 1) % players.size();
        } while(!checkIfPlayerIsPlaying(playerTurn) && playersPlaying() > 1);

    }

    /**
     *  checkIfPlayersAreReady() - check if all connected players are ready to play
     * @return - true if all connected players are ready to play, otherwise false
     */
    public boolean checkIfPlayersAreReady(){
        for (Player player : players) {
            if(!player.isReady){
                return false;
            }
        }
        return true;
    }


    public void addCard(int playerId, Card card){
        players.get(playerId).hand.add(card);
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

    /**
     *  prepareGame() - prepares the game for the next round
     * inits default variable values
     */
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

    /**
     *  bet - handles the bet action
     * @param playerId
     * @param decision
     * @param bet
     */
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
                playerPots[playerId] = bet;
                lastPlayerAction = playerId;
            }
            case CHECK ->
                playerDecisions[playerId] = Decision.CHECK;

            case ALL_IN -> {
                playerDecisions[playerId] = Decision.ALL_IN;
                addPot(players.get(playerId).getChips());
                playerPots[playerId] += players.get(playerId).getChips();
                maxBet = max(maxBet, playerPots[playerId]);
                players.get(playerId).setChips(0);
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

    /**
     *  playBlind - handles the blind plays
     * @param value
     * @return - returns true if player was able to play the blind, otherwise false
     */
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


    /**
     *  playersPlaying - returns the amount of players playing
     * @return - the amount of players playing
     */
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
