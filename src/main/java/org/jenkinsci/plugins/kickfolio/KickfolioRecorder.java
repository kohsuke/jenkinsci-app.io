package org.jenkinsci.plugins.kickfolio;

import com.cloudbees.plugins.credentials.CredentialsProvider;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
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
    private String appFile;
    private String appName;
    private String developerName;
    private String companyName;

    public String getAppName() {
		return appName;
	}

	public String getDeveloperName() {
		return developerName;
	}

	public String getCompanyName() {
		return companyName;
	}

	@Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new KickfolioProjectAction();
    }

    @DataBoundConstructor
    public KickfolioRecorder(String ipaFile, 
    						 String appName, 
    						 String developerName,
    						 String companyName) {
        this.appFile = ipaFile;
        this.appName = appName;
		this.developerName = developerName;
		this.companyName = companyName;
    }

    public String getAppFile() {
        return appFile;
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        listener.getLogger().println("Deploying to Kickfolio!");

        // TODO:
        FilePath ipa = build.getWorkspace().child(appFile);
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
            return "Upload to Kickfoliio";
        }
    }
}
