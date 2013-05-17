package org.jenkinsci.plugins.kickfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class FilepickerServiceTest {
    private final String filePath = "/Users/markprichard/Documents/Kickfolio/StockFish.zip";
    private final String apiKey = "AM5ozphAqSNKD6vhNfBivz";
    private final String badPath = "/Users/markprichard/foo";
    private final String badKey = "foo";
    private final String urlPrefix = "https://www.filepicker.io/api/file/";

    @Test
    public void goodTest() {
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
    public void errorTestPath() {
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
    public void errorTestKey() {
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
