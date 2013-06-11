package org.jenkinsci.plugins.appio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.jenkinsci.plugins.appio.model.AppioAppObject;
import org.jenkinsci.plugins.appio.model.AppioVersionObject;
import org.jenkinsci.plugins.appio.service.AppioService;
import org.jenkinsci.plugins.appio.service.FilepickerService;
import org.jenkinsci.plugins.appio.service.S3Service;
import org.junit.Test;

public class AppioServiceTest {

    // Test properties loaded via getClassLoader().getResourceAsStream()
    private String propertyPackage = ("org/jenkinsci/plugins/appio/");
    private String propertyFile = propertyPackage + "test.properties";

    // KickfolioService test variables
    private String apiKeyRaw = null;
    private String apiKey = null;
    private String appName = null;
    private String badKey = null;
    private String badName = null;

    // FilepickerService test variables
    private String filePath = null;
    private String fpApiKey = null;

    // Amazon S3 test variables
    private String bucketName = null;
    private String keyName = null;
    private String uploadFile = null;

    private Properties testProperties = new Properties();

    public AppioServiceTest() {
        super();
        loadTestProperties();
    }

    // Utility to load test properties
    public void loadTestProperties() {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream(propertyFile);

        try {
            testProperties.load(in);

            apiKeyRaw = testProperties.getProperty("Appio.apiKeyRaw");
            byte[] encodedBytes = Base64.encodeBase64(apiKeyRaw.getBytes());
            apiKey = new String(encodedBytes);

            appName = testProperties.getProperty("Appio.appName");
            badKey = testProperties.getProperty("Appio.badKey");
            badName = testProperties.getProperty("Appio.badName");
            filePath = testProperties.getProperty("Appio.filePath");
            fpApiKey = testProperties.getProperty("Filepicker.apiKey");

            bucketName = testProperties.getProperty("S3.bucketName");
            keyName = testProperties.getProperty("S3.keyName");
            uploadFile = testProperties.getProperty("S3.uploadFile");

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void createApp() {
        AppioAppObject testAppObject = null;
        AppioService testService = new AppioService();

        try {
            // Create a new Kickfolio app
            testAppObject = testService.createApp(appName, apiKey);

            // Quick test for valid UUID
            UUID uid = UUID.fromString(testAppObject.getId());
            assertEquals(uid.toString().equals(testAppObject.getId()), true);

            // Cleanup: delete the app object
            testService.deleteApp(testAppObject.getId(), apiKey);

        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void createAppBadKey() {
        AppioAppObject testAppObject = null;
        AppioService testService = new AppioService();

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
        AppioAppObject testAppObject = null;
        AppioService testService = new AppioService();

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
            testService.deleteApp(testAppObject.getId(), apiKey);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAppNotFound() {
        AppioAppObject testAppObject = null;
        AppioService testService = new AppioService();

        try {
            testAppObject = testService.findApp(badName, apiKey);
            assertEquals(testAppObject.getId(), null);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void findAppBadKey() {
        AppioAppObject testAppObject = null;
        AppioService testService = new AppioService();

        try {
            testAppObject = testService.findApp(appName, badKey);
            assertEquals(testAppObject.getId(), null);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
    }

    @Test
    public void addVersionFilepicker() {
        AppioService testService = new AppioService();

        try {
            // Upload new bits via Filepicker
            FilepickerService filepicker = new FilepickerService();
            String fileUrl = filepicker.getUploadURL(filePath, fpApiKey);

            // Create a new App.io app
            AppioAppObject testAppObject = testService
                    .createApp(appName, apiKey);

            // Add a new version
            AppioVersionObject testVersionObject = testService
                    .addVersion(testAppObject.getId(), fileUrl, apiKey);

            // Get app info and check for new version id
            testAppObject = testService.findApp(appName, apiKey);
            assertEquals(testAppObject.getId(), testVersionObject.getApp_id());
            assertEquals(testAppObject.getVersion_ids()[0], testVersionObject.getId());

            // Cleanup: delete the app object
            testService.deleteApp(testAppObject.getId(), apiKey);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void addVersionS3() {
        AppioService testService = new AppioService();

        try {
            // Upload new bits via Amazon S3
            S3Service s3service = new S3Service();
            String fileUrl = s3service
                    .getUploadUrl(bucketName, keyName, uploadFile);

            // Create a new App.io app
            AppioAppObject testAppObject = testService
                    .createApp(appName, apiKey);

            // Add a new version
            AppioVersionObject testVersionObject = testService
                    .addVersion(testAppObject.getId(), fileUrl, apiKey);

            // Get app info and check for new version id
            testAppObject = testService.findApp(appName, apiKey);

            assertEquals(testAppObject.getId(), testVersionObject.getApp_id());
            assertEquals(testAppObject.getVersion_ids()[0], testVersionObject.getId());

            // Cleanup: delete the app object
            testService.deleteApp(testAppObject.getId(), apiKey);

        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
