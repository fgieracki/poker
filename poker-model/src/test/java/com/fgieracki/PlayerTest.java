package com.fgieracki;

import junit.framework.TestCase;

public class PlayerTest extends TestCase {

    public void testGetHandToString() {
        Player player = new Player();
        player.hand.add(new Card(1, 1));
        assertEquals("{[1 Hearts]}", player.getHandToString());
    }

    public void testSetReady() {
        Player player = new Player();
        player.setReady(true);
        assertTrue(player.isReady);
    }

    public void testReplaceCard() {
        Player player = new Player();
        player.hand.add(new Card(1, 1));
        player.replaceCard(0, new Card(2, 2));
        assertEquals("{[2 Diamonds]}", player.getHandToString());
    }

    public void testGetFiveCards() {
        Player player = new Player();
        Deck deck = new Deck();
        player.getFiveCards(deck);
        while(deck.deckSize() > 4) deck.getTopCard();
        player.getFiveCards(deck);
        assertEquals(5, player.hand.size());

    }

    public void testChips() {
        Player player = new Player();
        player.setChips(200);
        assertEquals(200, player.getChips());
        player.addChips(100);
        assertEquals(300, player.getChips());
        player.removeChips(100);
        assertEquals(200, player.getChips());
    }

    public void testClearHand() {
        Player player = new Player();
        player.hand.add(new Card(1, 1));
        player.clearHand();
        assertEquals(0, player.hand.size());
    }


    public void testGetHandValue() {
        Player player = new Player();
        player.hand.add(new Card(1, 1));
        player.hand.add(new Card(2, 1));
        player.hand.add(new Card(3, 1));
        player.hand.add(new Card(4, 1));
        player.hand.add(new Card(5, 1));
        assertEquals(9, player.getHandValue().countHandValue());
    }
}