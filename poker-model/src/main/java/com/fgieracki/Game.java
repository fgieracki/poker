package com.fgieracki;

import java.util.ArrayList;

public class Game {
    private Deck deck;
    private ArrayList<Player> players;
    private int[] playerPots = new int[4];
    private int pot;
    private int playerTurn;

    private int maxBet;
    private int smallBlindValue = 10;
    private int bigBlindValue = 20;
    private int dealer;
    private enum Decision {FOLD, CALL, RAISE, CHECK};
    private Decision[] playerDecisions = new Decision[4];



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
        maxBet = 0;
        deck = new Deck();
        deck.shuffle();
        for (int i = 0; i < players.size(); i++) {
            playerPots[i] = 0;
            players.get(i).clearHand();
            players.get(i).getFiveCards(deck);
        }
        playerTurn = dealer;

        //start betting
        bettingRound();

    }

    public void bettingRound(){
        int bet = 0;
        smallBlind();
        bigBlind();
        while(playerTurn != dealer){
            bet(playerTurn);
        }
        //betting round

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
        System.out.println("Player " + playerId + " decision: ");
        System.out.println("1. Fold");
        System.out.println("2. Call");
        System.out.println("3. Raise");
        System.out.println("4. Check");
        decision = Integer.parseInt(System.console().readLine());
        while(decision < 1 || decision > 4){
            System.out.println("Wrong input");
            System.out.println("Player " + playerId + " decision: ");
            decision = Integer.parseInt(System.console().readLine());
        }
        switch(decision){
            case 1:
                playerDecisions[playerId] = Decision.FOLD;
                break;
            case 2:
                playerDecisions[playerId] = Decision.CALL;
                playerPots[playerId] += maxBet - playerPots[playerId];
                break;
            case 3:
                playerDecisions[playerId] = Decision.RAISE;
                System.out.println("Player " + playerId + " raise: ");
                bet = Integer.parseInt(System.console().readLine());
                while(bet < 2*maxBet || bet > playerChips){
                    System.out.println("Wrong input");
                    System.out.println("Player " + playerId + " raise: ");
                    bet = Integer.parseInt(System.console().readLine());
                }
                break;
            case 4:
                playerDecisions[playerId] = Decision.CHECK;
                bet = 0;
                break;
        }
    }
    private void smallBlind(){
        playerTurn = (playerTurn + 1) % players.size();
        players.get(playerTurn).removeChips(smallBlindValue);
        playerPots[playerTurn] += smallBlindValue;
        pot += smallBlindValue;
        maxBet = smallBlindValue;
    }

    private void bigBlind(){
        playerTurn = (playerTurn + 1) % players.size();
        players.get(playerTurn).removeChips(bigBlindValue);
        playerPots[playerTurn] += bigBlindValue;
        pot += bigBlindValue;
        maxBet = bigBlindValue;
    }




    public void showDeck(){
        deck.showDeck();
    }



}
