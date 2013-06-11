package org.jenkinsci.plugins.appio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.jenkinsci.plugins.appio.service.S3Service;
import org.junit.Test;

public class S3ServiceTest {

    // Test properties loaded via getClassLoader().getResourceAsStream()
    private String propertyPackage = ("org/jenkinsci/plugins/appio/");
    private String propertyFile = propertyPackage + "test.properties";

    private String bucketName = null;
    private String keyName = null;
    private String uploadFile = null;

    private String badBucket = null;
    private String badFile = null;

    private Properties testProperties = new Properties();

    public S3ServiceTest() {
        super();
        loadTestProperties();
    }

    // Utility to load test properties
    public void loadTestProperties() {
        InputStream in = this.getClass().getClassLoader()
                .getResourceAsStream(propertyFile);
        try {
            testProperties.load(in);

            bucketName = testProperties.getProperty("S3ServiceTest.bucketName");
            keyName = testProperties.getProperty("S3ServiceTest.keyName");
            uploadFile = testProperties.getProperty("S3ServiceTest.uploadFile");
            badFile = testProperties.getProperty("S3ServiceTest.badFile");
            badBucket = testProperties.getProperty("S3ServiceTest.badBucket");

        } catch (IOException e) {
            fail();
        }
    }

    @Test
    public void getUploadURL() {
        S3Service s3UploadService = new S3Service();

        try {
            String uploadURL = s3UploadService
                    .getUploadUrl(bucketName, keyName, uploadFile);
            assertEquals(uploadURL, "https://s3.amazonaws.com/" + bucketName
                    + "/" + keyName);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void getUploadURLBadPath() {
        S3Service s3UploadService = new S3Service();
        String testResult = null;

        try {
            testResult = s3UploadService
                    .getUploadUrl(bucketName, keyName, badFile);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
        assertEquals(testResult, null);
    }

    @Test
    public void getUploadURLBadBucket() {
        S3Service s3UploadService = new S3Service();
        String testResult = null;

        try {
            testResult = s3UploadService
                    .getUploadUrl(badBucket, keyName, uploadFile);
        } catch (Exception e) {
            assertTrue(e.getMessage(), true);
        }
        assertEquals(testResult, null);
    }
}
