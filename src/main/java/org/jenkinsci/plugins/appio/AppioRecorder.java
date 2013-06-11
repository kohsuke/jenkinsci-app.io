package org.jenkinsci.plugins.appio;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;

import java.io.IOException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.plugins.credentials.CredentialsProvider;

/**
 * @author Kohsuke Kawaguchi
 */
public class AppioRecorder extends Recorder {
    private String zipFile;
    private String appName;

    public String getAppName() {
        return appName;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new AppioProjectAction();
    }

    @DataBoundConstructor
    public AppioRecorder(String zipFile, String appName) {
        this.zipFile = zipFile;
        this.appName = appName;
    }

    public String getAppFile() {
        return zipFile;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Deploying to Kickfolio!");

        FilePath zipPath = build.getWorkspace().child(zipFile);
        // InputStream is = zipPath.read();

        List<AppioCredentials> c = CredentialsProvider
                .lookupCredentials(AppioCredentials.class, build.getProject());
        AppioCredentials x = c.get(0);

        listener.getLogger().println("Filepicker API Key: "
                + x.getFilepickerApiKey());
        byte[] encodedBytes = Base64.encodeBase64(x.getAppioApiKey()
                .getPlainText().getBytes());
        String appioApiKeyBase64 = new String(encodedBytes);
        listener.getLogger().println("App.io API key: " + appioApiKeyBase64);

        return true;
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Upload to App.io";
        }
    }
}
