package org.jenkinsci.plugins.kickfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Test;

public class FilepickerServiceTest {
    private String filePath = "/Users/markprichard/Documents/Kickfolio/StockFish.zip";
    private String apiKey = "AM5ozphAqSNKD6vhNfBivz";
    private String badPath = "/Users/markprichard/foo";
    private String badKey = "foo";

    private final String urlPrefix = "https://www.filepicker.io/api/file/";

    private Properties testProperties = new Properties();

    public FilepickerServiceTest() {
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

            filePath = testProperties
                    .getProperty("FilepickerServiceTest.filePath");
            badPath = testProperties
                    .getProperty("FilepickerServiceTest.badPath");

            apiKey = testProperties.getProperty("FilepickerServiceTest.apiKey");
            badKey = testProperties.getProperty("FilepickerServiceTest.badKey");

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
