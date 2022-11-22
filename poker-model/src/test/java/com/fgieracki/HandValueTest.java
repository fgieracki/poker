package com.fgieracki;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class HandValueTest {

    @Test
    public void testGetHandValue() {
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(14, 1));
        hand.add(new Card(13, 1));
        hand.add(new Card(12, 1));
        hand.add(new Card(11, 1));
        hand.add(new Card(10, 1));

        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(10, handValue.countHandValue());
        HandValue handValue2 = HandValue.getHandValue(hand);
        Assert.assertEquals(10, handValue2.countHandValue());
    }

    @Test
    public void testRoyalFlush(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(14, 1));
        hand.add(new Card(13, 1));
        hand.add(new Card(12, 1));
        hand.add(new Card(11, 1));
        hand.add(new Card(10, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(10, handValue.countHandValue());

        Assert.assertEquals(14, handValue.getNthHighestCard(0));
        Assert.assertEquals(13, handValue.getNthHighestCard(1));
        Assert.assertEquals(12, handValue.getNthHighestCard(2));
        Assert.assertEquals(11, handValue.getNthHighestCard(3));
        Assert.assertEquals(10, handValue.getNthHighestCard(4));
    }

    @Test
    public void testStraightFlush(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(8, 1));
        hand.add(new Card(12, 1));
        hand.add(new Card(11, 1));
        hand.add(new Card(10, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(9, handValue.countHandValue());
    }

    @Test
    public void testFourOfAKind(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(9, 3));
        hand.add(new Card(9, 0));
        hand.add(new Card(10, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(8, handValue.countHandValue());

        hand = new ArrayList<>();
        hand.add(new Card(8, 1));
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(9, 3));
        hand.add(new Card(9, 0));
        handValue = new HandValue(hand);
        Assert.assertEquals(8, handValue.countHandValue());

    }

    @Test
    public void testFullHouse(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(9, 3));
        hand.add(new Card(10, 0));
        hand.add(new Card(10, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(7, handValue.countHandValue());

        hand = new ArrayList<>();
        hand.add(new Card(8, 0));
        hand.add(new Card(8, 1));
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(9, 3));
        handValue = new HandValue(hand);
        Assert.assertEquals(7, handValue.countHandValue());

    }

    @Test
    public void testFlush(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(4, 1));
        hand.add(new Card(2, 1));
        hand.add(new Card(7, 1));
        hand.add(new Card(5, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(6, handValue.countHandValue());
    }

    @Test
    public void testStraight(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(8, 2));
        hand.add(new Card(12, 3));
        hand.add(new Card(11, 0));
        hand.add(new Card(10, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(5, handValue.countHandValue());
    }

    @Test
    public void testThreeOfAKind(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(9, 3));
        hand.add(new Card(10, 0));
        hand.add(new Card(11, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(4, handValue.countHandValue());

        hand = new ArrayList<>();
        hand.add(new Card(8, 0));
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(9, 3));
        hand.add(new Card(11, 1));
        handValue = new HandValue(hand);
        Assert.assertEquals(4, handValue.countHandValue());

        hand = new ArrayList<>();
        hand.add(new Card(7, 1));
        hand.add(new Card(8, 0));
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(9, 3));

        handValue = new HandValue(hand);
        Assert.assertEquals(4, handValue.countHandValue());

    }

    @Test
    public void testTwoPair(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(10, 3));
        hand.add(new Card(10, 0));
        hand.add(new Card(11, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(3, handValue.countHandValue());

        hand = new ArrayList<>();
        hand.add(new Card(8, 1));
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(10, 3));
        hand.add(new Card(10, 0));
        handValue = new HandValue(hand);
        Assert.assertEquals(3, handValue.countHandValue());

        hand = new ArrayList<>();
        hand.add(new Card(8, 1));
        hand.add(new Card(8, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(10, 3));
        hand.add(new Card(10, 0));
        handValue = new HandValue(hand);
        Assert.assertEquals(3, handValue.countHandValue());
    }

    @Test
    public void testPair(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(9, 2));
        hand.add(new Card(10, 3));
        hand.add(new Card(11, 0));
        hand.add(new Card(12, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(2, handValue.countHandValue());
    }

    @Test
    public void testHighCard(){
        ArrayList<Card> hand = new ArrayList<>();
        hand.add(new Card(9, 1));
        hand.add(new Card(7, 2));
        hand.add(new Card(11, 3));
        hand.add(new Card(1, 0));
        hand.add(new Card(13, 1));
        HandValue handValue = new HandValue(hand);
        Assert.assertEquals(1, handValue.countHandValue());
    }

}