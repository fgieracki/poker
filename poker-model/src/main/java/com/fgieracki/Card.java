package com.fgieracki;

import java.util.Objects;

/**
 * @author fgieracki
 * @version 1.0
 *
 * Card class
 *
 * @param rank - rank of the card
 *             14 - Ace
 *             13 - King
 *             12 - Queen
 *             11 - Jack
 *              2 - 10 - numbers of the card
 *
 * @param suit - suit of the card
 *             0 - Spades
 *             1 - Hearts
 *             2 - Diamonds
 *             3 - Clubs
 *
 * @function getRank() - returns rank of the card
 * @function getSuit() - returns suit of the card
 * @function toString() - returns string representation of the card
 * @function equals() - returns true if two cards are equal
 * @function hashCode() - returns hash code of the card
 *
 */


public class Card {
    private int rank;
    private int suit;

    //class constructor
    public Card(int rank, int suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Card() {
        this.rank = 0;
        this.suit = 0;
    }

    //getters and setters

    //getters
    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }


    //setters

    /**
     * @param rank - rank of the card
     */
    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * @param suit - suit of the card
     */
    public void setSuit(int suit) {
        this.suit = suit;
    }


    /**
     * @return string representation of the card
     */
    public void showCard(){
        System.out.println(rank + " " + suit);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }


    //convert rank to string
    public String rankToString(){
        switch (rank){
            case 14:
                return "Ace";
            case 13:
                return "King";
            case 12:
                return "Queen";
            case 11:
                return "Jack";
            default:
                return Integer.toString(rank);
        }
    }

    public String suitToString(){
        switch (suit){
            case 0:
                return "Spades";
            case 1:
                return "Hearts";
            case 2:
                return "Diamonds";
            case 3:
                return "Clubs";
            default:
                return Integer.toString(suit);
        }
    }

    public int descendingCompare(Card card) {
        if (this.rank > card.rank) {
            return -1;
        } else if (this.rank < card.rank) {
            return 1;
        } else {
            return 0;
        }
    }
}
