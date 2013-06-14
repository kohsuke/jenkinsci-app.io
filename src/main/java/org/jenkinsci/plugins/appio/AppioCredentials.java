package org.jenkinsci.plugins.appio;

import hudson.Extension;
import hudson.util.Secret;

import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.BaseCredentials;

/**
 * @author Kohsuke Kawaguchi, Mark Prichard
 */
public class AppioCredentials extends BaseCredentials {

    private static final long serialVersionUID = 1L;

    // App.io API Key
    private final Secret apiKey;
    
    // AWS Credentials and S3 Bucket
    private final String s3AccessKey;
    private final Secret s3SecretKey;
    private final String s3Bucket;

    @DataBoundConstructor
    public AppioCredentials(String s3AccessKey, Secret s3SecretKey, String s3Bucket, Secret apiKey) {
        this.s3AccessKey = s3AccessKey;
        this.s3SecretKey = s3SecretKey;
        this.s3Bucket = s3Bucket;
        this.apiKey = apiKey;
    }

    public Secret getApiKey() {
		return apiKey;
	}

	public String getS3AccessKey() {
		return s3AccessKey;
	}

	public Secret getS3SecretKey() {
		return s3SecretKey;
	}

	public String getS3Bucket() {
		return s3Bucket;
	}

	@Extension
    public static class DescriptorImpl extends CredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return "App.io Credentials";
        }
    }
}
