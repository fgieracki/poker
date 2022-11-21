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
     * @function showCard() - shows the card
     */
//    public void showCard(){
//        System.out.println(rank + " " + suit);
//    }

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

    @Override
    public String toString(){
        return "[" + rankToString() + " " + suitToString() + "]";
    }

    //convert rank to string
    public String rankToString(){
        return switch (rank) {
            case 14 -> "Ace";
            case 13 -> "King";
            case 12 -> "Queen";
            case 11 -> "Jack";
            default -> Integer.toString(rank);
        };
    }

    public String suitToString(){
        return switch (suit) {
            case 0 -> "Spades";
            case 1 -> "Hearts";
            case 2 -> "Diamonds";
            case 3 -> "Clubs";
            default -> Integer.toString(suit);
        };
    }

    public int descendingCompare(Card card) {
        return Integer.compare(card.rank, this.rank);
    }
}
