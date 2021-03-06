package org.jenkinsci.plugins.appio;

import hudson.model.ProminentProjectAction;

/**
 * @author Kohsuke Kawaguchi
 */
public class AppioProjectAction implements ProminentProjectAction {
	
	private String appURL = null;

    public String getIconFileName() {
        return "setting.png";
    }

    public String getDisplayName() {
        return "App.io Simulator Link";
    }

    public String getUrlName() {
        return appURL;
    }
    
    public void setAppURL(String url) {
    	this.appURL = url;
    }

	public String getAppURL() {
		return appURL;
	}
}
