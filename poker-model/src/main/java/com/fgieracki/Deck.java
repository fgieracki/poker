package com.fgieracki;

import java.util.ArrayList;

/**
 * @author fgieracki
 * @version 1.0
 *
 * Deck class
 *
 * @param cards - array of cards
 *              52 cards in the deck
 *              13 cards of each suit
 *              4 suits
 *
 *              Rank:
 *              2 - 10 - numbers of the card
 *              11 - Jack
 *              12 - Queen
 *              13 - King
 *              14 - Ace
 *
 *              Suit:
 *              0 - Spades
 *              1 - Hearts
 *              2 - Diamonds
 *              3 - Clubs
 */

public class Deck {
    private final ArrayList<Card> cards;

    //default constructor
    public Deck() {
        //create cards
        cards = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            for (int j = 2; j < 15; j++) {
                cards.add(new Card(j, i));
            }
        }
    }


    /**
     * @function shuffle() - shuffles the deck
     *
     */
    public void shuffle() {
        //shuffle cards
        for (int i = 0; i < cards.size(); i++) {
            int random = (int) (Math.random() * cards.size());
            swap(i, random);
        }
    }

    //cards getter
    public ArrayList<Card> getCards() {
        return cards;
    }


    /**
     * @function showDeck() - shows the deck
     */
//    public void showDeck(){
//        for (int i = 0; i < cards.size(); i++) {
//            System.out.println(cards.get(i).getRank() + " " + cards.get(i).getSuit());
//        }
//    }

    public int deckSize(){
        return cards.size();
    }


    /**
     * function getTopCard() - returns the top card of the deck
     * @return top card of the deck
     */
    public Card getTopCard(){
        if(cards.size() == 0) return null;
        Card topCard = cards.get(0);
        cards.remove(0);
        return topCard;
    }


    /**
     * function swap() - swaps two cards in the deck
     * @param i - index of the first card
     * @param j - index of the second card
     */
    private boolean swap(int i, int j){
        if(i < 0 || i >= cards.size() || j < 0 || j >= cards.size()){
            return false;
        }
        Card tmp = cards.get(i);
        cards.set(i, cards.get(j));
        cards.set(j, tmp);
        return true;
    }

}
