package org.jenkinsci.plugins.kickfolio.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class KickfolioApps {
    KickfolioApp[] apps;

    public KickfolioApps() {
        super();
    }

    public KickfolioApps(KickfolioApp[] apps) {
        super();
        this.apps = apps;
    }

    public KickfolioApp[] getApps() {
        return apps;
    }

    public void setApps(KickfolioApp[] apps) {
        this.apps = apps;
    }
}
