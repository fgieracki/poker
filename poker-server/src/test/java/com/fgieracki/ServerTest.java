package com.fgieracki;

import junit.framework.TestCase;

public class ServerTest extends TestCase {

    public void testServerModules(){
        Server.game = new Game(10);
        Server.maxPlayers = 4;
        Server.game.addPlayers(4);

        boolean error = false;

        Server.validateCommands(0, "!ready dasdasdas");
        Server.validateCommands(0, "!ready 10");
        Server.validateCommands(0, "!ready 30");
        Server.validateCommands(1, "!ready 40");
        Server.validateCommands(0, "!hand");
        Server.validateCommands(0, "!info");
        Server.validateCommands(0, "!turn");
        Server.validateCommands(0, "!bet");


        assertEquals(30, Server.game.getPlayer(0).getChips());
        assertEquals(40, Server.game.getPlayer(1).getChips());

        Server.playSmallBlind();
        Server.nextTurn();
        Server.playBigBlind();
        Server.nextTurn();

        assertEquals(20, Server.game.getPlayer(0).getChips());
        assertEquals(20, Server.game.getPlayer(1).getChips());

        Server.game.getPlayer(0).setChips(200);
        Server.game.getPlayer(1).setChips(200);
        String[] args = {"!bet", "raise", "50"};
        Server.playerBet(0, args);
        args[1] = "call";
        Server.playerBet(1, args);
        assertEquals(160, Server.game.getPlayer(0).getChips());
        assertEquals(170, Server.game.getPlayer(1).getChips());
        args[1] = "allin";
        Server.playerBet(0, args);
        assertEquals(0, Server.game.getPlayer(0).getChips());
        args[1] = "check";
        Server.playerBet(1, args);
        args[1] = "fold";
        Server.playerBet(1, args);
        assertEquals(170, Server.game.getPlayer(1).getChips());

        Server.firstBettingRound = true;
        Server.validateCommands(0, "!bet");
        Server.validateCommands(0, "!draw");
        Server.drawRound = true;
        Server.validateCommands(0, "!draw");
        Server.firstBettingRound = false;
        Server.secondBettingRound = true;
        Server.validateCommands(0, "!bet");
        Server.validateCommands(0, "!!stop");
        assertEquals(170, Server.game.getPlayer(1).getChips());
    }
}