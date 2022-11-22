package com.fgieracki;

import java.util.ArrayList;

/**
 *  @author fgieracki
 *  @version 1.0
 *
 *  Player class
 *
 *        hand - array of cards
 *             5 cards in the hand
 *
 *       chips - number of chips
 *
 *
 */

public class Player{
    ArrayList<Card> hand;
    int chips;
    boolean isReady;


    public Player(){
        hand = new ArrayList<>();
        isReady = false;
    }


    public String getHandToString(){
        String handString = "{";
        for (Card card : hand){
            handString += card.toString() + ", ";
        }
        //replace last comma with curly bracket
        handString = handString.substring(0, handString.length() - 2) + "}";
        return handString;
    }


    public void setReady(boolean value){
        isReady = value;
    }

    public void replaceCard(int index, Card card){
        hand.set(index, card);
    }
    /**
     * @function getFiveCards() - gets five cards from the deck
     * @param deck - deck of cards
     */
    public void getFiveCards(Deck deck){
        if(deck.deckSize() >= 5){
            for (int i = 0; i < 5; i++) {
                hand.add(deck.getTopCard());
            }
        }
    }

    /**
     * @function getChips() - gets the number of chips
     * @return chips - number of chips
     */
    public int getChips(){
        return chips;
    }

    /**
    * @function setChips() - sets the number of chips
    * @param chips - number of chips
    */
    public void setChips(int chips){
        this.chips = chips;
    }

    public void clearHand(){
        hand.clear();
    }

    /**
    * @function addChips() - adds chips to the player
    * @param chips - number of chips
    */
    public void addChips(int chips){
        this.chips += chips;
    }

    /**
    * @function removeChips() - removes chips from the player
    * @param chips - number of chips
    */
    public void removeChips(int chips){
        this.chips -= chips;
    }

    public HandValue getHandValue() {
        return HandValue.getHandValue(hand);
    }
}
