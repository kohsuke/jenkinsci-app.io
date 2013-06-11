package org.jenkinsci.plugins.appio.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AppioApps {
    AppioAppObject[] apps;

    public AppioApps() {
        super();
    }

    public AppioApps(AppioAppObject[] apps) {
        super();
        this.apps = apps;
    }

    public AppioAppObject[] getApps() {
        return apps;
    }

    public void setApps(AppioAppObject[] apps) {
        this.apps = apps;
    }
}
