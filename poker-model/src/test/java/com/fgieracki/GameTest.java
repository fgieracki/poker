package com.fgieracki;

import junit.framework.TestCase;
import org.junit.Test;

public class GameTest extends TestCase {


    public void testPot() {
        Game game = new Game(10);
        game.setPot(100);
        assertEquals(100, game.getPot());
        game.addPot(100);
        assertEquals(200, game.getPot());
        game.removePot(100);
        assertEquals(100, game.getPot());
    }


    public void testPlayers() {
        Game game = new Game(10);
        game.addPlayers(2);
        assertEquals(2, game.getPlayerAmount());
        game.addPlayer();
        assertEquals(3, game.getPlayerAmount());
    }


    public void testGetWinner() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.addPlayer();
        game.addPlayer();
        game.startRound();
        game.getPlayer(0).setChips(100);
        game.getPlayer(1).setChips(100);
        game.getPlayer(0).clearHand();
        game.getPlayer(1).clearHand();
        game.getPlayer(0).hand.add(new Card(14, 1));
        game.getPlayer(0).hand.add(new Card(13, 1));
        game.getPlayer(0).hand.add(new Card(12, 1));
        game.getPlayer(0).hand.add(new Card(11, 1));
        game.getPlayer(0).hand.add(new Card(10, 1));

        game.getPlayer(1).hand.add(new Card(9, 2));
        game.getPlayer(1).hand.add(new Card(13, 2));
        game.getPlayer(1).hand.add(new Card(12, 2));
        game.getPlayer(1).hand.add(new Card(11, 2));
        game.getPlayer(1).hand.add(new Card(10, 2));

        assertEquals(0, game.getWinner());
    }

    public void testTieBreaker1(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.addPlayer();
        game.addPlayer();
        game.startRound();
        game.getPlayer(0).setChips(100);
        game.getPlayer(1).setChips(100);
        game.getPlayer(0).clearHand();
        game.getPlayer(1).clearHand();
        game.getPlayer(0).hand.add(new Card(13, 1));
        game.getPlayer(0).hand.add(new Card(12, 1));
        game.getPlayer(0).hand.add(new Card(11, 1));
        game.getPlayer(0).hand.add(new Card(10, 1));
        game.getPlayer(0).hand.add(new Card(9, 1));

        game.getPlayer(1).hand.add(new Card(12, 2));
        game.getPlayer(1).hand.add(new Card(11, 2));
        game.getPlayer(1).hand.add(new Card(10, 2));
        game.getPlayer(1).hand.add(new Card(9, 2));
        game.getPlayer(1).hand.add(new Card(8, 2));

        assertEquals(0, game.getWinner());
    }

    public void testTieBreaker2(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.addPlayer();
        game.addPlayer();
        game.startRound();
        game.getPlayer(0).setChips(100);
        game.getPlayer(1).setChips(100);
        game.getPlayer(0).clearHand();
        game.getPlayer(1).clearHand();
        game.getPlayer(1).hand.add(new Card(13, 1));
        game.getPlayer(1).hand.add(new Card(12, 1));
        game.getPlayer(1).hand.add(new Card(11, 1));
        game.getPlayer(1).hand.add(new Card(10, 1));
        game.getPlayer(1).hand.add(new Card(9, 1));

        game.getPlayer(0).hand.add(new Card(12, 2));
        game.getPlayer(0).hand.add(new Card(11, 2));
        game.getPlayer(0).hand.add(new Card(10, 2));
        game.getPlayer(0).hand.add(new Card(9, 2));
        game.getPlayer(0).hand.add(new Card(8, 2));

        assertEquals(1, game.getWinner());
    }

    public void testFinalTieBreaker(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.addPlayer();
        game.addPlayer();
        game.startRound();
        game.getPlayer(0).setChips(100);
        game.getPlayer(1).setChips(100);
        game.getPlayer(0).clearHand();
        game.getPlayer(1).clearHand();
        game.getPlayer(0).hand.add(new Card(13, 1));
        game.getPlayer(0).hand.add(new Card(12, 1));
        game.getPlayer(0).hand.add(new Card(11, 1));
        game.getPlayer(0).hand.add(new Card(10, 1));
        game.getPlayer(0).hand.add(new Card(9, 1));

        game.getPlayer(1).hand.add(new Card(13, 2));
        game.getPlayer(1).hand.add(new Card(12, 2));
        game.getPlayer(1).hand.add(new Card(11, 2));
        game.getPlayer(1).hand.add(new Card(10, 2));
        game.getPlayer(1).hand.add(new Card(9, 2));

        assertEquals(1, game.getWinner());
    }

    public void testFinalTieBreaker2(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.addPlayer();
        game.addPlayer();
        game.startRound();
        game.getPlayer(0).setChips(100);
        game.getPlayer(1).setChips(100);
        game.getPlayer(0).clearHand();
        game.getPlayer(1).clearHand();
        game.getPlayer(0).hand.add(new Card(13, 1));
        game.getPlayer(0).hand.add(new Card(12, 1));
        game.getPlayer(0).hand.add(new Card(11, 1));
        game.getPlayer(0).hand.add(new Card(10, 1));
        game.getPlayer(0).hand.add(new Card(9, 1));
        game.getPlayer(0).hand.add(new Card(8, 1));

        game.getPlayer(1).hand.add(new Card(13, 2));
        game.getPlayer(1).hand.add(new Card(12, 2));
        game.getPlayer(1).hand.add(new Card(11, 2));
        game.getPlayer(1).hand.add(new Card(10, 2));
        game.getPlayer(1).hand.add(new Card(9, 2));
        game.getPlayer(1).hand.add(new Card(8, 2));

        assertEquals(1, game.getWinner());
    }

    public void testFinalTieBreaker3(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.addPlayer();
        game.addPlayer();
        game.startRound();
        game.getPlayer(0).setChips(100);
        game.getPlayer(1).setChips(100);
        game.getPlayer(0).clearHand();
        game.getPlayer(1).clearHand();
        game.getPlayer(1).hand.add(new Card(13, 1));
        game.getPlayer(1).hand.add(new Card(12, 1));
        game.getPlayer(1).hand.add(new Card(11, 1));
        game.getPlayer(1).hand.add(new Card(10, 1));
        game.getPlayer(1).hand.add(new Card(9, 1));

        game.getPlayer(0).hand.add(new Card(13, 2));
        game.getPlayer(0).hand.add(new Card(12, 2));
        game.getPlayer(0).hand.add(new Card(11, 2));
        game.getPlayer(0).hand.add(new Card(10, 2));
        game.getPlayer(0).hand.add(new Card(9, 2));

        assertEquals(0, game.getWinner());
    }

    public void testBasicWinnerSelection(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.addPlayer();
        game.addPlayer();
        game.startRound();
        game.getPlayer(0).setChips(100);
        game.getPlayer(1).setChips(100);
        game.getPlayer(0).clearHand();
        game.getPlayer(1).clearHand();
        game.getPlayer(0).hand.add(new Card(13, 1));
        game.getPlayer(0).hand.add(new Card(12, 1));
        game.getPlayer(0).hand.add(new Card(11, 1));
        game.getPlayer(0).hand.add(new Card(10, 1));
        game.getPlayer(0).hand.add(new Card(9, 1));

        game.getPlayer(1).hand.add(new Card(13, 2));
        game.getPlayer(1).hand.add(new Card(12, 2));
        game.getPlayer(1).hand.add(new Card(11, 2));
        game.getPlayer(1).hand.add(new Card(10, 2));
        game.getPlayer(1).hand.add(new Card(9, 2));
        game.bet(1, Game.Decision.FOLD, 0);

        assertEquals(0, game.getWinner());
    }

    public void testGetLastPlayerAction() {
        Game game = new Game(10);
        game.setLastPlayerAction(1);
        assertEquals(1, game.getLastPlayerAction());

    }


    public void testGetSmallBlindValue() {
        Game game = new Game(10);
        assertEquals(10, game.getSmallBlindValue());
    }

    public void testGetBigBlindValue() {
        Game game = new Game(10);
        assertEquals(20, game.getBigBlindValue());
    }

    public void testGetPlayerPot() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        game.bet(0, Game.Decision.RAISE, 30);
        assertEquals(30, game.getPlayerPot(0));
    }



    public void testGetDealer() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        assertEquals(0, game.getDealer());
    }

    public void testGetPlayerTurn() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        assertEquals(0, game.getPlayerTurn());
    }

    public void testGetDeck() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        assertEquals(42, game.getDeck().deckSize());
    }

    public void testPlayerTurn() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        game.nextPlayerTurn();
        assertEquals(1, game.getPlayerTurn());
        game.setPlayerTurn(0);
        assertEquals(0, game.getPlayerTurn());
    }

    public void testCheckIfPlayersAreReady() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        game.getPlayer(0).setReady(true);
        assertEquals(false, game.checkIfPlayersAreReady());
        game.getPlayer(1).setReady(true);
        assertEquals(true, game.checkIfPlayersAreReady());
    }

    public void testStartRound() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        assertEquals(2, game.getPlayerAmount());
        assertEquals(0, game.getDealer());
        assertEquals(0, game.getPlayerTurn());
        assertEquals(42, game.getDeck().deckSize());
        assertEquals(10, game.getPlayerPot(0));
        assertEquals(10, game.getPlayerPot(1));
        assertEquals(-10, game.getPlayerChips(0));
        assertEquals(-10, game.getPlayerChips(1));
    }

    public void testSetLastPlayerAction() {
        Game game = new Game(10);
        game.setLastPlayerAction(1);
        assertEquals(1, game.getLastPlayerAction());
    }

    public void testBet() {
        Game game = new Game(10);
        game.addPlayers(3);
        game.getPlayer(0).setChips(200);
        game.getPlayer(1).setChips(200);
        assertEquals(200, game.getPlayer(0).getChips());
        game.startRound();
        assertEquals(10, game.getPlayerPot(0));
        assertEquals(190, game.getPlayerChips(0));
        game.bet(0, Game.Decision.RAISE, 100);
        assertEquals(100, game.getHighestBet());
        game.bet(1, Game.Decision.CALL, 0);
        assertTrue(game.checkIfPlayerIsPlaying(0));
        game.bet(0, Game.Decision.FOLD, 0);
        assertFalse(game.checkIfPlayerIsPlaying(0));
        game.bet(1, Game.Decision.CHECK, 0);
        assertFalse(game.checkIfPlayerIsPlaying(0));
        assertEquals(100, game.getPlayerChips(0));
        game.bet(0, Game.Decision.ALL_IN, 0);
        assertEquals(200, game.getPlayerPot(0));
        assertEquals(100, game.getPlayerPot(1));
        assertEquals(0, game.getPlayerChips(0));
        assertEquals(100, game.getPlayerChips(1));
    }

    public void testAddDraw() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        game.addDraw();
        assertEquals(1, game.getDrawCounter());
        game.addDraw();
        assertEquals(2, game.getDrawCounter());
    }

    public void testPlayBlind(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        game.playBlind(20);
        game.getPlayer(0).setChips(200);
        assertEquals(10, game.getPlayerPot(1));
        game.playBlind(20);
        assertEquals(20, game.getPlayerPot(0));
    }



    public void testPlayersPlaying() {
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        assertEquals(2, game.playersPlaying());
        game.bet(0, Game.Decision.FOLD, 0);
        assertEquals(1, game.playersPlaying());
        game.bet(0, Game.Decision.CHECK, 0);
        assertEquals(1, game.playersPlaying());
        assertEquals(1, game.playersPlaying());
        game.bet(0, Game.Decision.RAISE, 0);
        assertEquals(2, game.playersPlaying());
        game.bet(0, Game.Decision.ALL_IN, 0);
        assertEquals(2, game.playersPlaying());
    }

    public void testAddCard(){
        Game game = new Game(10);
        game.addPlayers(2);
        game.startRound();
        game.addCard(0, new Card(1, 1));
        assertEquals(6, game.getPlayer(0).getHandSize());
    }

}