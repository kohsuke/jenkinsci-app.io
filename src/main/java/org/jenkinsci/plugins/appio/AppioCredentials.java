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
    private final Secret filepickerApiKey;
    private final Secret appioApiKey;

    @DataBoundConstructor
    public AppioCredentials(Secret fpApiKey, Secret kfApiKey) {
        this.filepickerApiKey = fpApiKey;
        this.appioApiKey = kfApiKey;
    }

    public Secret getFilepickerApiKey() {
        return filepickerApiKey;
    }

    public Secret getAppioApiKey() {
        return appioApiKey;
    }

    @Extension
    public static class DescriptorImpl extends CredentialsDescriptor {
        @Override
        public String getDisplayName() {
            return "App.io Credentials";
        }
    }
}
