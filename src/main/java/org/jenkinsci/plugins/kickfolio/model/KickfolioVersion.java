package org.jenkinsci.plugins.kickfolio.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

@XmlRootElement
public class KickfolioVersion {
    @Expose
    private KickfolioVersionObject version;

    public KickfolioVersion() {
        super();
    }

    public KickfolioVersionObject getVersion() {
        return version;
    }

    public void setVersion(KickfolioVersionObject version) {
        this.version = version;
    }
}
