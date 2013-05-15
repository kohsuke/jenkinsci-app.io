package org.jenkinsci.plugins.kickfolio;

import com.cloudbees.plugins.credentials.BaseCredentials;
import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import hudson.Extension;
import hudson.util.Secret;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * @author Kohsuke Kawaguchi, Mark Prichard
 */
public class KickfolioCredentials extends BaseCredentials {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final String username;
    private final Secret apiKey;

    @DataBoundConstructor
    public KickfolioCredentials(String username, Secret apiKey) {
        this.username = username;
        this.apiKey = apiKey;
    }

    public String getUsername() {
        return username;
    }

    public Secret getApiKey() {
        return apiKey;
    }

    @Extension
    public static class DescriptorImpl extends CredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return "Kickfolio Credentials";
        }
    }
}
