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
import org.jenkinsci.plugins.appio.model.AppioAppObject;
import org.jenkinsci.plugins.appio.model.AppioVersionObject;
import org.jenkinsci.plugins.appio.service.AppioService;
import org.jenkinsci.plugins.appio.service.S3Service;
import org.jenkinsci.plugins.appio.service.ZipService;
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
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher,
			BuildListener listener) throws InterruptedException, IOException {

		FilePath zipPath = build.getWorkspace().child(zipFile);
		listener.getLogger().println("Deploying to App.io: " + zipPath);
		// InputStream is = zipPath.read();

		List<AppioCredentials> c = CredentialsProvider.lookupCredentials(
				AppioCredentials.class, build.getProject());
		AppioCredentials x = c.get(0);

		listener.getLogger().println(
				"App.io API Key: " + x.getApiKey().getPlainText());
		byte[] encodedBytes = Base64.encodeBase64(x.getApiKey().getPlainText()
				.getBytes());
		String appioApiKeyBase64 = new String(encodedBytes);
		listener.getLogger().println(
				"App.io API key (base64): " + appioApiKeyBase64);

		// Zip <build>.app package for upload to S3
		String zippedPath = zipPath.readToString() + ".zip";
		listener.getLogger().println("Creating zip file: " + zippedPath);
		ZipService zipService = new ZipService();
		try {
			// App.io expects the containing folder
			zipService.zipFile(zipPath.readToString(), zippedPath, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Upload <build>.app.zip to S3 bucket
		S3Service s3service = new S3Service(x.getS3AccessKey(), x
				.getS3SecretKey().getPlainText());
		listener.getLogger().println("Uploading to S3 bucket: " + x.getS3Bucket());
		String fileUrl = s3service.getUploadUrl(x.getS3Bucket(), "app",
				zippedPath);

		// Check if app already exists on App.io
		AppioAppObject appObject = null;
		AppioService appioService = new AppioService(x.getApiKey()
				.getPlainText());

		// Create new App.io app if necessary
		if (appObject.getId().isEmpty()) {
			listener.getLogger().println("Creating new App.io application");
			try {
				appObject = appioService.createApp("app");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// Add new version pointing to S3 URL
		listener.getLogger().println("Adding new version");
		try {
			AppioVersionObject versionObject = appioService.addVersion(
					appObject.getId(), fileUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}

		listener.getLogger().println("App.io URL: " + "https://app.io/" + appObject.getPublic_key());
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
