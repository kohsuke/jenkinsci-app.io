package org.jenkinsci.plugins.appio;

import hudson.model.ProminentProjectAction;

/**
 * @author Kohsuke Kawaguchi
 */
public class AppioProjectAction implements ProminentProjectAction {

    public String getIconFileName() {
        return "setting.png";
    }

    public String getDisplayName() {
        return "Running App";
    }

    public String getUrlName() {
        return "kickfolio";
    }
}
