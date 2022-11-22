package com.fgieracki;

import org.junit.Test;
import static org.junit.Assert.*;

public class DeckTest {

    @Test
    public void shuffle() {
        Deck deck = new Deck();
        deck.shuffle();
        assertEquals(52, deck.getCards().size());
        assertEquals(52, deck.deckSize());
    }

    @Test
    public void deckSize() {
        Deck deck = new Deck();
        assertEquals(52, deck.deckSize());
        deck.getTopCard();
        assertEquals(51, deck.deckSize());
    }

    @Test
    public void getTopCard() {
        Deck deck = new Deck();
        Card topCard = deck.getCards().get(0);
        assertEquals(topCard, deck.getTopCard());
        while(deck.deckSize() > 0){
            deck.getTopCard();
        }
        assertNull(deck.getTopCard());
    }
}