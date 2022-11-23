package com.fgieracki;

import junit.framework.TestCase;

public class ClientTest extends TestCase {




    public void testMain() {
        Client.main(new String[]{"test"});
        assertEquals(9999, Client.port);
    }
}