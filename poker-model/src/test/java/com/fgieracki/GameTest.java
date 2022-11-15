package com.fgieracki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void startGame() {
        Game game = new Game();
        game.addPlayers(2);
        game.startRound();
        assertEquals(2, game.getPlayerAmount());
    }

    @Test
    void addPlayers() {
        Game game = new Game();
        game.addPlayers(3);
        assertEquals(3, game.getPlayerAmount());

    }

    @Test
    void addPlayer() {
        Game game = new Game();
        game.addPlayer();
        assertEquals(1, game.getPlayerAmount());
    }

    @Test
    void testAddPlayer() {
        Game game = new Game();
        game.addPlayer(new Player());
        assertEquals(1, game.getPlayerAmount());
    }

    @Test
    void addPot() {
        Game game = new Game();
        game.addPot(100);
        assertEquals(100, game.getPot());
    }

    @Test
    void removePot() {
        Game game = new Game();
        game.addPot(100);
        game.removePot(50);
        assertEquals(50, game.getPot());
    }

    @Test
    void setPlayersChips() {
        Game game = new Game();
        game.addPlayer();
        game.setPlayersChips(100);
        assertEquals(100, game.getPlayerChips(0));
    }

    @Test
    void addPlayersChips() {
        Game game = new Game();
        game.addPlayer();
        game.addPlayersChips(100);
        assertEquals(100, game.getPlayerChips(0));
    }

    @Test
    void removePlayersChips() {
        Game game = new Game();
        game.addPlayer();
        game.addPlayersChips(100);
        game.removePlayersChips(50);
        assertEquals(50, game.getPlayerChips(0));
    }
}