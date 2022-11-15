package com.fgieracki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void shuffle() {
        Deck deck = new Deck();
        deck.shuffle();
        //check if the deck is shuffled
        for(int i = 0; i < deck.deckSize(); i++){
            for(int j = 0; j < deck.deckSize(); j++){
                if(i != j){
                    assertNotEquals(deck.getCards().get(i), deck.getCards().get(j));
                }
            }
        }
    }

    @Test
    void deckSize() {
        Deck deck = new Deck();
        assertEquals(52, deck.deckSize());
    }

    @Test
    void getTopCard() {
        Deck deck = new Deck();
        Card topCard = deck.getTopCard();
        assertEquals(2, topCard.getRank());
        assertEquals(0, topCard.getSuit());
    }

    @Test
    void getTopCardAfterShuffle() {
        Deck deck = new Deck();
        deck.shuffle();
        Card topCard = deck.getTopCard();
        assertNotEquals(2, topCard.getRank());
        assertNotEquals(0, topCard.getSuit());
    }
}