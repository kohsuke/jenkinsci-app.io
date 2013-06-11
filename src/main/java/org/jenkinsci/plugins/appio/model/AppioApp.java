package org.jenkinsci.plugins.appio.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

@XmlRootElement
public class AppioApp {
    @Expose
    private AppioAppObject app;

    public AppioApp() {
        super();
    }

    public AppioApp(AppioAppObject app) {
        super();
        this.app = app;
    }

    public AppioAppObject getApp() {
        return app;
    }

    public void setApp(AppioAppObject app) {
        this.app = app;
    }
}
