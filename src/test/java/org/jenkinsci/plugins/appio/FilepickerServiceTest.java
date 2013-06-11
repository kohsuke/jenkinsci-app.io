package org.jenkinsci.plugins.appio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jenkinsci.plugins.appio.service.FilepickerService;
import org.junit.Test;

public class FilepickerServiceTest {

    // Test properties loaded via getClassLoader().getResourceAsStream()
    private String propertyPackage = ("org/jenkinsci/plugins/appio/");
    private String propertyFile = propertyPackage + "test.properties";

    private String filePath = null;
    private String apiKey = null;
    private String badPath = null;
    private String badKey = null;

    private final String urlPrefix = "https://www.filepicker.io/api/file/";

    private Properties testProperties = new Properties();

    public FilepickerServiceTest() {
        super();

        loadTestProperties();
    }

    // Utility to load test properties
    public void loadTestProperties() {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream(propertyFile);
        try {
            testProperties.load(in);

            filePath = testProperties.getProperty("Filepicker.filePath");
            badPath = testProperties.getProperty("Filepicker.badPath");

            apiKey = testProperties.getProperty("Filepicker.apiKey");
            badKey = testProperties.getProperty("Filepicker.badKey");

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void getUploadURL() {
        FilepickerService testService = new FilepickerService();
        String testResult = null;
        try {
            testResult = testService.getUploadURL(filePath, apiKey);
        } catch (Exception e) {
            fail(e.getMessage());
        }
        assertEquals(testResult.startsWith(urlPrefix), true);
    }

    @Test
    public void getUploadURLBadPath() {
        FilepickerService testService = new FilepickerService();
        String testResult = null;
        try {
            testResult = testService.getUploadURL(badPath, apiKey);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
            assertEquals(e.getMessage(), badPath
                    + " (No such file or directory)");
        }
        assertEquals(testResult, null);
    }

    @Test
    public void getUploadURLBadKey() {
        FilepickerService testService = new FilepickerService();
        String testResult = null;
        try {
            testResult = testService.getUploadURL(filePath, badKey);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
        assertEquals(testResult, null);
    }
}
