package org.jenkinsci.plugins.kickfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jenkinsci.plugins.kickfolio.model.KickfolioAppObject;
import org.jenkinsci.plugins.kickfolio.model.KickfolioVersionObject;
import org.junit.Test;

public class KickfolioServiceTest {
    // KickfolioService test variables
    private String apiKey = null;
    private String appName = null;
    private String badKey = null;
    private String badName = null;

    // FilepickerService test variables
    private String filePath = null;
    private String fpApiKey = null;

    private Properties testProperties = new Properties();

    public KickfolioServiceTest() {
        super();

        // Get test properties from classpath
        loadTestProperties();
    }

    // Utility to load test properties
    public void loadTestProperties() {
        InputStream in = this
                .getClass()
                .getClassLoader()
                .getResourceAsStream("org/jenkinsci/plugins/kickfolio/test.properties");
        try {
            testProperties.load(in);

            apiKey = testProperties.getProperty("KickfolioServiceTest.apiKey");
            appName = testProperties
                    .getProperty("KickfolioServiceTest.appName");
            badKey = testProperties.getProperty("KickfolioServiceTest.badKey");
            badName = testProperties
                    .getProperty("KickfolioServiceTest.badName");
            filePath = testProperties
                    .getProperty("KickfolioServiceTest.filePath");
            fpApiKey = testProperties
                    .getProperty("KickfolioServiceTest.fpApiKey");

        } catch (IOException e) {
            fail();
        }
    }

    // Utility to delete apps after test cases
    public void deleteApp(String appId, String apiKey) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpHost httpHost = new HttpHost("kickfolio.com", 443, "https");
        HttpDelete httpDelete = new HttpDelete("/api/apps" + "/" + appId);

        String kickfolioAuth = "Basic " + apiKey;
        httpDelete.addHeader("Authorization", kickfolioAuth);
        httpDelete.addHeader("Content-Type", "application/json");

        try {
            httpClient.execute(httpHost, httpDelete);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.getConnectionManager().shutdown();
            } catch (Exception ignore) {
            }
        }
    }

    @Test
    public void createApp() {
        KickfolioAppObject testAppObject = null;
        KickfolioService testService = new KickfolioService();

        try {
            // Create a new Kickfolio app
            testAppObject = testService.createApp(appName, apiKey);

            // Quick test for valid UUID
            UUID uid = UUID.fromString(testAppObject.getId());
            assertEquals(uid.toString().equals(testAppObject.getId()), true);

            // Cleanup: delete the app object
            deleteApp(testAppObject.getId(), apiKey);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createAppBadKey() {
        KickfolioAppObject testAppObject = null;
        KickfolioService testService = new KickfolioService();

        try {
            testAppObject = testService.createApp(appName, badKey);

            // Quick test for valid UUID
            UUID uid = UUID.fromString(testAppObject.getId());
            assertEquals(uid.toString().equals(testAppObject.getId()), true);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
    }

    @Test
    public void findApp() {
        KickfolioAppObject testAppObject = null;
        KickfolioService testService = new KickfolioService();

        try {
            // Create a new Kickfolio app
            testAppObject = testService.createApp(appName, apiKey);

            // Find the newly-created app
            testAppObject = testService.findApp(appName, apiKey);

            // Quick test for valid UUID and appName
            UUID uid = UUID.fromString(testAppObject.getId());
            assertEquals(uid.toString().equals(testAppObject.getId()), true);
            assertEquals(testAppObject.getName(), appName);

            // Cleanup: delete the app
            deleteApp(testAppObject.getId(), apiKey);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAppNotFound() {
        KickfolioAppObject testAppObject = null;
        KickfolioService testService = new KickfolioService();

        try {
            testAppObject = testService.findApp(badName, apiKey);
            assertEquals(testAppObject.getId(), null);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAppBadKey() {
        KickfolioAppObject testAppObject = null;
        KickfolioService testService = new KickfolioService();

        try {
            testAppObject = testService.findApp(appName, badKey);
            assertEquals(testAppObject.getId(), null);
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

            // Cleanup: delete the app object
            deleteApp(testAppObject.getId(), apiKey);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
