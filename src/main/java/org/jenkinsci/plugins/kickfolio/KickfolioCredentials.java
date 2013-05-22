package org.jenkinsci.plugins.kickfolio;

import hudson.Extension;
import hudson.util.Secret;

import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.plugins.credentials.CredentialsDescriptor;
import com.cloudbees.plugins.credentials.BaseCredentials;

/**
 * @author Kohsuke Kawaguchi, Mark Prichard
 */
public class KickfolioCredentials extends BaseCredentials {

    private static final long serialVersionUID = 1L;
    private final String filepickerApiKey;
    private final Secret kickfolioApiKey;

    @DataBoundConstructor
    public KickfolioCredentials(String fpApiKey, Secret kfApiKey) {
        this.filepickerApiKey = fpApiKey;
        this.kickfolioApiKey = kfApiKey;
    }

    public String getFilepickerApiKey() {
        return filepickerApiKey;
    }

    public Secret getKickfolioApiKey() {
        return kickfolioApiKey;
    }

    @Extension
    public static class DescriptorImpl extends CredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return "Kickfolio Credentials";
        }
    }
}
