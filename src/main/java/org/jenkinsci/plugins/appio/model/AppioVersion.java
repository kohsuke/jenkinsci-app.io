package org.jenkinsci.plugins.appio.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.Expose;

@XmlRootElement
public class AppioVersion {
    @Expose
    private AppioVersionObject version;

    public AppioVersion() {
        super();
    }

    public AppioVersionObject getVersion() {
        return version;
    }

    public void setVersion(AppioVersionObject version) {
        this.version = version;
    }
}
