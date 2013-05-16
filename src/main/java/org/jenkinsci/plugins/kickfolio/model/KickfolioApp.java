package org.jenkinsci.plugins.kickfolio.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

@XmlRootElement
public class KickfolioApp {
    @Expose
    private KickfolioAppObject app;

    public KickfolioApp() {
        super();
    }

    public KickfolioApp(KickfolioAppObject app) {
        super();
        this.app = app;
    }

    public KickfolioAppObject getApp() {
        return app;
    }

    public void setApp(KickfolioAppObject app) {
        this.app = app;
    }
}
