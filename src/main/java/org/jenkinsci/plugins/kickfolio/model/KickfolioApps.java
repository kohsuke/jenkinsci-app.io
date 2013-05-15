package org.jenkinsci.plugins.kickfolio.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class KickfolioApps {
    KickfolioAppObject[] apps;

    public KickfolioApps() {
        super();
    }

    public KickfolioApps(KickfolioAppObject[] apps) {
        super();
        this.apps = apps;
    }

    public KickfolioAppObject[] getApps() {
        return apps;
    }

    public void setApps(KickfolioAppObject[] apps) {
        this.apps = apps;
    }
}
