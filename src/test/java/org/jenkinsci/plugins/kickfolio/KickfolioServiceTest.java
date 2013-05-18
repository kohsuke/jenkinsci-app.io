package org.jenkinsci.plugins.kickfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.UUID;

import org.jenkinsci.plugins.kickfolio.model.KickfolioAppObject;
import org.jenkinsci.plugins.kickfolio.model.KickfolioVersionObject;
import org.junit.Test;

public class KickfolioServiceTest {
    private final String apiKey = "UDFOZG9Gd21MdExIVjlwamtkamg=";
    private final String appName = "Stockfish";
    private final String badKey = "foo";
    private final String badName = "foo";

    private final String filePath = "/Users/markprichard/Documents/Kickfolio/StockFish.zip";
    private final String fpApiKey = "AM5ozphAqSNKD6vhNfBivz";

    // For chained tests
    private KickfolioAppObject testAppObject = new KickfolioAppObject();
    private KickfolioVersionObject testVersionObject = new KickfolioVersionObject();

    @Test
    public void createApp() {
        KickfolioAppObject result = null;
        KickfolioService testService = new KickfolioService();
        try {
            result = testService.createApp(appName, apiKey);
            testAppObject = result;
            System.out
                    .println(result.getName() + ", " + result.getPublic_key());

            // Quick test for valid UUID
            UUID uid = UUID.fromString(result.getId());
            assertEquals(uid.toString().equals(result.getId()), true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void createAppBadKey() {
        KickfolioAppObject result = null;
        KickfolioService testService = new KickfolioService();
        try {
            result = testService.createApp(appName, badKey);
            System.out.println(result);

            // Quick test for valid UUID
            UUID uid = UUID.fromString(result.getId());
            assertEquals(uid.toString().equals(result.getId()), true);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
    }

    @Test
    public void findApp() {
        KickfolioAppObject result = null;
        KickfolioService testService = new KickfolioService();
        try {
            result = testService.findApp(appName, apiKey);
            System.out.println(result.getId());
            System.out.println(result.getPublic_key());

            // Quick test for valid UUID
            UUID uid = UUID.fromString(result.getId());
            assertEquals(uid.toString().equals(result.getId()), true);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAppNotFound() {
        KickfolioAppObject result = null;
        KickfolioService testService = new KickfolioService();
        try {
            result = testService.findApp(badName, apiKey);
            assertEquals(result.getId(), null);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAppBadKey() {
        KickfolioAppObject result = null;
        KickfolioService testService = new KickfolioService();
        try {
            result = testService.findApp(appName, badKey);
            assertEquals(result.getId(), null);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
    }

    @Test
    public void addVersion() {
        // KickfolioVersionObject result = null;
        KickfolioService testService = new KickfolioService();
        try {
            // Upload new bits via Filepicker
            FilepickerService filepicker = new FilepickerService();
            String fileUrl = filepicker.getUploadURL(filePath, fpApiKey);

            // Create a new Kickfolio app
            KickfolioAppObject testAppObject = testService
                    .createApp(appName, apiKey);

            // Add a new version
            KickfolioVersionObject testVersionObject = testService
                    .addVersion(testAppObject.getId(), fileUrl, apiKey);

            // Get app info and check for new version id
            testAppObject = testService.findApp(appName, apiKey);
            assertEquals(testAppObject.getId(), testVersionObject.getApp_id());
            assertEquals(testAppObject.getVersion_ids()[0], testVersionObject.getId());

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
