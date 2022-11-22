package com.fgieracki;

import java.util.ArrayList;

public class HandValue {
    private int value;
    private final ArrayList<Card> hand;

    public HandValue(ArrayList<Card> hand){
        this.hand = hand;
        hand.sort(Card::descendingCompare);
        value = 0;
    }

    public static HandValue getHandValue(ArrayList<Card> hand) {
        HandValue handValue = new HandValue(hand);
        handValue.countHandValue();
        return handValue;
    }

    public int countHandValue(){
        if(isRoyalFlush()){
            value = 10;
        }
        else if(isStraightFlush()){
            value = 9;
        }
        else if(isFourOfAKind()){
            value = 8;
        }
        else if(isFullHouse()){
            value = 7;
        }
        else if(isFlush()){
            value = 6;
        }
        else if(isStraight()){
            value = 5;
        }
        else if(isThreeOfAKind()){
            value = 4;
        }
        else if(isTwoPairs()){
            value = 3;
        }
        else if(isOnePair()){
            value = 2;
        }
        else{
            value = 1;
        }
        return value;
    }

    //check for royal flush
    private boolean isRoyalFlush(){
        return (isFlush() && isStraight() && hand.get(0).getRank() == 14);
    }

    //check for straight flush
    private boolean isStraightFlush(){
        return (isFlush() && isStraight());
    }

    //check for four of a kind
    private boolean isFourOfAKind(){
         return ((hand.get(0).getRank() == hand.get(1).getRank()
                && hand.get(1).getRank() == hand.get(2).getRank()
                && hand.get(2).getRank() == hand.get(3).getRank()) ||
                (hand.get(1).getRank() == hand.get(2).getRank()
                        && hand.get(2).getRank() == hand.get(3).getRank()
                        && hand.get(3).getRank() == hand.get(4).getRank()));
    }

    //check for full house
    private boolean isFullHouse(){
            return (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(1).getRank() == hand.get(2).getRank() && hand.get(3).getRank() == hand.get(4).getRank())
                    || (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(2).getRank() == hand.get(3).getRank() && hand.get(3).getRank() == hand.get(4).getRank());
        }

    //check for flush
    private boolean isFlush() {
        for (int i = 0; i < hand.size() - 1; i++) {
            if(hand.get(i).getSuit() != hand.get(i+1).getSuit()){
                return false;
            }
        }
        return true;
    }

    //check for straight
    private boolean isStraight() {
        for (int i = 0; i < hand.size() - 1; i++) {
            if(hand.get(i).getRank() - hand.get(i + 1).getRank() != 1){
                return false;
            }
        }
        return true;
    }

    //check for three of a kind
    private boolean isThreeOfAKind(){
        return (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(1).getRank() == hand.get(2).getRank())
                || (hand.get(1).getRank() == hand.get(2).getRank() && hand.get(2).getRank() == hand.get(3).getRank())
                || (hand.get(2).getRank() == hand.get(3).getRank() && hand.get(3).getRank() == hand.get(4).getRank());
    }

    //check for two pairs
    private boolean isTwoPairs(){
        return (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(2).getRank() == hand.get(3).getRank())
                || (hand.get(0).getRank() == hand.get(1).getRank() && hand.get(3).getRank() == hand.get(4).getRank())
                || (hand.get(1).getRank() == hand.get(2).getRank() && hand.get(3).getRank() == hand.get(4).getRank());
    }

    //check for one pair
    private boolean isOnePair(){
         return ((hand.get(0).getRank() == hand.get(1).getRank())
                 || (hand.get(1).getRank() == hand.get(2).getRank())
                 || (hand.get(2).getRank() == hand.get(3).getRank())
                 || (hand.get(3).getRank() == hand.get(4).getRank()));
    }


    public int getNthHighestCard(int n){
        return hand.get(n).getRank();
    }
}
