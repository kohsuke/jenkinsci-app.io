package org.jenkinsci.plugins.kickfolio;

import hudson.model.ProminentProjectAction;

/**
 * @author Kohsuke Kawaguchi
 */
public class KickfolioProjectAction implements ProminentProjectAction {

    public int foo(int a, int b) {
        return a+b;
    }

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
