package com.fgieracki;

import java.util.Objects;

/**
 * @author fgieracki
 * @version 1.0
 *
 * Card class
 *
 *        rank - rank of the card
 *             14 - Ace
 *             13 - King
 *             12 - Queen
 *             11 - Jack
 *              2 - 10 - numbers of the card
 *
 *        suit - suit of the card
 *             0 - Spades
 *             1 - Hearts
 *             2 - Diamonds
 *             3 - Clubs
 *
 *  getRank() - returns rank of the card
 *  getSuit() - returns suit of the card
 *  toString() - returns string representation of the card
 *  equals() - returns true if two cards are equal
 *  hashCode() - returns hash code of the card
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

    //getters
    public int getRank() {
        return rank;
    }

    public int getSuit() {
        return suit;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    /**
     *  toString()
     * @return string representation of the card
     */
    @Override
    public String toString(){
        return "[" + rankToString() + " " + suitToString() + "]";
    }

    /**
     *  rankToString() - returns string representation of the rank
     * @return string representation of the rank
     */
    public String rankToString(){
        return switch (rank) {
            case 14 -> "Ace";
            case 13 -> "King";
            case 12 -> "Queen";
            case 11 -> "Jack";
            default -> Integer.toString(rank);
        };
    }

    /**
     *  suitToString() - returns string representation of the suit
     * @return string representation of the suit
     */
    public String suitToString(){
        return switch (suit) {
            case 0 -> "Spades";
            case 1 -> "Hearts";
            case 2 -> "Diamonds";
            case 3 -> "Clubs";
            default -> Integer.toString(suit);
        };
    }

    /**
     *  descendingCompare() - compares two cards
     * @param card
     * @return 1 if this card is greater than the card passed as a parameter
     */
    public int descendingCompare(Card card) {
        return Integer.compare(card.rank, this.rank);
    }
}
