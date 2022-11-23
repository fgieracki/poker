package com.fgieracki;

import junit.framework.TestCase;

public class ServerConnectionTest extends TestCase {

    public void testTestRun() {
        boolean fail = false;
        ServerConnection serverConnection = new ServerConnection(null);
        try{
            serverConnection.run();
        } catch (Exception e) {
            fail = true;
        }
        assertEquals(1024, ServerConnection.BUFFER_SIZE);

    }
}