package com.fgieracki;

import java.util.ArrayList;

public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    private int pot;
    private int playerTurn;
    private int dealer;



    public Game(){
        deck = new Deck();
        players = new ArrayList<Player>();
        pot = 0;
        playerTurn = 0;
        dealer = 0;
    }


    //Pot related methods
    public void showPot(){
        System.out.println("Pot: " + pot);
    }

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


    //Player related methods
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

    public void showPlayers(){
        for (int i = 0; i < players.size(); i++) {
            players.get(i).showHand();
        }
    }


    public void showPlayersChips(){
        for (int i = 0; i < players.size(); i++) {
            System.out.println("Player " + i + " has " + players.get(i).getChips() + " chips");
        }
    }

    public void setPlayerChips(int playerId, int chips){
        players.get(playerId).setChips(chips);
    }

    public void setPlayersChips(int chips){
        for (int i = 0; i < players.size(); i++) {
            players.get(i).setChips(chips);
        }
    }

    public void addPlayersChips(int chips){
        for (int i = 0; i < players.size(); i++) {
            players.get(i).addChips(chips);
        }
    }

    public int getPlayerChips(int playerId){
        return players.get(playerId).chips;
    }

    public void removePlayersChips(int chips){
        for (int i = 0; i < players.size(); i++) {
            players.get(i).removeChips(chips);
        }
    }

    public void showPlayersHandValue(){
        for (int i = 0; i < players.size(); i++) {
            System.out.println("Player " + i + " has " + players.get(i).getHandValue().countHandValue() + " hand value");
        }
    }





    public void startRound(){
        dealer = (dealer + 1) % players.size();
        deck = new Deck();
        deck.shuffle();
        for (int i = 0; i < players.size(); i++) {
            players.get(i).clearHand();
            players.get(i).getFiveCards(deck);
        }
        playerTurn = dealer;
    }



    public void showDeck(){
        deck.showDeck();
    }



}
