package org.jenkinsci.plugins.kickfolio;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.List;

/**
 * @author Kohsuke Kawaguchi
 */
public class KickfolioRecorder extends Recorder {
    private String ipaFile;

    @DataBoundConstructor
    public KickfolioRecorder(String ipaFile) {
        this.ipaFile = ipaFile;
    }

    public String getIpaFile() {
        return ipaFile;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Deploying to Kickfolio!");

        // TODO:
        FilePath ipa = build.getWorkspace().child(ipaFile);
        // InputStream is = ipa.read();

        List<KickfolioCredentials> c = CredentialsProvider.lookupCredentials(KickfolioCredentials.class, build.getProject());
        KickfolioCredentials x = c.get(0);// TODO: check if there's a better way to do it

        listener.getLogger().println(String.format("Using %s:%s", x.getUsername(), x.getApiKey()));

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
            return "Deploy to Kickfoliio";
        }
    }
}
