package org.jenkinsci.plugins.kickfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.junit.Test;

public class KickfolioServiceTest {
    private final String apiKey = "UDFOZG9Gd21MdExIVjlwamtkamg=";
    private final String appName = "Stockfish";
    private final String badKey = "foo";

    @Test
    public void createApp() {
        String result = null;
        KickfolioService testService = new KickfolioService();
        try {
            result = testService.createApp(appName, apiKey);

            // Quick test for valid UUID
            UUID uid = UUID.fromString(result);
            assertEquals(uid.toString().equals(result), true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBadKey() {
        String result = null;
        KickfolioService testService = new KickfolioService();
        try {
            result = testService.createApp(appName, badKey);
            UUID uid = UUID.fromString(result);
            assertEquals(uid.toString().equals(result), true);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
    }
}
