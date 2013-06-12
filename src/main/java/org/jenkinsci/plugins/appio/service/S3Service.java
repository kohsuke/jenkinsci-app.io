package org.jenkinsci.plugins.appio.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.PropertiesCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3Service implements Serializable {
    private static final long serialVersionUID = 1L;

    private Logger logger = null;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    static interface Logger {
        void logDebug(String message);
    }

    private void logDebug(String message) {
        if (logger != null) {
            logger.logDebug(message);
        } else {
            System.out.println(message);
        }
    }

    public String getUploadUrl(String bucketName, String keyName, String uploadFile) {

        try {
            AmazonS3 s3client = new AmazonS3Client(
                    new PropertiesCredentials(
                            this.getClass()
                                    .getClassLoader()
                                    .getResourceAsStream("org/jenkinsci/plugins/appio/AwsCredentials.properties")));

            File file = new File(uploadFile);
            s3client.putObject(new PutObjectRequest(bucketName, keyName, file)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

        } catch (AmazonServiceException ase) {
            logDebug("AmazonServiceException");
            logDebug("Error Message:    " + ase.getMessage());
            logDebug("HTTP Status Code: " + ase.getStatusCode());
            logDebug("AWS Error Code:   " + ase.getErrorCode());
            logDebug("Error Type:       " + ase.getErrorType());
            logDebug("Request ID:       " + ase.getRequestId());
            return null;
        } catch (AmazonClientException ace) {
            logDebug("AmazonClientException");
            logDebug("Error Message: " + ace.getMessage());
            return null;
        } catch (IOException ioe) {
            logDebug("IOException");
            return null;
        }

        return new String("https://s3.amazonaws.com/" + bucketName + "/"
                + keyName);
    }
}
