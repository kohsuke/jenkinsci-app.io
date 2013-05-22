package org.jenkinsci.plugins.kickfolio;

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

import org.kohsuke.stapler.DataBoundConstructor;

import com.cloudbees.plugins.credentials.CredentialsProvider;

/**
 * @author Kohsuke Kawaguchi
 */
public class KickfolioRecorder extends Recorder {
    private String zipFile;
    private String appName;

    public String getAppName() {
        return appName;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new KickfolioProjectAction();
    }

    @DataBoundConstructor
    public KickfolioRecorder(String zipFile, String appName,
            String developerName, String companyName) {
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

        List<KickfolioCredentials> c = CredentialsProvider
                .lookupCredentials(KickfolioCredentials.class, build
                        .getProject());
        KickfolioCredentials x = c.get(0);

        listener.getLogger().println(String.format("Using %s:%s", x
                .getFilepickerApiKey(), x.getKickfolioApiKey()));

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
            return "Upload to Kickfoliio";
        }
    }
}
