package com.fgieracki;

import org.junit.Test;

import static org.junit.Assert.*;

public class CardTest {

    @Test
    public void testEquals() {
        Card card1 = new Card(1, 1);
        Card card2 = new Card(card1.getRank(), card1.getSuit());
        Card card3 = new Card(2, 2);
        Card card4 = new Card(1, 2);
        Card card5 = new Card(2, 1);
        Player player1 = new Player();
        assertEquals(false, card1.equals(player1));
        assertEquals(false, card1.equals(null));
        assertEquals(card1, card2);
        assertEquals(card1.hashCode(), card2.hashCode());
        assertEquals(card2, card1);
        assertNotEquals(card1, card3);
        assertNotEquals(card1, card4);
        assertNotEquals(card4, card5);

    }

    @Test
    public void testToString() {
        Card card1 = new Card(1, 1);
        Card card2 = new Card(1, 1);
        Card card3 = new Card(2, 2);
        Card card4 = new Card(1, 2);
        Card card5 = new Card(12, 1);

        assertEquals("[1 Hearts]", card1.toString());
        assertEquals("[1 Hearts]", card2.toString());
        assertEquals("[2 Diamonds]", card3.toString());
        assertEquals("[1 Diamonds]", card4.toString());
        assertEquals("[Queen Hearts]", card5.toString());
    }

    @Test
    public void rankToString() {
        Card card1 = new Card(14, 1);
        Card card2 = new Card(11, 1);
        Card card3 = new Card(2, 2);
        Card card4 = new Card(13, 2);
        Card card5 = new Card(12, 1);
        Card card6 = new Card(12, 5);

        assertEquals("Ace", card1.rankToString());
        assertEquals("Jack", card2.rankToString());
        assertEquals("2", card3.rankToString());
        assertEquals("King", card4.rankToString());
        assertEquals("Queen", card5.rankToString());
        assertEquals("5", card6.suitToString());
    }

    @Test
    public void suitToString() {
        Card card1 = new Card(14, 1);
        Card card2 = new Card(1, 3);
        Card card3 = new Card(2, 2);
        Card card4 = new Card(13, 0);
        Card card5 = new Card(12, 1);

        assertEquals("Hearts", card1.suitToString());
        assertEquals("Clubs", card2.suitToString());
        assertEquals("Diamonds", card3.suitToString());
        assertEquals("Spades", card4.suitToString());
        assertEquals("Hearts", card5.suitToString());
    }

    @Test
    public void descendingCompare() {
        Card card1 = new Card(14, 1);
        Card card2 = new Card(1, 3);
        Card card3 = new Card(2, 2);
        Card card4 = new Card(13, 0);
        Card card5 = new Card(12, 1);

        assertEquals(-1, card1.descendingCompare(card2));
        assertEquals(1, card2.descendingCompare(card1));
        assertEquals(0, card1.descendingCompare(card1));
        assertEquals(-1, card1.descendingCompare(card3));
        assertEquals(1, card3.descendingCompare(card1));
        assertEquals(-1, card1.descendingCompare(card4));
        assertEquals(1, card4.descendingCompare(card1));
        assertEquals(-1, card1.descendingCompare(card5));
        assertEquals(1, card5.descendingCompare(card1));
    }
}